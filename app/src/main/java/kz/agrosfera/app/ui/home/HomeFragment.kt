package kz.agrosfera.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.MainActivity
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentHomeBinding
import kz.agrosfera.app.domain.plant.GardenPlantCatalog
import kz.agrosfera.app.ui.plants.PlantChipAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerMyPlants.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerMyPlants.adapter = PlantChipAdapter(GardenPlantCatalog.plants)

        binding.btnCheckPlant.setOnClickListener {
            (requireActivity() as MainActivity).selectTab(R.id.nav_check)
        }

        val app = requireContext().applicationContext as AgroApp
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.authRepository.session.collect { session ->
                    binding.textGreeting.text = if (session != null) {
                        getString(R.string.home_greeting_name, session.name)
                    } else {
                        getString(R.string.home_greeting_guest)
                    }
                }
            }
        }
        showLastDiagnosis()
    }

    override fun onResume() {
        super.onResume()
        showLastDiagnosis()
    }

    private fun showLastDiagnosis() {
        val app = requireContext().applicationContext as AgroApp
        val last = app.diagnosisHistoryStore.getLast()
        binding.cardLastDiagnosis.isVisible = last != null
        if (last == null) return
        binding.textLastDiagnosisName.text = last.displayName
        binding.textLastDiagnosisMeta.text = last.confidencePercent?.let {
            getString(R.string.diagnosis_confidence, it)
        } ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
