var state_arr = new Array(
	"Almaty Region", //0 
	/*"Akmola Region", //1
	"Aktobe Region", //2
	"Atyrau Region", //3
	"Kostanay Region", //4
	"Karaganda Region", //5
	"Kyzylorda Region", //6
	"Pavlodar Region", //7
	"Petropavl Region", //8
	"Shymkent Region",//9
	"Uralsk Region",//10
	"Zhambyl Region"*/
	) //11

var s_a = new Array();
s_a[0]="";
s_a[1]=" Almaty | Talgar | Zharkent";
s_a[2]=" Astana | Kokshetau | Shuchinsk | Makinsk";
s_a[3]=" Aktobe | Khromtau";
s_a[4]=" Atyrau | Kulsary";
s_a[5]=" Kostanay | Rudny | Lisakovsk";
s_a[6]=" Karaganda | Jezkazgan | Satpayev";
s_a[7]=" Kyzylorda | Aral";
s_a[8]=" Pavlodar | Aksu | Ekibastuz";
s_a[9]=" Petropavl";
s_a[10]=" Shymkent";
s_a[11]=" Uralsk";
s_a[12]=" Taraz";

function print_state(state_id){
	// given the id of the <select> tag as function argument, it inserts <option> tags
	var option_str = document.getElementById(state_id);
	option_str.length=0;
	option_str.options[0] = new Option('выберите область','');
	option_str.selectedIndex = 0;
	for (var i=0; i<state_arr.length; i++) {
		option_str.options[option_str.length] = new Option(state_arr[i],state_arr[i]);
	}
}

function print_city(city_id, city_index){
	var option_str = document.getElementById(city_id);
	option_str.length=0;	// Fixed by Julian Woods
	option_str.options[0] = new Option('выберите город/район','');
	option_str.selectedIndex = 0;
	var city_arr = s_a[city_index].split("|");
	for (var i=0; i<city_arr.length; i++) {
		option_str.options[option_str.length] = new Option(city_arr[i],city_arr[i]);
	}
}
