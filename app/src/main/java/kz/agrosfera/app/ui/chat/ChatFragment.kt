package kz.agrosfera.app.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentChatBinding
import kz.agrosfera.app.domain.chat.ChatMessage
import kz.agrosfera.app.ui.auth.AuthNavArgs
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val adapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireContext().applicationContext as AgroApp
        binding.recyclerChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.recyclerChat.adapter = adapter

        binding.panelLocked.btnLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_chat_to_login,
                bundleOf(
                    AuthNavArgs.REDIRECT_CHAT to true,
                    AuthNavArgs.REDIRECT_TAB to R.id.nav_chat,
                ),
            )
        }
        binding.panelLocked.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.authRepository.session.collect { session ->
                    val loggedIn = session != null
                    binding.panelLocked.root.isVisible = !loggedIn
                    binding.panelChat.isVisible = loggedIn
                }
            }
        }

        showInitialMessages(app)
        binding.btnSend.setOnClickListener { sendMessage() }
        binding.inputMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun showInitialMessages(app: AgroApp) {
        if (!app.chatRepository.isConfigured()) {
            adapter.submit(
                listOf(ChatMessage(getString(R.string.chat_gemini_missing), isUser = false)),
            )
            return
        }
        if (app.chatRepository.messages().isEmpty()) {
            adapter.submit(
                listOf(ChatMessage(getString(R.string.chat_welcome), isUser = false)),
            )
        } else {
            refreshMessages(app)
        }
    }

    private fun sendMessage() {
        val text = binding.inputMessage.text?.toString().orEmpty().trim()
        if (text.isBlank()) return

        val app = requireContext().applicationContext as AgroApp
        if (!app.chatRepository.isConfigured()) {
            Snackbar.make(binding.root, R.string.chat_gemini_missing, Snackbar.LENGTH_LONG).show()
            return
        }

        binding.inputMessage.text?.clear()
        binding.progressChat.isVisible = true
        binding.btnSend.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            val result = app.chatRepository.send(text)
            withBinding { b ->
                b.progressChat.isVisible = false
                b.btnSend.isEnabled = true
                result.fold(
                    onSuccess = {
                        refreshMessages(app)
                        scrollToBottom(b)
                    },
                    onFailure = { e ->
                        refreshMessages(app)
                        Snackbar.make(b.root, mapChatError(e), Snackbar.LENGTH_LONG).show()
                    },
                )
            }
        }
    }

    private fun mapChatError(error: Throwable): String {
        val code = error.message.orEmpty()
        return when (code) {
            "gemini_key_missing" -> getString(R.string.chat_gemini_missing)
            "gemini_network_host", "gemini_network_connect" ->
                getString(R.string.chat_error_no_internet)
            "gemini_network_timeout" -> getString(R.string.chat_error_timeout)
            "gemini_api_busy" -> getString(R.string.chat_error_busy)
            "gemini_api_forbidden" -> getString(R.string.chat_error_api_key)
            "gemini_model_deprecated" -> getString(R.string.chat_error_model)
            "gemini_empty" -> getString(R.string.chat_error_empty)
            else -> when {
                code.contains("Unable to resolve host", ignoreCase = true) ||
                    code.contains("Unable to connect", ignoreCase = true) ->
                    getString(R.string.chat_error_no_internet)
                code.contains("high demand", ignoreCase = true) ->
                    getString(R.string.chat_error_busy)
                code.contains("no longer available", ignoreCase = true) ->
                    getString(R.string.chat_error_model)
                code.startsWith("gemini_http_") ->
                    getString(R.string.chat_error_network)
                else -> getString(R.string.chat_error, code.ifBlank { "?" })
            }
        }
    }

    private fun refreshMessages(app: AgroApp) {
        adapter.submit(app.chatRepository.messages())
    }

    private fun scrollToBottom(b: FragmentChatBinding) {
        if (adapter.itemCount > 0) {
            b.recyclerChat.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private inline fun withBinding(block: (FragmentChatBinding) -> Unit) {
        _binding?.let(block)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
