package com.messenger.mobile.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.messenger.mobile.data.MeResponse
import com.messenger.mobile.ui.common.Avatar

@Composable
fun ProfileScreen(me: MeResponse?, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Avatar(me?.displayName ?: "?", 120.dp)
        Spacer(Modifier.height(12.dp))
        Text(me?.displayName.orEmpty(), style = MaterialTheme.typography.headlineMedium)
        Text(
            me?.let { "@${it.username}" }.orEmpty(),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}