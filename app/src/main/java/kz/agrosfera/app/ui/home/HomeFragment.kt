package kz.agrosfera.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kz.agrosfera.app.MainActivity
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentHomeBinding

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
        binding.btnCheckPlant.setOnClickListener {
            (requireActivity() as MainActivity).selectTab(R.id.nav_check)
        }
        binding.btnGoDiseases.setOnClickListener {
            (requireActivity() as MainActivity).selectTab(R.id.nav_field)
        }
        binding.btnGoKnowledge.setOnClickListener {
            (requireActivity() as MainActivity).selectTab(R.id.nav_knowledge)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
