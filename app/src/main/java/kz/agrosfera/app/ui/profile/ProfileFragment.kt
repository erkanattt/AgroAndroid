package kz.agrosfera.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { (requireContext().applicationContext as AgroApp).authRepository }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_login)
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_register)
        }
        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                auth.logout()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                auth.session.collect { session ->
                    if (session == null) {
                        binding.textGreeting.setText(R.string.profile_guest)
                        binding.textEmail.isVisible = false
                        binding.btnLogin.isVisible = true
                        binding.btnRegister.isVisible = true
                        binding.btnLogout.isVisible = false
                    } else {
                        binding.textGreeting.text = getString(R.string.profile_hello, session.name)
                        binding.textEmail.text = getString(R.string.profile_email_label, session.email)
                        binding.textEmail.isVisible = true
                        binding.btnLogin.isVisible = false
                        binding.btnRegister.isVisible = false
                        binding.btnLogout.isVisible = true
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
