package kz.agrosfera.app.ui.knowledge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentKnowledgeBinding
import kz.agrosfera.app.domain.plant.DiseaseCategoryCatalog
import kz.agrosfera.app.domain.plant.PlantDisease
import kz.agrosfera.app.domain.plant.PlantDiseaseCatalog
import kz.agrosfera.app.ui.diseases.DiseaseAdapter
import kz.agrosfera.app.ui.diseases.DiseaseNavArgs

class KnowledgeFragment : Fragment() {

    private var _binding: FragmentKnowledgeBinding? = null
    private val binding get() = _binding!!

    private var filterCategory: String? = null
    private val adapter = DiseaseAdapter { disease -> openDetail(disease) }

    private val vegetableCategories = setOf("tomato", "cucumber", "potato", "pepper", "cabbage", "carrot")
    private val fruitCategories = setOf("apple", "grape")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentKnowledgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerDiseases.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDiseases.adapter = adapter

        binding.inputSearch.addTextChangedListener { applyFilter() }

        binding.chipAll.setOnClickListener {
            filterCategory = null
            applyFilter()
        }
        binding.chipVegetables.setOnClickListener {
            filterCategory = "vegetables"
            applyFilter()
        }
        binding.chipFruit.setOnClickListener {
            filterCategory = "fruit"
            applyFilter()
        }

        applyFilter()
    }

    private fun applyFilter() {
        val query = binding.inputSearch.text?.toString().orEmpty().trim().lowercase()
        var list = PlantDiseaseCatalog.diseases
        when (filterCategory) {
            "vegetables" -> list = list.filter { it.categoryId in vegetableCategories }
            "fruit" -> list = list.filter { it.categoryId in fruitCategories }
        }
        if (query.isNotEmpty()) {
            list = list.filter {
                it.name.lowercase().contains(query) ||
                    it.summary.lowercase().contains(query) ||
                    categoryName(it.categoryId).lowercase().contains(query)
            }
        }
        adapter.submitList(list)
    }

    private fun categoryName(id: String): String =
        DiseaseCategoryCatalog.byId(id)?.name ?: id

    private fun openDetail(disease: PlantDisease) {
        findNavController().navigate(
            R.id.action_knowledge_to_detail,
            bundleOf(DiseaseNavArgs.DISEASE_ID to disease.id),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
