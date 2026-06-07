package kz.agrosfera.app.ui.treatment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentTreatmentBinding

class TreatmentFragment : Fragment() {

    private var _binding: FragmentTreatmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTreatmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = requireArguments()
        binding.textDiseaseTitle.text = args.getString(ARG_DISEASE).orEmpty()
        binding.textPrevention.text = args.getString(ARG_PREVENTION)
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.treatment_agro_default)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_DISEASE = "diseaseName"
        const val ARG_PREVENTION = "prevention"
    }
}
