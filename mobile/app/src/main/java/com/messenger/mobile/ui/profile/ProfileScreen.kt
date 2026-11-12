package com.messenger.mobile.ui.profile

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.messenger.mobile.data.MeResponse
import com.messenger.mobile.data.UpdateProfileRequest
import com.messenger.mobile.ui.common.Avatar
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ProfileScreen(
    me: MeResponse?,
    modifier: Modifier = Modifier,
    onSave: suspend (UpdateProfileRequest) -> String?
) {
    var editing by remember { mutableStateOf(false) }

    if (me == null) {
        Box(modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Avatar(me.displayName, 120.dp)
        Spacer(Modifier.height(12.dp))
        Text(me.displayName, style = MaterialTheme.typography.headlineMedium)
        Text("@${me.username}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        if (editing) {
            EditForm(me, onSave, onDone = { editing = false })
        } else {
            InfoRow("Phone", me.phone)
            InfoRow("Bio", me.bio)
            InfoRow("Date of birth", me.birthDate)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { editing = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Change profile settings")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        Text(if (value.isNullOrBlank()) "—" else value)
    }
}

@Composable
private fun EditForm(
    me: MeResponse,
    onSave: suspend (UpdateProfileRequest) -> String?,
    onDone: () -> Unit
) {
    var name by remember { mutableStateOf(me.displayName) }
    var phone by remember { mutableStateOf(me.phone.orEmpty()) }
    var bio by remember { mutableStateOf(me.bio.orEmpty()) }
    var birth by remember { mutableStateOf(me.birthDate.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    OutlinedTextField(name, { name = it }, label = { Text("Name") },
        singleLine = true, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(phone, { phone = it }, label = { Text("Phone") },
        singleLine = true, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(bio, { bio = it }, label = { Text("Bio") },
        maxLines = 4, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(birth, { birth = it },
        label = { Text("Date of birth(YEAR-MM-DD)") },
        singleLine = true, modifier = Modifier.fillMaxWidth())

    error?.let {
        Spacer(Modifier.height(8.dp))
        Text(it, color = MaterialTheme.colorScheme.error)
    }
    Spacer(Modifier.height(16.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = onDone, modifier = Modifier.weight(1f), enabled = !saving) {
            Text("Cancel")
        }
        Button(
            modifier = Modifier.weight(1f),
            enabled = !saving && name.isNotBlank(),
            onClick = {
                val birthText = birth.trim()
                if (birthText.isNotEmpty() && runCatching { LocalDate.parse(birthText) }.isFailure) {
                    error = "Date must be like: YEAR-MM-DD"
                    return@Button
                }
                scope.launch {
                    saving = true
                    val err = onSave(
                        UpdateProfileRequest(
                            name.trim(), phone.trim(), bio.trim(),
                            birthText.ifEmpty { null }
                        )
                    )
                    saving = false
                    if (err == null) onDone() else error = err
                }
            }
        ) { Text("Save") }
    }
}