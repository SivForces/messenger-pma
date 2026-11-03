package com.messenger.mobile.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.messenger.mobile.MessengerApp
import com.messenger.mobile.data.AppContainer
import com.messenger.mobile.data.LoginRequest
import com.messenger.mobile.data.RegisterRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

enum class SessionState { Loading, LoggedOut, LoggedIn }

class AuthViewModel(private val container: AppContainer) : ViewModel() {

    var session by mutableStateOf(SessionState.Loading); private set
    var isBusy by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    init {
        viewModelScope.launch {
            val saved = container.tokenStore.token.first()
            if (saved != null) {
                container.sessionToken = saved
                session = SessionState.LoggedIn
            } else {
                session = SessionState.LoggedOut
            }
        }
    }

    fun submit(isRegister: Boolean, username: String, displayName: String, password: String, remember: Boolean) {
        if (isBusy) return
        viewModelScope.launch {
            isBusy = true
            error = null
            try {
                val response = if (isRegister)
                    container.api.register(
                        RegisterRequest(
                            username.trim(),
                            displayName.trim(),
                            password
                        )
                    )
                else
                    container.api.login(LoginRequest(username.trim(), password))

                container.sessionToken = response.token
                if (remember) container.tokenStore.save(response.token)
                else container.tokenStore.clear()
                session = SessionState.LoggedIn
            } catch (e: HttpException) {
                error = when (e.code()) {
                    401 -> "Not valid username"
                    409 -> "Username is already in use"
                    400 -> "Username or password is not valid"
                    else -> "Server error (${e.code()})"
                }
            } catch (e: IOException) {
                error = "No connection to the server"
            } finally {
                isBusy = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            container.sessionToken = null
            container.tokenStore.clear()
            session = SessionState.LoggedOut
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MessengerApp
                AuthViewModel(app.container)
            }
        }
    }
}