/*
  Copyright 2022 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
 */
package com.adobe.marketing.mobile.core.testapp

import android.app.Application
import android.util.Log
import androidx.core.os.UserManagerCompat
import com.adobe.marketing.mobile.AdobeCallback
import com.adobe.marketing.mobile.Identity
import com.adobe.marketing.mobile.Lifecycle
import com.adobe.marketing.mobile.LoggingMode
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.Signal
import com.adobe.marketing.mobile.core.testapp.extension.PerfExtension
import com.adobe.marketing.mobile.services.HttpConnecting
import com.adobe.marketing.mobile.services.NetworkCallback
import com.adobe.marketing.mobile.services.NetworkRequest
import com.adobe.marketing.mobile.services.Networking
import com.adobe.marketing.mobile.services.ServiceProvider
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http.HttpMethod
import java.io.InputStream
import java.net.HttpURLConnection

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("MyApp", "Application.onCreate() - start to initialize Adobe SDK. UserManagerCompat.isUserUnlocked(): ${UserManagerCompat.isUserUnlocked(this)}")
        MobileCore.setApplication(this)
        MobileCore.setLogLevel(LoggingMode.VERBOSE)

        // The test app uses bundled config. Uncomment this and change the app ID for testing the mobile tags property.
        val extensions = listOf(Identity.EXTENSION, Signal.EXTENSION, Lifecycle.EXTENSION, PerfExtension::class.java)
        MobileCore.registerExtensions(extensions) {
            MobileCore.configureWithAppID("3149c49c3910/c9a9e8a4892e/launch-2a566e89f4ec-development")
        }

        ServiceProvider.getInstance().networkService = MockNetworkService()
    }

    class MockNetworkService: Networking {
        override fun connectAsync(request: NetworkRequest?, callback: NetworkCallback?) {
            /* Does not produce IllegalStateException
            val requestBuilt = request?.url?.let { Request.Builder().url(it).build() }
                ?: Request.Builder().url("https://www.adobe.com").build()
            val responseBody = "{}".toResponseBody("application/json".toMediaType())
            val emptyResponse = Response.Builder().request(requestBuilt)
                .protocol(Protocol.HTTP_2)
                .code(HttpURLConnection.HTTP_OK)
                .message("success")
                .header("MOCKED_HEADER_KEY", "MOCKED_HEADER_VALUE")
                .body(responseBody)
                .build() */
            // Leads to java.lang.IllegalStateException: code < 0: -1
            val emptyResponse = Response.Builder().build()
            callback?.call(emptyResponse.toHttpConnecting())
        }

        private fun Response.toHttpConnecting() = object : HttpConnecting

        {    override fun getInputStream(): InputStream? = body?.byteStream()
            override fun getErrorStream(): InputStream? = null
            override fun getResponseCode(): Int = code
            override fun getResponseMessage(): String = message
            override fun getResponsePropertyValue(name: String): String? = header(name)
            override fun close() = this@toHttpConnecting.close()
        }
    }
}
