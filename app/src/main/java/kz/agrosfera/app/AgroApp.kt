package kz.agrosfera.app

import android.app.Application
import kz.agrosfera.app.data.auth.AuthRepositoryImpl
import kz.agrosfera.app.data.local.AppDatabase
import kz.agrosfera.app.data.local.DiagnosisRepositoryImpl
import kz.agrosfera.app.data.plant.DiseaseRepositoryImpl
import kz.agrosfera.app.data.remote.DiseaseApiClient
import kz.agrosfera.app.data.remote.GeminiApiClient
import kz.agrosfera.app.domain.auth.AuthRepository
import kz.agrosfera.app.domain.chat.ChatRepository
import kz.agrosfera.app.domain.diagnosis.DiagnosisRepository
import kz.agrosfera.app.domain.plant.PredictDiseaseUseCase

class AgroApp : Application() {

    lateinit var authRepository: AuthRepository
    lateinit var predictDiseaseUseCase: PredictDiseaseUseCase
    lateinit var diagnosisRepository: DiagnosisRepository
    lateinit var chatRepository: ChatRepository

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.get(this)
        authRepository = AuthRepositoryImpl(this)
        diagnosisRepository = DiagnosisRepositoryImpl(db.diagnosisDao())
        predictDiseaseUseCase = PredictDiseaseUseCase(
            DiseaseRepositoryImpl(DiseaseApiClient()),
        )
        chatRepository = ChatRepository(GeminiApiClient())
    }
}
