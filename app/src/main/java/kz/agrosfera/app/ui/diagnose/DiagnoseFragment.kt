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
import kotlinx.coroutines.launch
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.BuildConfig
import kz.agrosfera.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kz.agrosfera.app.data.remote.DiseaseApiException
import kz.agrosfera.app.databinding.FragmentDiagnoseBinding

class DiagnoseFragment : Fragment() {

    private var _binding: FragmentDiagnoseBinding? = null
    private val binding get() = _binding!!

    private var selectedUri: Uri? = null
    private var cameraUri: Uri? = null

    private val pickVisualMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) onImagePicked(uri) }

    /** Барлық телефон/эмуляторда жұмыс істейді */
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

    private fun checkServerStatus() {
        val app = requireContext().applicationContext as AgroApp
        viewLifecycleOwner.lifecycleScope.launch {
            val online = withContext(Dispatchers.IO) { app.diseaseApiClient.pingHealth() }
            binding.textDemoNote.text = if (online) {
                getString(R.string.diagnosis_server_online, BuildConfig.API_BASE_URL)
            } else {
                getString(R.string.diagnosis_server_offline, BuildConfig.API_BASE_URL)
            }
        }
    }

    private fun onImagePicked(uri: Uri) {
        selectedUri = uri
        binding.imagePreview.setImageURI(uri)
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
        viewLifecycleOwner.lifecycleScope.launch {
            binding.btnAnalyze.isEnabled = false
            binding.progressAnalyze.isVisible = true
            binding.cardResult.isVisible = false
            try {
                val (bytes, filename) = readImageBytes(uri)
                val result = app.predictDiseaseUseCase.predict(bytes, filename)
                binding.cardResult.isVisible = true
                binding.textDiseaseName.text = result.displayName
                binding.textConfidence.isVisible = result.confidencePercent != null
                if (result.confidencePercent != null) {
                    binding.textConfidence.text = getString(
                        R.string.diagnosis_confidence,
                        result.confidencePercent,
                    )
                }
                binding.textSymptoms.text = result.symptoms
                binding.textPrevention.text = result.prevention
            } catch (e: DiseaseApiException) {
                val messageRes = when (e.message) {
                    "server_unreachable" -> R.string.diagnosis_error_server
                    "server_timeout" -> R.string.diagnosis_error_timeout
                    "empty_image" -> R.string.need_image
                    else -> R.string.diagnosis_error_generic
                }
                Snackbar.make(binding.root, messageRes, Snackbar.LENGTH_LONG).show()
            } catch (_: Exception) {
                Snackbar.make(binding.root, R.string.diagnosis_error_generic, Snackbar.LENGTH_LONG).show()
            } finally {
                binding.progressAnalyze.isVisible = false
                binding.btnAnalyze.isEnabled = true
            }
        }
    }

    private fun readImageBytes(uri: Uri): Pair<ByteArray, String> {
        val ctx = requireContext()
        val bytes = ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("cannot_read_image")
        val name = ctx.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                cursor.getString(nameIndex)
            } else {
                null
            }
        } ?: uri.lastPathSegment ?: "leaf.jpg"
        return bytes to name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
