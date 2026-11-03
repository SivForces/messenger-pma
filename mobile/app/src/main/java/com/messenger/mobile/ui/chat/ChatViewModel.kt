package com.messenger.mobile.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.messenger.mobile.data.AppContainer
import com.messenger.mobile.data.MessageDto
import com.messenger.mobile.data.SendMessageRequest
import com.messenger.mobile.data.container
import com.messenger.mobile.data.toUserMessage
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ChatViewModel(private val container: AppContainer, private val chatId: Long) : ViewModel() {
    var messages by mutableStateOf<List<MessageDto>>(emptyList()); private set
    var input by mutableStateOf("")
    var error by mutableStateOf<String?>(null); private set

    suspend fun poll() {
        try {
            val lastId = messages.lastOrNull()?.id
            val fresh = container.api.getMessages(chatId, lastId, 50)
            if (fresh.isNotEmpty()) {
                messages = (messages + fresh).distinctBy { it.id }.sortedBy { it.id }
            }
            error = null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = e.toUserMessage()
        }
    }

    fun send() {
        val text = input.trim()
        if (text.isEmpty()) return
        input = ""
        viewModelScope.launch {
            try {
                container.api.sendMessage(chatId, SendMessageRequest(text))
                poll()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                input = text
                error = e.toUserMessage()
            }
        }
    }

    companion object {
        fun factory(chatId: Long) = viewModelFactory {
            initializer { ChatViewModel(container(), chatId) }
        }
    }
}