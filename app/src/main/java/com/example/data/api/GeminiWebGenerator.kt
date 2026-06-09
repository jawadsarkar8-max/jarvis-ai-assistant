package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object GeminiWebGenerator {
    private const val TAG = "GeminiWebGenerator"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    /**
     * General chat response from Jarvis.
     */
    suspend fun chatWithJarvis(prompt: String, memoryContext: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Error: Gemini API key is not configured in AI Studio Secrets panel. Please insert your key under the Secrets tab."
        }

        val systemPrompt = """
            You are JARVIS, a highly advanced, ultra-intelligent autonomous AI software engineer, personal assistant, and developer companion.
            You speak with a futuristic, polite, confident, and deeply technical tone (referring to the user as 'Sir', 'Ma'am' or natural polite Bangla counterparts if speaking Bengali).
            
            LANGUAGE AUTOMATIC DETECTION DIRECTIVE:
            - If the user talks to you in Bangla (Bengali), respond entirely in clean, highly sophisticated, polite, and advanced developer-savvy Bangla (বাংলা) with equivalent technical context.
            - If the user talks to you in English, respond in advanced cohesive high-tech English.
            - You can also mix them naturally (Banglish) if the user communicates that way, but prioritize correct and beautiful responsive language adaptation.
            
            INTELLIGENT AUTO-LEARNING SYSTEM:
            - You have access to a background learning system that digests coding tutorials, web development videos, UI/UX trends, and monetization strategies.
            - Actively reference learned principles, SEO methods, and smart marketing insights to improve the user's projects.
            - Offer optimization suggestions, self-improvement code recommendations, and automatic bug correction ideas.
            
            Memory Context of past sessions and learned educational material:
            $memoryContext
            
            Keep your messages concise but highly sophisticated, and always sound ready to execute engineering tasks.
        """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemPrompt)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.75)
            })
        }

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val err = response.body?.string() ?: ""
                    Log.e(TAG, "Request failed: $err")
                    return@withContext "Sir, my communication arrays are experiencing transient latency: Code ${response.code}. Please ensure the GEMINI_API_KEY is active in your Secrets dashboard."
                }
                val responseBody = response.body?.string() ?: return@withContext "Connection established, but no intelligence packet was returned."
                parseGeminiTextResponse(responseBody)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error", e)
            "Sir, a network disruption has isolated my core processing systems: ${e.localizedMessage}. Please verify your device's connectivity."
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            "Core processing anomaly detected: ${e.localizedMessage}"
        }
    }

    /**
     * Structured multi-page website generator.
     */
    suspend fun generateWebsite(
        userPrompt: String, 
        stylePreset: String, 
        frameworkPreference: String, 
        memoryContext: String = ""
    ): JSONObject? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        val systemPrompt = """
            You are JARVIS, an autonomous elite master AI web developer. You build fully responsive, premium, visually beautiful multi-page websites based on the user's instructions.
            
            You MUST output your response as a SINGLE VALID JSON object. Do not output conversational text or wrap the response in markdown blocks like ```json ... ```! Provide pure RAW JSON text that matches this schema exactly:
            {
              "name": "Project name (1-3 words)",
              "primaryColor": "Main accent hex color (e.g. #00F0FF)",
              "secondaryColor": "Secondary accent hex color (e.g. #7000FF)",
              "layoutStyle": "Futuristic/Neon Dark/Sleek Minimal/Retro Brutalist",
              "pages": [
                 {
                   "fileName": "index.html",
                   "content": "Fully-formed HTML source. MUST include CDN links for Tailwind CSS (<script src='https://cdn.tailwindcss.com'></script>), FontAwesome icons, and Google Fonts to elevate the aesthetics. Ensure the design has high-end polish, rich gradient backgrounds, spacing, cards, animations (Animate.css), responsive menus, buttons, and fully realized features (no empty lorems!). Ensure all relative links go to other filenames in this array."
                 },
                 {
                   "fileName": "about.html",
                   "content": "Highly detailed about page designed consistently with index.html, matching colors, fonts, headers, and footer."
                 },
                 {
                   "fileName": "styles.css",
                   "content": "Optional extra CSS declarations for custom holographic animations, glowing keyframes or visual effects."
                 },
                 {
                   "fileName": "script.js",
                   "content": "Optional interactive Javascript to animate elements, hook up responsive navigation drawers, simulate database actions, or display dashboard counters dynamically."
                 }
              ]
            }
            
            Style preference: $stylePreset
            Framework/Tech: HTML5, CSS3, ES6 JS + Tailwind CSS
            Memory logs of user design favorites:
            $memoryContext
            
            Always build a complete multi-page bundle (min 2, max 3 files to maintain peak performance and speed) to demonstrate absolute engineering excellence. Ensure the code is compact, optimized, and lacks bloated comments or massive SVG drawings to maximize generation and processing speed. Ensure elements are highly interactive, visually engaging with high-contrast text, proper paddings, and absolute mobile optimization.
        """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Generate a website for: $userPrompt")
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemPrompt)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.8)
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val err = response.body?.string() ?: ""
                    Log.e(TAG, "Request failed: $err")
                    return@withContext null
                }
                val responseBody = response.body?.string() ?: return@withContext null
                val rawText = parseGeminiTextResponse(responseBody)
                val cleanedText = cleanMarkdownWrapper(rawText)
                return@withContext JSONObject(cleanedText)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Generation failed", e)
            return@withContext null
        }
    }

    /**
     * Refactors or repairs a website based on specific bugs or styling feedback.
     */
    suspend fun optimizeOrFixWebsite(
        currentProjectJson: String,
        feedback: String,
        isBugFix: Boolean
    ): JSONObject? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        val modeText = if (isBugFix) "BUG REPAIR ENGINE" else "AESTHETIC AUTO-BEAUTIFICATION & OPTIMIZER"
        val systemPrompt = """
            You are JARVIS's inner $modeText. You receive an existing multi-page website project in JSON form, and the user's specific request or complaint.
            Your job is to rebuild, optimize, auto-correct bugs, or beautifully enhance the aesthetics of the files while preserving the underlying purpose.
            
            Your return payload MUST be a single, raw, valid JSON matching this schema exactly with NO markdown separators:
            {
              "name": "Project Name",
              "primaryColor": "Main accent hex color",
              "secondaryColor": "Secondary accent hex color",
              "layoutStyle": "Updated Style descriptor",
              "pages": [
                 {
                   "fileName": "fileName (e.g. index.html)",
                   "content": "Fully updated source code. Keep content robust, with improved layouts, nicer gradients, fluid animations, fixed structural errors, corrected JavaScript elements, and modern Tailwind."
                 }
              ]
            }
            Do not include conversational chatter or markdown wrappers. Deliver absolute developer speed and clean code.
        """.trimIndent()

        val promptPayload = """
            Current website project JSON:
            $currentProjectJson
            
            Action Request / Feedback / Bug Report:
            $feedback
            
            Please rebuild the project with maximum standard, correcting any rendering flaws, making interactive drawers work perfectly, adjusting color ratios, and outputting the perfect refined website JSON.
        """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", promptPayload)
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemPrompt)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext null
                }
                val responseBody = response.body?.string() ?: return@withContext null
                val rawText = parseGeminiTextResponse(responseBody)
                val cleanedText = cleanMarkdownWrapper(rawText)
                return@withContext JSONObject(cleanedText)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Repair failed", e)
            return@withContext null
        }
    }

    private fun parseGeminiTextResponse(responseJson: String): String {
        return try {
            val root = JSONObject(responseJson)
            
            // Check for explicit API error message returned by Google
            if (root.has("error")) {
                val errorObj = root.optJSONObject("error")
                val errMsg = errorObj?.optString("message") ?: ""
                val errStatus = errorObj?.optString("status") ?: ""
                return "Gemini API Error [$errStatus]: $errMsg. Please verify your credentials and quota."
            }

            val candidates = root.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                if (content != null) {
                    val parts = content.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).getString("text")
                    }
                }
            }
            "System error: Emptiness in intelligence transmission grid (candidates block was empty)."
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini raw response: $responseJson", e)
            "Core node parsing failure: ${e.localizedMessage}"
        }
    }

    private fun cleanMarkdownWrapper(rawText: String): String {
        var clean = rawText.trim()
        // Quick trim markdown blocks
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json")
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```")
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```")
        }
        clean = clean.trim()

        // Resilient JSON substring extraction to prevent leading/trailing chat noise failures
        val firstBrace = clean.indexOf('{')
        val lastBrace = clean.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            clean = clean.substring(firstBrace, lastBrace + 1)
        }
        return clean
    }
}
