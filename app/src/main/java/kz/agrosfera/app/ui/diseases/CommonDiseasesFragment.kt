package kz.agrosfera.app.ui.diseases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentCommonDiseasesBinding
import kz.agrosfera.app.domain.plant.PlantDiseaseCatalog

class CommonDiseasesFragment : Fragment() {

    private var _binding: FragmentCommonDiseasesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommonDiseasesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = DiseaseAdapter { disease ->
            val message = buildString {
                append(disease.symptoms)
                append("\n\n")
                append(getString(R.string.diagnosis_prevention))
                append("\n")
                append(disease.prevention)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(disease.name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
        binding.recyclerDiseases.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDiseases.adapter = adapter
        adapter.submitList(PlantDiseaseCatalog.diseases)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
