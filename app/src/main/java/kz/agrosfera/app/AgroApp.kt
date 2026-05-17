package kz.agrosfera.app

import android.app.Application
import kz.agrosfera.app.data.auth.AuthRepositoryImpl
import kz.agrosfera.app.data.plant.DiseaseRepositoryImpl
import kz.agrosfera.app.data.remote.DiseaseApiClient
import kz.agrosfera.app.domain.auth.AuthRepository
import kz.agrosfera.app.domain.plant.PredictDiseaseUseCase

class AgroApp : Application() {

    lateinit var authRepository: AuthRepository

    lateinit var predictDiseaseUseCase: PredictDiseaseUseCase
    lateinit var diseaseApiClient: DiseaseApiClient

    override fun onCreate() {
        super.onCreate()
        authRepository = AuthRepositoryImpl(this)
        diseaseApiClient = DiseaseApiClient(BuildConfig.API_BASE_URL)
        predictDiseaseUseCase = PredictDiseaseUseCase(DiseaseRepositoryImpl(diseaseApiClient))
    }
}
