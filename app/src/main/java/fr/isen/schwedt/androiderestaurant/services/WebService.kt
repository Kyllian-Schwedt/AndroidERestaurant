package fr.isen.schwedt.androiderestaurant.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.android.volley.Cache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.schwedt.androiderestaurant.dto.Item
import fr.isen.schwedt.androiderestaurant.dto.MenuResult
import org.json.JSONObject

class WebService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun fetchMenu(context: Context, category: String, forceRefresh:Boolean = false, callback: (List<Item>) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val url = "http://test.api.catering.bluecodegames.com/menu"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                val gson = Gson()
                Log.d("WebService", "Response: $response")
                val menuResult = gson.fromJson(response, MenuResult::class.java)
                val filteredItems =
                    menuResult.data.flatMap { it.items }.filter { it.categ_name_fr == category }
                callback(filteredItems)
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id_shop"] = "1"
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return JSONObject(params).toString().toByteArray()
            }

            override fun getCacheEntry(): Cache.Entry? {
                val entry = super.getCacheEntry()
                return if (entry == null) {
                    null
                } else {
                    Cache.Entry().apply {
                        data = entry.data
                        etag = entry.etag
                        softTtl = entry.softTtl
                        ttl = entry.ttl
                        serverDate = entry.serverDate
                        lastModified = entry.lastModified
                        responseHeaders = entry.responseHeaders
                    }
                }
            }
        }

        stringRequest.setShouldCache(!forceRefresh)
        queue.add(stringRequest)
    }
}