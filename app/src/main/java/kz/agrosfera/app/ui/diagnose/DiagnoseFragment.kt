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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.R
import kz.agrosfera.app.data.remote.DiseaseApiException
import kz.agrosfera.app.databinding.FragmentDiagnoseBinding
import kz.agrosfera.app.util.ImageCompressor

class DiagnoseFragment : Fragment() {

    private var _binding: FragmentDiagnoseBinding? = null
    private val binding get() = _binding!!

    private var selectedUri: Uri? = null
    private var cameraUri: Uri? = null
    private var serverExpanded = false

    private val pickVisualMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) onImagePicked(uri) }

    private val pickImageFromGallery = registerForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri -> if (uri != null) onImagePicked(uri) }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            pickImageFromGallery.launch("image/*")
        } else {
            Snackbar.make(binding.root, R.string.permission_gallery_denied, Snackbar.LENGTH_LONG).show()
        }
    }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success && cameraUri != null) {
            onImagePicked(cameraUri!!)
        }
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            launchCameraInternal()
        } else {
            Snackbar.make(binding.root, R.string.permission_camera_denied, Snackbar.LENGTH_SHORT).show()
        }
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
        binding.editServerUrl.setText(app.diseaseDiagnosisService.currentBaseUrl())

        binding.headerServerToggle.setOnClickListener { toggleServerSettings() }
        binding.btnSaveServer.setOnClickListener { saveServerAndCheck() }
        binding.btnGallery.setOnClickListener { openGallery() }
        binding.imagePreview.setOnClickListener { openGallery() }
        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                launchCameraInternal()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
        binding.btnAnalyze.setOnClickListener { analyze() }
        checkServerStatus()
    }

    private fun toggleServerSettings() {
        serverExpanded = !serverExpanded
        binding.layoutServerBody.isVisible = serverExpanded
        binding.textServerToggle.text = getString(
            if (serverExpanded) R.string.server_collapse else R.string.server_expand,
        )
    }

    private fun saveServerAndCheck() {
        val url = binding.editServerUrl.text?.toString().orEmpty()
        if (url.isBlank()) {
            Snackbar.make(binding.root, R.string.server_url_empty, Snackbar.LENGTH_SHORT).show()
            return
        }
        val app = requireContext().applicationContext as AgroApp
        app.diseaseDiagnosisService.updateBaseUrl(url)
        binding.editServerUrl.setText(app.diseaseDiagnosisService.currentBaseUrl())
        Snackbar.make(binding.root, R.string.server_url_saved, Snackbar.LENGTH_SHORT).show()
        checkServerStatus()
    }

    private fun checkServerStatus() {
        val app = requireContext().applicationContext as AgroApp
        val url = app.diseaseDiagnosisService.currentBaseUrl()
        binding.chipServerStatus.text = getString(R.string.server_status_checking)
        binding.textDemoNote.text = getString(R.string.diagnosis_checking_server, url)
        viewLifecycleOwner.lifecycleScope.launch {
            val online = withContext(Dispatchers.IO) { app.diseaseDiagnosisService.pingHealth() }
            updateServerChip(online)
            binding.textDemoNote.text = if (online) {
                getString(R.string.diagnosis_server_online, url)
            } else {
                getString(R.string.diagnosis_server_offline, url)
            }
        }
    }

    private fun updateServerChip(online: Boolean) {
        binding.chipServerStatus.text = getString(
            if (online) R.string.server_status_online else R.string.server_status_offline,
        )
        binding.chipServerStatus.setBackgroundResource(
            if (online) R.drawable.bg_status_online else R.drawable.bg_status_offline,
        )
        binding.chipServerStatus.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (online) R.color.success else R.color.danger,
            ),
        )
    }

    private fun onImagePicked(uri: Uri) {
        selectedUri = uri
        binding.imagePreview.setImageURI(uri)
        binding.overlayEmptyHint.isVisible = false
        binding.btnAnalyze.isEnabled = true
        binding.cardResult.isVisible = false
        Snackbar.make(binding.root, R.string.image_picked_ready, Snackbar.LENGTH_SHORT).show()
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
        val uri = FileProvider.getUriForFile(
            ctx,
            "${ctx.packageName}.fileprovider",
            file,
        )
        cameraUri = uri
        takePicture.launch(uri)
    }

    private fun analyze() {
        val uri = selectedUri
        if (uri == null) {
            Snackbar.make(binding.root, R.string.need_image, Snackbar.LENGTH_SHORT).show()
            return
        }
        val app = requireContext().applicationContext as AgroApp
        val url = binding.editServerUrl.text?.toString().orEmpty()
        if (url.isNotBlank()) {
            app.diseaseDiagnosisService.updateBaseUrl(url)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnAnalyze.isEnabled = false
            binding.overlayAnalyzing.isVisible = true
            binding.progressAnalyze.isVisible = true
            binding.cardResult.isVisible = false
            try {
                val (bytes, filename) = readImageBytes(uri)
                val result = app.predictDiseaseUseCase.predict(bytes, filename)
                val isHealthy = result.classId.contains("healthy", ignoreCase = true)

                app.diagnosisHistoryStore.save(
                    result.displayName,
                    result.confidencePercent,
                    result.classId,
                )

                binding.cardResult.isVisible = true
                binding.textDiseaseName.text = result.displayName
                binding.textResultBadge.text = getString(
                    if (isHealthy) R.string.result_healthy else R.string.result_disease,
                )
                binding.textResultBadge.setBackgroundResource(
                    if (isHealthy) R.drawable.bg_badge_healthy else R.drawable.bg_badge_disease,
                )
                binding.textResultBadge.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isHealthy) R.color.success else R.color.warning,
                    ),
                )

                binding.textConfidence.isVisible = result.confidencePercent != null
                binding.progressConfidence.isVisible = result.confidencePercent != null
                if (result.confidencePercent != null) {
                    binding.textConfidence.text = getString(
                        R.string.diagnosis_confidence,
                        result.confidencePercent,
                    )
                    binding.progressConfidence.progress = result.confidencePercent
                }
                binding.textSymptoms.text = result.symptoms
                binding.textPrevention.text = result.prevention
            } catch (e: DiseaseApiException) {
                val text = when (e.message) {
                    "server_unreachable" -> getString(R.string.diagnosis_error_server)
                    "server_timeout" -> getString(R.string.diagnosis_error_timeout)
                    "empty_image" -> getString(R.string.need_image)
                    else -> getString(R.string.diagnosis_error_detail, e.message ?: "?")
                }
                Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
                checkServerStatus()
            } catch (_: Exception) {
                Snackbar.make(binding.root, R.string.diagnosis_error_generic, Snackbar.LENGTH_LONG).show()
            } finally {
                binding.overlayAnalyzing.isVisible = false
                binding.progressAnalyze.isVisible = false
                binding.btnAnalyze.isEnabled = true
            }
        }
    }

    private fun readImageBytes(uri: Uri): Pair<ByteArray, String> =
        ImageCompressor.compressForUpload(requireContext(), uri)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
