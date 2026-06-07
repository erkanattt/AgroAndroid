package kz.agrosfera.app.ui.plants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentPlantsBinding
import kz.agrosfera.app.domain.plant.GardenPlantCatalog

class PlantsFragment : Fragment() {

    private var _binding: FragmentPlantsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerGarden.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerGarden.adapter = GardenPlantAdapter(GardenPlantCatalog.plants)
        binding.btnAddPlant.setOnClickListener {
            Snackbar.make(binding.root, R.string.garden_add_soon, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
