package com.messenger.mobile.data

import retrofit2.HttpException
import java.io.IOException

fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        401 -> "Time out, log in again"
        else -> "Server error (${code()})"
    }
    is IOException -> "No connection with the server"
    else -> "Something's wrong"
}