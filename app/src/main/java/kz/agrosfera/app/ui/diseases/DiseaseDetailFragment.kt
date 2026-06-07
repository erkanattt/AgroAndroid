package kz.agrosfera.app.ui.diseases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentDiseaseDetailBinding
import kz.agrosfera.app.domain.plant.PlantDiseaseCatalog

class DiseaseDetailFragment : Fragment() {

    private var _binding: FragmentDiseaseDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiseaseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val diseaseId = requireArguments().getString(DiseaseNavArgs.DISEASE_ID).orEmpty()
        val disease = PlantDiseaseCatalog.byId(diseaseId)
        if (disease == null) {
            findNavController().navigateUp()
            return
        }
        binding.textRank.text = getString(R.string.disease_rank_label, disease.frequencyRank)
        binding.textTitle.text = disease.name
        binding.textSummary.text = disease.summary
        binding.textSymptoms.text = disease.symptoms
        binding.textPrevention.text = disease.prevention
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
