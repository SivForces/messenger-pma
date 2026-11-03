package com.messenger.mobile.ui.common

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant

fun formatTime(iso: String): String = try {
    val dt = Instant.parse(iso).atZone(ZoneId.systemDefault())
    if (dt.toLocalDate() == LocalDate.now()) dt.format(DateTimeFormatter.ofPattern("HH:mm"))
    else dt.format(DateTimeFormatter.ofPattern("dd.MM"))
} catch (e: Exception) {
    ""
}