package com.mucheng.qingyuan.wallpaper.application

import android.annotation.SuppressLint
import android.app.Application
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AppContext : Application() {

    companion object {
        @JvmStatic
        lateinit var instance: AppContext
            private set

        @JvmStatic
        lateinit var okHttpClient: OkHttpClient
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        val trustManager = @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {

            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return emptyArray()
            }
        }
        val trustAllCerts = Array<TrustManager>(1) { trustManager }

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val hostnameVerifier = { _: String, _: SSLSession -> true }

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)

        okHttpClient = OkHttpClient()
            .newBuilder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier(hostnameVerifier)
            .connectTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }


}