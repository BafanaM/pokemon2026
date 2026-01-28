package com.example.networkmodule

import com.example.networkmodule.constants.NetworkConstants
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import java.net.ConnectException
import java.io.IOException

object NetworkErrorHandler {
    
    /**
     * Convert an exception to a user-friendly error message.
     */
    fun handleException(exception: Throwable): String {
        return when (exception) {
            is HttpException -> handleHttpException(exception)
            is UnknownHostException -> NetworkConstants.NO_INTERNET_CONNECTION
            is ConnectException -> NetworkConstants.NO_INTERNET_CONNECTION
            is SocketTimeoutException -> NetworkConstants.REQUEST_TIMED_OUT
            is IOException -> NetworkConstants.NETWORK_ERROR.format(exception.message ?: NetworkConstants.UNABLE_TO_CONNECT)
            else -> NetworkConstants.UNEXPECTED_ERROR.format(exception.message ?: NetworkConstants.UNKNOWN_ERROR_OCCURRED)
        }
    }
    
    /**
     * Handle HTTP exceptions with specific error codes and messages.
     */
    private fun handleHttpException(httpException: HttpException): String {
        val code = httpException.code()
        return when (code) {
            HttpURLConnection.HTTP_INTERNAL_ERROR -> NetworkConstants.SERVER_ERROR
            HttpURLConnection.HTTP_GATEWAY_TIMEOUT -> NetworkConstants.SERVER_TIMEOUT
            else -> NetworkConstants.SERVER_ERROR_WITH_CODE.format(code)
        }
    }
}
