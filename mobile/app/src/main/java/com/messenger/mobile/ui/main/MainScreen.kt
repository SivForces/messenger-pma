package com.messenger.mobile.ui.main

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.messenger.mobile.MessengerApp
import com.messenger.mobile.data.MeResponse
import com.messenger.mobile.data.UserSummary
import com.messenger.mobile.ui.chat.ChatScreen
import com.messenger.mobile.ui.chat.ChatViewModel
import com.messenger.mobile.ui.chats.ChatsScreen
import com.messenger.mobile.ui.chats.ChatsViewModel
import com.messenger.mobile.ui.common.Avatar
import com.messenger.mobile.ui.profile.ProfileScreen
import com.messenger.mobile.ui.search.SearchUsersScreen
import com.messenger.mobile.ui.search.SearchViewModel
import com.messenger.mobile.ui.settings.SettingsScreen
import kotlin.coroutines.cancellation.CancellationException

private fun chatRoute(chatId: Long, partner: UserSummary) =
    "chat/$chatId/${partner.id}?name=${Uri.encode(partner.displayName)}"

@Composable
private fun TabScaffold(
    selected: String,
    myName: String?,
    onTab: (String) -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        floatingActionButton = floatingActionButton,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selected == "chats", onClick = { onTab("chats") },
                    icon = { Icon(Icons.Filled.Home, "Chats") }
                )
                NavigationBarItem(
                    selected = selected == "settings", onClick = { onTab("settings") },
                    icon = { Icon(Icons.Filled.Settings, "Settings") }
                )
                NavigationBarItem(
                    selected = selected == "profile", onClick = { onTab("profile") },
                    icon = { Avatar(myName ?: "?", 28.dp) }
                )
            }
        },
        content = content
    )
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val container = (LocalContext.current.applicationContext as MessengerApp).container
    var me by remember { mutableStateOf<MeResponse?>(null) }
    LaunchedEffect(Unit) {
        me = try {
            container.api.me()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    val nav = rememberNavController()
    val onTab: (String) -> Unit = { route ->
        nav.navigate(route) {
            popUpTo("chats") { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = nav, startDestination = "chats") {
        composable("chats") {
            val vm: ChatsViewModel = viewModel(factory = ChatsViewModel.Factory)
            TabScaffold(
                selected = "chats", myName = me?.displayName, onTab = onTab,
                floatingActionButton = {
                    FloatingActionButton(onClick = { nav.navigate("search") }) {
                        Icon(Icons.Filled.Add, "Новий чат")
                    }
                }
            ) { padding ->
                ChatsScreen(vm, Modifier.padding(padding)) { chat ->
                    nav.navigate(chatRoute(chat.id, chat.partner))
                }
            }
        }
        composable("settings") {
            TabScaffold("settings", me?.displayName, onTab) { padding ->
                SettingsScreen(me, onLogout, Modifier.padding(padding))
            }
        }
        composable("profile") {
            TabScaffold("profile", me?.displayName, onTab) { padding ->
                ProfileScreen(me, Modifier.padding(padding))
            }
        }
        composable("search") {
            val vm: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
            SearchUsersScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onOpened = { chat ->
                    nav.navigate(chatRoute(chat.id, chat.partner)) {
                        popUpTo("search") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "chat/{chatId}/{partnerId}?name={name}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.LongType },
                navArgument("partnerId") { type = NavType.LongType },
                navArgument("name") { type = NavType.StringType; defaultValue = "" }
            )
        ) { entry ->
            val chatId = entry.arguments!!.getLong("chatId")
            val partnerId = entry.arguments!!.getLong("partnerId")
            val name = entry.arguments?.getString("name").orEmpty()
            val vm: ChatViewModel = viewModel(
                key = "chat$chatId", factory = ChatViewModel.factory(chatId)
            )
            ChatScreen(vm, partnerId, name, onBack = { nav.popBackStack() })
        }
    }
}