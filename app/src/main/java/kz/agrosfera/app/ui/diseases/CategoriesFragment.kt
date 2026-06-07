package kz.agrosfera.app.ui.diseases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentCategoriesBinding
import kz.agrosfera.app.domain.plant.DiseaseCategoryCatalog

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategories.adapter = CategoryAdapter(DiseaseCategoryCatalog.categories) { category ->
            findNavController().navigate(
                R.id.action_categories_to_disease_list,
                Bundle().apply { putString(DiseaseNavArgs.CATEGORY_ID, category.id) },
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
