package com.uzlov.dating.lavada.data

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class OAuthInterceptor (private val tokenType: String, private val accessToken: String) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .header("Authorization", "$tokenType $accessToken")
            .build()
        try {
            return chain.proceed(request)
        } catch (e: Exception) {
            e.printStackTrace()
            val msg: String
            when (e) {
                is SocketTimeoutException -> {
                    msg = "Timeout - Please check your internet connection"
                }
                is UnknownHostException -> {
                    msg = "Unable to make a connection. Please check your internet"
                }
                is ConnectionShutdownException -> {
                    msg = "Connection shutdown. Please check your internet"
                }
                is IOException -> {
                    msg = "Server is unreachable, please try again later."
                }
                is IllegalStateException -> {
                    msg = "${e.message}"
                }
                else -> {
                    msg = "${e.message}"
                }
            }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(999)
                .message(msg)
                .body("{${e}}".toResponseBody(null)).build()
        }
    }
}

