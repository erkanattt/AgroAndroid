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
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { (requireContext().applicationContext as AgroApp).authRepository }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener {
            val name = binding.inputName.text?.toString().orEmpty()
            val email = binding.inputEmail.text?.toString().orEmpty()
            val password = binding.inputPassword.text?.toString().orEmpty()
            val confirm = binding.inputPasswordConfirm.text?.toString().orEmpty()
            if (name.isBlank() || email.isBlank() || password.isBlank() || confirm.isBlank()) {
                Snackbar.make(binding.root, R.string.auth_error_empty, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirm) {
                Snackbar.make(binding.root, R.string.auth_error_password_mismatch, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                auth.register(name, email, password)
                Snackbar.make(binding.root, R.string.auth_success_register, Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
