# Importing essential libraries and modules

from flask import Flask, render_template, request, jsonify, make_response, redirect
from markupsafe import Markup
import numpy as np
import pandas as pd
from utils.disease import disease_dic
from utils.fertilizer import fertilizer_dic
import requests
import config
import pickle
import io
import torch
from torchvision import transforms
from PIL import Image
from utils.model import ResNet9
# ==============================================================================================

# -------------------------LOADING THE TRAINED MODELS -----------------------------------------------

# Loading plant disease classification model

disease_classes = ['Apple___Apple_scab',
                   'Apple___Black_rot',
                   'Apple___Cedar_apple_rust',
                   'Apple___healthy',
                   'Blueberry___healthy',
                   'Cherry_(including_sour)___Powdery_mildew',
                   'Cherry_(including_sour)___healthy',
                   'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot',
                   'Corn_(maize)___Common_rust_',
                   'Corn_(maize)___Northern_Leaf_Blight',
                   'Corn_(maize)___healthy',
                   'Grape___Black_rot',
                   'Grape___Esca_(Black_Measles)',
                   'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)',
                   'Grape___healthy',
                   'Orange___Haunglongbing_(Citrus_greening)',
                   'Peach___Bacterial_spot',
                   'Peach___healthy',
                   'Pepper,_bell___Bacterial_spot',
                   'Pepper,_bell___healthy',
                   'Potato___Early_blight',
                   'Potato___Late_blight',
                   'Potato___healthy',
                   'Raspberry___healthy',
                   'Soybean___healthy',
                   'Squash___Powdery_mildew',
                   'Strawberry___Leaf_scorch',
                   'Strawberry___healthy',
                   'Tomato___Bacterial_spot',
                   'Tomato___Early_blight',
                   'Tomato___Late_blight',
                   'Tomato___Leaf_Mold',
                   'Tomato___Septoria_leaf_spot',
                   'Tomato___Spider_mites Two-spotted_spider_mite',
                   'Tomato___Target_Spot',
                   'Tomato___Tomato_Yellow_Leaf_Curl_Virus',
                   'Tomato___Tomato_mosaic_virus',
                   'Tomato___healthy']

disease_model_path = 'models/plant_disease_model.pth'
disease_model = ResNet9(3, len(disease_classes))
disease_model.load_state_dict(torch.load(
    disease_model_path, map_location=torch.device('cpu'), weights_only=False))
disease_model.eval()


# Crop model loads on demand (pickle may need older sklearn; disease API does not need it)
crop_recommendation_model_path = 'models/RandomForest.pkl'
_crop_recommendation_model = None
_crop_model_load_error = None


def get_crop_recommendation_model():
    global _crop_recommendation_model, _crop_model_load_error
    if _crop_recommendation_model is not None:
        return _crop_recommendation_model
    if _crop_model_load_error is not None:
        raise RuntimeError(_crop_model_load_error)
    try:
        with open(crop_recommendation_model_path, 'rb') as f:
            _crop_recommendation_model = pickle.load(f)
        return _crop_recommendation_model
    except Exception as exc:
        _crop_model_load_error = (
            'Crop model unavailable (sklearn version mismatch?). '
            'Disease detection still works. Details: ' + str(exc)
        )
        raise RuntimeError(_crop_model_load_error) from exc


# =========================================================================================

# Custom functions for calculations


def weather_fetch(city_name):
    """
    Fetch and returns the temperature and humidity of a city
    :params: city_name
    :return: temperature, humidity
    """
    api_key = config.weather_api_key
    base_url = "http://api.openweathermap.org/data/2.5/weather?"

    complete_url = base_url + "appid=" + api_key + "&q=" + city_name+"&units=metric"
    response = requests.get(complete_url)
    x = response.json()

    if x["cod"] != "404":
        y = x["main"]

        temperature = y["temp"] #round((y["temp"] - 273.15), 2)
        humidity = y["humidity"]
        pressure = y["pressure"]
        return temperature, humidity, pressure
    else:
        return None


def predict_image(img, model=disease_model):
    """
    Transforms image to tensor and predicts disease label
    :params: image
    :return: prediction (string)
    """
    transform = transforms.Compose([
        transforms.Resize(256),
        transforms.ToTensor(),
    ])
    image = Image.open(io.BytesIO(img))
    img_t = transform(image)
    img_u = torch.unsqueeze(img_t, 0)

    # Get predictions from model
    yb = model(img_u)
    probs = torch.nn.functional.softmax(yb, dim=1)
    conf, preds = torch.max(probs, dim=1)
    prediction = disease_classes[preds[0].item()]
    confidence_percent = int(round(conf[0].item() * 100))
    return prediction, confidence_percent

# Локализация HTML для страницы урожая и JSON API (agroAndroid / др.)
CROP_LOCALIZED = {
    'rice': 'рись',
    'maize': 'maize',
    'chickpea': 'chickpea',
    'kidneybeans': 'рекомендуется <u>фасоль почечная</u><br>это вид бобового растения, который широко распространен в мировом сельском хозяйстве и широко используется в пищевой промышленности и домашнем приготовлении пищи. Она известна своими вкусными и питательными стручками, содержащими съедобные семена.<br><br><img src="/static/images/kidneybeans.jpg" width="500" />',
    'pigeonpeas': 'рекомендуется <u>голубиный горох</u><br>Хотя голубиный горох является многолетним растением, часто его возделывают как однолетнее. Размножают семенами. Растение теплолюбивое, оптимальная температура для его роста 25—27 °С, при температуре ниже 15 °С рост приостанавливается. Засухоустойчив, хорошо растёт не только во влажных субтропиках, но и в регионах, где сумма годовых осадков составляет около 400 мм. К почвам неприхотлив, выносит слабое засоление.<br><br><img src="/static/images/goroh.jpg" width="500" />',
    'mothbeans': 'mothbeans',
    'mungbean': 'mungbean',
    'blackgram': 'blackgram',
    'lentil': 'lentil',
    'pomegranate': 'pomegranate',
    'banana': 'banana',
    'mango': 'рекомендуется <u>манго</u><br>Тропическому растению в помещении понадобится много света и стабильная плюсовая температура. Плоды на дереве не завязываются, если температура опускается ниже +12 °С. К почве особых требований у культуры нет.<br><img src="/static/images/mango.jpeg" width="500" />',
    'grapes': 'grapes',
    'watermelon': 'watermelon',
    'muskmelon': 'muskmelon',
    'apple': 'apple',
    'orange': 'orange',
    'papaya': 'papaya',
    'coconut': 'рекомендуется <u>кокосовый орех</u><br>Высокая (до 27—30 м) стройная пальма. Ствол — 15—45 см в диаметре (у основания до 60 см), гладкий, в кольцах от опавших листьев, слегка наклонён и расширен у основания.<br>Плоды растут группами по 15 — 20 штук, полностью созревая в течение восьми — десяти месяцев. В культуре дерево начинает плодоносить с семи — девяти лет и продолжает около 50 лет. Одно дерево ежегодно даёт от 60 до 200 плодов.<br><img src="/static/images/kokos.jpeg" width="500" />',
    'cotton': 'cotton',
    'jute': 'jute',
    'coffee': 'coffee',
}


def fertilizer_logic(crop_name, N, P, K):
    """Возвращает ключ советa (NHigh, ...) и текст из fertilizer_dic."""
    df = pd.read_csv('Data/fertilizer.csv')
    nr = df[df['Crop'] == crop_name]['N'].iloc[0]
    pr = df[df['Crop'] == crop_name]['P'].iloc[0]
    kr = df[df['Crop'] == crop_name]['K'].iloc[0]
    n = nr - N
    p = pr - P
    k = kr - K
    temp = {abs(n): "N", abs(p): "P", abs(k): "K"}
    max_value = temp[max(temp.keys())]
    if max_value == "N":
        key = 'NHigh' if n < 0 else "Nlow"
    elif max_value == "P":
        key = 'PHigh' if p < 0 else "Plow"
    else:
        key = 'KHigh' if k < 0 else "Klow"
    return key, str(fertilizer_dic[key])

# ===============================================================================================
# ------------------------------------ FLASK APP -------------------------------------------------


app = Flask(__name__)

# render home page


@ app.route('/')
def home():
    title = 'SE Disser - Главная страница'
    return render_template('index.html', title=title)

# render crop recommendation form page


@ app.route('/urojai')
def crop_recommend():
    title = 'SE Disser - Рекомендация по культуре'
    return render_template('crop.html', title=title)

# render fertilizer recommendation form page


@ app.route('/udobrenie')
def fertilizer_recommendation():
    title = 'SE Disser - Предложение по удобрению'

    return render_template('fertilizer.html', title=title)

# render disease prediction input page




# ===============================================================================================

# RENDER PREDICTION PAGES

# render crop recommendation result page


@ app.route('/urojai', methods=['POST'])
def crop_prediction():
    title = 'SE Disser — Рекомендация по культуре'

    if request.method == 'POST':
        N = int(request.form['nitrogen'])
        P = int(request.form['phosphorous'])
        K = int(request.form['pottasium'])
        ph = float(request.form['ph'])
        rainfall = float(request.form['rainfall'])
        t = float(request.form['t'])
        h = float(request.form['h'])      

        try:
            data = np.array([[N, P, K, t, h, ph, rainfall]])
            my_prediction = get_crop_recommendation_model().predict(data)
            final_prediction = my_prediction[0]
            final_prediction = Markup(CROP_LOCALIZED[final_prediction])

            return render_template('crop-result.html', prediction=final_prediction, title=title)
        except:
            return render_template('try_again.html', title=title)

# render fertilizer recommendation result page


@ app.route('/udobrenie-predict', methods=['POST'])
def fert_recommend():
    title = 'SE Disser - Предложение по удобрению'

    crop_name = str(request.form['cropname'])
    N = int(request.form['nitrogen'])
    P = int(request.form['phosphorous'])
    K = int(request.form['pottasium'])
    _, advice_text = fertilizer_logic(crop_name, N, P, K)
    response = Markup(advice_text)

    return render_template('fertilizer-result.html', recommendation=response, title=title)

# render disease prediction result page


@app.route('/bolezn-predict', methods=['GET', 'POST'])
def disease_prediction():
    title = 'SE Disser — Обнаружение болезней'

    if request.method == 'POST':
        if 'file' not in request.files:
            return redirect(request.url)
        file = request.files.get('file')
        if not file:
            return render_template('disease.html', title=title)
        try:
            img = file.read()

            prediction, _confidence = predict_image(img)

            prediction = Markup(str(disease_dic[prediction]))
            return render_template('disease-result.html', prediction=prediction, title=title)
        except:
            pass
    return render_template('disease.html', title=title)

@app.route('/weather', methods=['POST'])
def get_weaher_data():

    city_name = request.form['city_name']

    data = weather_fetch(city_name)
    if data is None:
        return make_response(jsonify({'error': 'city not found'}), 404)
    return make_response(jsonify(data), 200)


# ---------- JSON API для Android (agroAndroid) и других клиентов ----------
@app.route('/api/v1/health', methods=['GET'])
def api_health():
    return jsonify(ok=True, service='agro-disease-api')


@app.after_request
def _cors_api(response):
    if request.path.startswith('/api/'):
        response.headers['Access-Control-Allow-Origin'] = '*'
        response.headers['Access-Control-Allow-Headers'] = 'Content-Type'
        response.headers['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS'
    return response


@app.route('/api/v1/crop', methods=['POST', 'OPTIONS'])
def api_crop():
    if request.method == 'OPTIONS':
        return '', 204
    body = request.get_json(silent=True) or {}
    try:
        N = int(body['nitrogen'])
        P = int(body['phosphorous'])
        K = int(body['pottasium'])
        ph = float(body['ph'])
        rainfall = float(body['rainfall'])
        t = float(body['t'])
        h = float(body['h'])
    except (KeyError, TypeError, ValueError):
        return jsonify(
            ok=False,
            error='JSON: nitrogen, phosphorous, pottasium, ph, rainfall, t, h'
        ), 400
    try:
        arr = np.array([[N, P, K, t, h, ph, rainfall]])
        crop_id = get_crop_recommendation_model().predict(arr)[0]
        return jsonify(
            ok=True,
            crop=crop_id,
            description_html=CROP_LOCALIZED[crop_id],
        )
    except Exception as exc:
        return jsonify(ok=False, error=str(exc)), 400


@app.route('/api/v1/fertilizer', methods=['POST', 'OPTIONS'])
def api_fertilizer():
    if request.method == 'OPTIONS':
        return '', 204
    body = request.get_json(silent=True) or {}
    try:
        crop_name = str(body['cropname'])
        N = int(body['nitrogen'])
        P = int(body['phosphorous'])
        K = int(body['pottasium'])
    except (KeyError, TypeError, ValueError):
        return jsonify(
            ok=False,
            error='JSON: cropname, nitrogen, phosphorous, pottasium',
        ), 400
    try:
        key, text = fertilizer_logic(crop_name, N, P, K)
        return jsonify(ok=True, advice_key=key, description_html=text)
    except Exception as exc:
        return jsonify(ok=False, error=str(exc)), 400


@app.route('/api/v1/disease', methods=['POST', 'OPTIONS'])
def api_disease():
    if request.method == 'OPTIONS':
        return '', 204
    if 'file' not in request.files:
        return jsonify(ok=False, error='multipart field "file" required'), 400
    file = request.files.get('file')
    if not file or file.filename == '':
        return jsonify(ok=False, error='empty file'), 400
    try:
        img = file.read()
        if not img:
            return jsonify(ok=False, error='empty image bytes'), 400
        print(f'[disease] request from {request.remote_addr}, '
              f'bytes={len(img)}, name={file.filename!r}')
        cls, confidence_percent = predict_image(img)
        print(f'[disease] result={cls!r} confidence={confidence_percent}%')
        return jsonify(
            ok=True,
            class_id=cls,
            confidence_percent=confidence_percent,
            description_html=disease_dic[cls],
        )
    except Exception as exc:
        return jsonify(ok=False, error=str(exc)), 400


@app.route('/api/v1/weather', methods=['POST', 'OPTIONS'])
def api_weather():
    if request.method == 'OPTIONS':
        return '', 204
    body = request.get_json(silent=True) or {}
    city_name = body.get('city_name')
    if not city_name:
        return jsonify(ok=False, error='city_name required'), 400
    data = weather_fetch(city_name)
    if data is None:
        return jsonify(ok=False, error='city not found'), 404
    temperature, humidity, pressure = data
    return jsonify(
        ok=True,
        temperature=temperature,
        humidity=humidity,
        pressure=pressure,
    )


# ===============================================================================================
if __name__ == '__main__':
    # 0.0.0.0 — эмулятор (10.0.2.2) және Wi‑Fi телефон үшін
    app.run(host='0.0.0.0', port=5000, debug=True)
