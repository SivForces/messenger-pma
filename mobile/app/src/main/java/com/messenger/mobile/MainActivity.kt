package com.messenger.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.messenger.mobile.ui.auth.AuthViewModel
import com.messenger.mobile.ui.auth.LoginScreen
import com.messenger.mobile.ui.auth.SessionState
import com.messenger.mobile.ui.theme.MobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTheme {
                val vm: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
                when (vm.session) {
                    SessionState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    SessionState.LoggedOut -> LoginScreen(vm)
                    SessionState.LoggedIn -> Column(
                        Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally
                    ) {
                        Text("Chats")
                        Button(onClick = { vm.logout() }) { Text("Log out") }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MobileTheme {
        Greeting("Android")
    }
}