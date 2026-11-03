package com.messenger.mobile.ui.chats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.messenger.mobile.data.AppContainer
import com.messenger.mobile.data.ChatDto
import com.messenger.mobile.data.container
import com.messenger.mobile.data.toUserMessage
import kotlin.coroutines.cancellation.CancellationException

class ChatsViewModel(private val container: AppContainer) : ViewModel() {
    var chats by mutableStateOf<List<ChatDto>>(emptyList()); private set
    var isLoading by mutableStateOf(true); private set
    var error by mutableStateOf<String?>(null); private set
    var query by mutableStateOf("")

    val visibleChats: List<ChatDto>
        get() {
            val q = query.trim()
            if (q.isEmpty()) return chats
            return chats.filter {
                it.partner.displayName.contains(q, ignoreCase = true) ||
                        it.partner.username.contains(q, ignoreCase = true) ||
                        it.lastMessage?.text?.contains(q, ignoreCase = true) == true
            }
        }

    suspend fun refresh() {
        try {
            chats = container.api.getChats()
            error = null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error = e.toUserMessage()
        } finally {
            isLoading = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ChatsViewModel(container()) }
        }
    }
}