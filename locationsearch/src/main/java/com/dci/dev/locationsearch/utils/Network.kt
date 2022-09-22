package com.dci.dev.locationsearch.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi


suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        val response = apiCall()
        when (response) {
            null -> error("Something went wrong :(")
            else -> NetworkResult.Success(response)
        }
    } catch (e: Exception) {
        error(e.message ?: e.toString())
    }
}

private fun <T> error(errorMessage: String): NetworkResult<T> =
    NetworkResult.Error("Api call failed $errorMessage")

@RequiresApi(Build.VERSION_CODES.M)
fun hasInternetConnection(context: Context?): Boolean {
    if (context == null)
        return false
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager

    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    return when {
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}


sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
}