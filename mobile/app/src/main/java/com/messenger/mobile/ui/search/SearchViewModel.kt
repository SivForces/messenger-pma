package com.messenger.mobile.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.messenger.mobile.data.AppContainer
import com.messenger.mobile.data.ChatDto
import com.messenger.mobile.data.CreateChatRequest
import com.messenger.mobile.data.UserSummary
import com.messenger.mobile.data.container
import com.messenger.mobile.data.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SearchViewModel(private val container: AppContainer) : ViewModel() {
    var query by mutableStateOf(""); private set
    var results by mutableStateOf<List<UserSummary>>(emptyList()); private set
    var error by mutableStateOf<String?>(null); private set
    private var searchJob: Job? = null

    fun onQueryChange(q: String) {
        query = q
        searchJob?.cancel()
        if (q.isBlank()) {
            results = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            try {
                results = container.api.searchUsers(q.trim())
                error = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = e.toUserMessage()
            }
        }
    }

    fun open(user: UserSummary, onOpened: (ChatDto) -> Unit) {
        viewModelScope.launch {
            try {
                onOpened(container.api.createChat(CreateChatRequest(user.id)))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = e.toUserMessage()
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { SearchViewModel(container()) }
        }
    }
}