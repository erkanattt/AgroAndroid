package kz.agrosfera.app.ui.diseases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentCategoryDiseasesBinding
import kz.agrosfera.app.domain.plant.DiseaseCategoryCatalog
import kz.agrosfera.app.domain.plant.PlantDiseaseCatalog

class CategoryDiseasesFragment : Fragment() {

    private var _binding: FragmentCategoryDiseasesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategoryDiseasesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryId = requireArguments().getString(DiseaseNavArgs.CATEGORY_ID).orEmpty()
        val category = DiseaseCategoryCatalog.byId(categoryId)
        if (category == null) {
            findNavController().navigateUp()
            return
        }
        binding.textCategoryTitle.text = category.name
        val diseases = PlantDiseaseCatalog.byCategory(categoryId)
        val adapter = DiseaseAdapter { disease ->
            findNavController().navigate(
                R.id.action_disease_list_to_detail,
                Bundle().apply { putString(DiseaseNavArgs.DISEASE_ID, disease.id) },
            )
        }
        binding.recyclerDiseases.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDiseases.adapter = adapter
        adapter.submitList(diseases)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
