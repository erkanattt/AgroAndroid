package kz.agrosfera.app.domain.chat

import kz.agrosfera.app.data.remote.GeminiApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestampMs: Long = System.currentTimeMillis(),
)

class ChatRepository(
    private val apiClient: GeminiApiClient,
) {
    private val history = mutableListOf<ChatMessage>()

    fun messages(): List<ChatMessage> = history.toList()

    fun isConfigured(): Boolean = apiClient.isConfigured()

    suspend fun send(userText: String): Result<ChatMessage> = withContext(Dispatchers.IO) {
        val trimmed = userText.trim()
        if (trimmed.isBlank()) return@withContext Result.failure(IllegalArgumentException("empty"))
        history.add(ChatMessage(trimmed, isUser = true))
        try {
            val pairs = history.dropLast(1).map { msg ->
                if (msg.isUser) "user" to msg.text else "model" to msg.text
            }
            val reply = apiClient.sendMessage(trimmed, pairs)
            val botMsg = ChatMessage(reply, isUser = false)
            history.add(botMsg)
            Result.success(botMsg)
        } catch (e: Exception) {
            if (history.lastOrNull()?.isUser == true) {
                history.removeAt(history.lastIndex)
            }
            Result.failure(e)
        }
    }

    fun clear() {
        history.clear()
    }
}
