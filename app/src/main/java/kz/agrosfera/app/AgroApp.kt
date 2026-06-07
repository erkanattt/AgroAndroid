package kz.agrosfera.app

import android.app.Application
import kz.agrosfera.app.data.auth.AuthRepositoryImpl
import kz.agrosfera.app.data.local.DiagnosisHistoryStore
import kz.agrosfera.app.data.plant.DiseaseDiagnosisService
import kz.agrosfera.app.domain.auth.AuthRepository
import kz.agrosfera.app.domain.plant.PredictDiseaseUseCase

class AgroApp : Application() {

    lateinit var authRepository: AuthRepository
    lateinit var diseaseDiagnosisService: DiseaseDiagnosisService
    lateinit var predictDiseaseUseCase: PredictDiseaseUseCase
    lateinit var diagnosisHistoryStore: DiagnosisHistoryStore

    override fun onCreate() {
        super.onCreate()
        authRepository = AuthRepositoryImpl(this)
        diagnosisHistoryStore = DiagnosisHistoryStore(this)
        diseaseDiagnosisService = DiseaseDiagnosisService(this)
        predictDiseaseUseCase = PredictDiseaseUseCase(diseaseDiagnosisService)
    }
}
