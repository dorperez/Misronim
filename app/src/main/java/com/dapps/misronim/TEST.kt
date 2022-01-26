package com.dapps.misronim

import android.content.Context
import org.json.JSONObject

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL


class TEST(token: String) :
    AsyncTask<Void?, Void?, Void?>() {
    private val FIREBASE_URL = "https://fcm.googleapis.com/fcm/send"
    private val SERVER_KEY = "AAAAfVabI-8:APA91bH_P7lMBdgf3E1-MEYwDtahxpssP9A-5QRrSFLawAoEffrEUGOA5xXwDZIadB4SQfwLdYbhcllViIeVQBsevbTMnyrIa5ovbnFHtFgGazNqgbwtcTIqNW5n2VmZdC0zzVavOpAB "
    private val token: String
    protected override fun doInBackground(vararg p0: Void?): Void? {
        Log.e("doInBackground","doInBackground")

        /*{
            "to": "DEVICE_TOKEN",
            "data": {
            "type": "type",
                "title": "Android",
                "message": "Push Notification",
                "data": {
                    "key": "Extra data"
                }
            }
        }*/
        try {
            val url = URL(FIREBASE_URL)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setUseCaches(false)
            connection.setDoInput(true)
            connection.setDoOutput(true)
            connection.setRequestMethod("POST")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", "key=$SERVER_KEY")

            val root = JSONObject()
            root.put("to", token)
            val data = JSONObject()
            data.put("type", "type")
            data.put("title", "Android")
            data.put("message", "Push Notification")
            val innerData = JSONObject()
            innerData.put("key", "Extra data")
            data.put("data", innerData)
            root.put("data", data)
            Log.e("PushNotification", "Data Format: $root")
            try {
                val writer = OutputStreamWriter(connection.getOutputStream())
                writer.write(root.toString())
                writer.flush()
                writer.close()
                val responseCode: Int = connection.getResponseCode()
                Log.e("PushNotification", "Request Code: $responseCode")
                val bufferedReader = BufferedReader(InputStreamReader(connection.getInputStream()))
                var output: String?
                val builder = StringBuilder()
                while (bufferedReader.readLine().also { output = it } != null) {
                    builder.append(output)
                }
                bufferedReader.close()
                val result = builder.toString()
                Log.e("PushNotification", "Result JSON: $result")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("PushNotification", "Error: " + e.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PushNotification", "Error: " + e.message)
        }
        return null
    }

    init {
        this.token = token
    }
}