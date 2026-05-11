package kz.agrosfera.app.ui.diagnose

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentDiagnoseBinding

class DiagnoseFragment : Fragment() {

    private var _binding: FragmentDiagnoseBinding? = null
    private val binding get() = _binding!!

    private var selectedUri: Uri? = null
    private var cameraUri: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            binding.imagePreview.setImageURI(uri)
            binding.btnAnalyze.isEnabled = true
        }
    }

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success && cameraUri != null) {
            selectedUri = cameraUri
            binding.imagePreview.setImageURI(cameraUri)
            binding.btnAnalyze.isEnabled = true
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
        binding.btnGallery.setOnClickListener {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        }
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
            delay(750)
            val seed = (uri.toString().hashCode().toLong() * 31L) xor System.nanoTime()
            val result = app.predictDiseaseUseCase.predict(seed)
            binding.cardResult.isVisible = true
            binding.textDiseaseName.text = result.disease.name
            binding.textConfidence.text = getString(R.string.diagnosis_confidence, result.confidencePercent)
            binding.textSymptoms.text = result.disease.symptoms
            binding.textPrevention.text = result.disease.prevention
            binding.progressAnalyze.isVisible = false
            binding.btnAnalyze.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
