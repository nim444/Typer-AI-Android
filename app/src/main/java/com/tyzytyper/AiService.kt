package com.tyzytyper

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

suspend fun fixGrammarAndTone(
    provider: String,
    model: String,
    apiKey: String,
    customPrompt: String,
    userInput: String
): String = withContext(Dispatchers.IO) {
    if (provider == "grok") {
        try {
            val url = URL("https://api.x.ai/v1/chat/completions")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.doOutput = true
            
            val jsonPayload = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", customPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userInput)
                    })
                })
            }
            
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonPayload.toString())
            writer.flush()
            writer.close()
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                val jsonResponse = JSONObject(response)
                val choices = jsonResponse.getJSONArray("choices")
                val message = choices.getJSONObject(0).getJSONObject("message")
                return@withContext message.getString("content").trim()
            } else {
                val errorReader = BufferedReader(InputStreamReader(connection.errorStream))
                val errorResponse = errorReader.readText()
                errorReader.close()
                return@withContext "Error ($responseCode): $errorResponse"
            }
        } catch (e: Exception) {
            return@withContext "Error: ${e.message}"
        }
    } else {
        // Default to Gemini
        val generativeModel = GenerativeModel(modelName = model, apiKey = apiKey)
        val prompt = "$customPrompt\n\n$userInput"
        return@withContext try {
            generativeModel.generateContent(prompt).text?.trim() ?: "Error."
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
