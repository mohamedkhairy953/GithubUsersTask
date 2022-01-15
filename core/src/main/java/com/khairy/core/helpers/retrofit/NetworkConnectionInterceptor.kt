package com.khairy.core.helpers.retrofit
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 *  Internet connection Interceptor
 *
 *  @param appContext Context (Injected)
 */
class NetworkConnectionInterceptor(@ApplicationContext private val appContext: Context) : Interceptor {

    /**
     *  @return isInternetConnected Boolean
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    /**
     *  An implementation of intercept
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            throw IOException("Network is not available")
        } else {
            return chain.proceed(chain.request())
        }
    }
}