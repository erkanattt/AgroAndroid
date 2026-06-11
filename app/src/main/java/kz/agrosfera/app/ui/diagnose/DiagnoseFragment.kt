package kz.agrosfera.app.ui.diagnose

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import java.io.File
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.R
import kz.agrosfera.app.data.remote.DiseaseApiException
import kz.agrosfera.app.databinding.FragmentDiagnoseBinding
import kz.agrosfera.app.domain.plant.DiagnosisResult
import kz.agrosfera.app.ui.auth.AuthNavArgs
import kz.agrosfera.app.util.ImageCompressor

class DiagnoseFragment : Fragment() {

    private var _binding: FragmentDiagnoseBinding? = null
    private val binding get() = _binding!!

    private var selectedUri: Uri? = null
    private var cameraUri: Uri? = null
    private var pendingResult: DiagnosisResult? = null
    private var savedToDb = false

    private val pickVisualMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) onImagePicked(uri) }

    private val pickImageFromGallery = registerForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri -> if (uri != null) onImagePicked(uri) }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) pickImageFromGallery.launch("image/*")
        else Snackbar.make(binding.root, R.string.permission_gallery_denied, Snackbar.LENGTH_LONG).show()
    }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success -> if (success && cameraUri != null) onImagePicked(cameraUri!!) }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) launchCameraInternal()
        else Snackbar.make(binding.root, R.string.permission_camera_denied, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiagnoseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireContext().applicationContext as AgroApp

        binding.btnGallery.setOnClickListener { openGallery() }
        binding.imagePreview.setOnClickListener { openGallery() }
        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            ) launchCameraInternal()
            else requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
        binding.btnAnalyze.setOnClickListener { analyze() }
        binding.btnSave.setOnClickListener { saveResult() }

        binding.panelLocked.btnLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_diagnose_to_login,
                bundleOf(
                    AuthNavArgs.REDIRECT_AI to true,
                    AuthNavArgs.REDIRECT_TAB to R.id.nav_check,
                ),
            )
        }
        binding.panelLocked.btnRegister.setOnClickListener {
            findNavController().navigate(
                R.id.action_diagnose_to_register,
                bundleOf(
                    AuthNavArgs.REDIRECT_AI to true,
                    AuthNavArgs.REDIRECT_TAB to R.id.nav_check,
                ),
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.authRepository.session.collect { session ->
                    val loggedIn = session != null
                    binding.panelLocked.root.isVisible = !loggedIn
                    binding.panelDiagnose.isVisible = loggedIn
                }
            }
        }

    }

    private fun onImagePicked(uri: Uri) {
        selectedUri = uri
        binding.imagePreview.setImageURI(uri)
        binding.overlayEmptyHint.isVisible = false
        binding.btnAnalyze.isEnabled = true
        binding.cardResult.isVisible = false
        pendingResult = null
        savedToDb = false
    }

    private fun openGallery() {
        val ctx = requireContext()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(ctx)
        ) {
            pickVisualMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
            return
        }
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery.launch("image/*")
        } else {
            requestStoragePermission.launch(permission)
        }
    }

    private fun launchCameraInternal() {
        val ctx = requireContext()
        val file = File(ctx.cacheDir, "agro_capture_${System.currentTimeMillis()}.jpg")
        cameraUri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
        takePicture.launch(cameraUri)
    }

    private fun analyze() {
        val uri = selectedUri ?: run {
            Snackbar.make(binding.root, R.string.need_image, Snackbar.LENGTH_SHORT).show()
            return
        }
        val app = requireContext().applicationContext as AgroApp
        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnAnalyze.isEnabled = false
            binding.overlayAnalyzing.isVisible = true
            binding.cardResult.isVisible = false
            savedToDb = false
            try {
                val (bytes, filename) = ImageCompressor.compressForUpload(requireContext(), uri)
                val result = app.predictDiseaseUseCase.predict(bytes, filename)
                pendingResult = result
                showResult(result)
            } catch (e: DiseaseApiException) {
                val text = when (e.message) {
                    "server_unreachable" -> getString(R.string.diagnosis_error_server)
                    "server_timeout" -> getString(R.string.diagnosis_error_timeout)
                    "empty_image" -> getString(R.string.need_image)
                    else -> getString(R.string.diagnosis_error_detail, e.message ?: "?")
                }
                Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
            } catch (_: Exception) {
                Snackbar.make(binding.root, R.string.diagnosis_error_generic, Snackbar.LENGTH_LONG).show()
            } finally {
                binding.overlayAnalyzing.isVisible = false
                binding.btnAnalyze.isEnabled = true
            }
        }
    }

    private fun showResult(result: DiagnosisResult) {
        val isHealthy = result.classId.contains("healthy", ignoreCase = true)
        binding.cardResult.isVisible = true
        binding.textDiseaseName.text = result.displayName
        binding.textResultBadge.text = getString(
            if (isHealthy) R.string.result_healthy else R.string.result_disease,
        )
        binding.textResultBadge.setBackgroundResource(
            if (isHealthy) R.drawable.bg_badge_healthy else R.drawable.bg_badge_disease,
        )
        binding.textSymptoms.text = result.symptoms
        binding.textPrevention.text = result.prevention
        binding.textConfidence.isVisible = result.confidencePercent != null
        binding.progressConfidence.isVisible = result.confidencePercent != null
        if (result.confidencePercent != null) {
            binding.textConfidence.text = getString(
                R.string.diagnosis_confidence,
                result.confidencePercent,
            )
            binding.progressConfidence.progress = result.confidencePercent
        }
        binding.btnSave.isEnabled = !savedToDb
        binding.btnSave.text = getString(R.string.action_save)
    }

    private fun saveResult() {
        val result = pendingResult ?: return
        if (savedToDb) return
        val app = requireContext().applicationContext as AgroApp
        viewLifecycleOwner.lifecycleScope.launch {
            val email = app.authRepository.session.first()?.email
            app.diagnosisRepository.save(
                displayName = result.displayName,
                classId = result.classId,
                confidencePercent = result.confidencePercent,
                symptoms = result.symptoms,
                prevention = result.prevention,
                userEmail = email,
            )
            savedToDb = true
            binding.btnSave.text = getString(R.string.action_saved)
            binding.btnSave.isEnabled = false
            Snackbar.make(binding.root, R.string.diagnosis_saved, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
