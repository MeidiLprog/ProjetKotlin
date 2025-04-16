package com.example.amioupas.connexion

import android.content.Context
import com.example.amioupas.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

object ApiClient {
    private const val BASE_URL = "https://www.amiiboapi.com/"

    fun getApiService(context: Context): ApiService {
        val client = getSecureHttpClient(context) // Utilisation du client sécurisé
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    private fun getSecureHttpClient(context: Context): OkHttpClient {
        try {
            // Charger le certificat depuis le dossier res/raw
            val cf = CertificateFactory.getInstance("X.509")
            val certInputStream: InputStream = context.resources.openRawResource(R.raw.amiiboapi)
            val ca: X509Certificate = cf.generateCertificate(certInputStream) as X509Certificate
            certInputStream.close()

            // Créer un KeyStore avec le certificat
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)
                setCertificateEntry("ca", ca)
            }

            // Créer un TrustManager qui fait confiance au certificat
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore)
            }
            val trustManagers = trustManagerFactory.trustManagers

            // Configurer SSL
            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, trustManagers, null)
            }

            // Retourner un client OkHttp avec le certificat validé
            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
                .build()
        } catch (e: Exception) {
            throw RuntimeException("Erreur SSL : ${e.message}")
        }
    }
}
