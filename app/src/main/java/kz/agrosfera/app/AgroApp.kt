package kz.agrosfera.app

import android.app.Application
import kz.agrosfera.app.data.auth.AuthRepositoryImpl
import kz.agrosfera.app.domain.auth.AuthRepository
import kz.agrosfera.app.domain.plant.PredictDiseaseUseCase

class AgroApp : Application() {

    lateinit var authRepository: AuthRepository

    lateinit var predictDiseaseUseCase: PredictDiseaseUseCase

    override fun onCreate() {
        super.onCreate()
        authRepository = AuthRepositoryImpl(this)
        predictDiseaseUseCase = PredictDiseaseUseCase()
    }
}
