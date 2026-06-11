package kz.agrosfera.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.MainActivity
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { (requireContext().applicationContext as AgroApp).authRepository }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener {
            val email = binding.inputEmail.text?.toString().orEmpty()
            val password = binding.inputPassword.text?.toString().orEmpty()
            if (email.isBlank() || password.isBlank()) {
                Snackbar.make(binding.root, R.string.auth_error_empty, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                val result = auth.login(email, password)
                if (result.isSuccess) {
                    Snackbar.make(binding.root, R.string.auth_success_login, Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                    redirectAfterAuth()
                } else {
                    Snackbar.make(binding.root, R.string.auth_error_invalid, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun redirectAfterAuth() {
        val tab = arguments?.getInt(AuthNavArgs.REDIRECT_TAB, 0) ?: 0
        if (tab != 0) {
            (requireActivity() as MainActivity).selectTab(tab)
            return
        }
        if (arguments?.getBoolean(AuthNavArgs.REDIRECT_AI) == true) {
            (requireActivity() as MainActivity).selectTab(R.id.nav_check)
        } else if (arguments?.getBoolean(AuthNavArgs.REDIRECT_CHAT) == true) {
            (requireActivity() as MainActivity).selectTab(R.id.nav_chat)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
