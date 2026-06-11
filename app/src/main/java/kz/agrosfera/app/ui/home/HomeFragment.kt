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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.MainActivity
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentHomeBinding
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
        val app = requireContext().applicationContext as AgroApp

        binding.recyclerMenu.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerMenu.adapter = HomeMenuAdapter(HomeMenuCatalog.items) { item ->
            when (item.action) {
                R.id.action_calendar, R.id.action_tips ->
                    Snackbar.make(binding.root, R.string.feature_soon, Snackbar.LENGTH_SHORT).show()
                else -> (requireActivity() as MainActivity).selectTab(item.action)
            }
        }

        binding.btnScan.setOnClickListener {
            (requireActivity() as MainActivity).selectTab(R.id.nav_check)
        }

        binding.textSeeAll.setOnClickListener {
            (requireActivity() as MainActivity).selectTab(R.id.nav_check)
        }

        binding.recyclerRecent.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.diagnosisRepository.observeRecent(5).collect { recent ->
                    binding.recyclerRecent.isVisible = recent.isNotEmpty()
                    binding.textRecentEmpty.isVisible = recent.isEmpty()
                    if (recent.isNotEmpty()) {
                        binding.recyclerRecent.adapter = HomeRecentAdapter(recent)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
