package com.tyzytyper

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.tyzytyper.ui.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PopupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        disableTransitions()

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            var text by remember { mutableStateOf("") }
            var originalText by remember { mutableStateOf("") }
            var result by remember { mutableStateOf("") }

            var geminiApiKey by remember { mutableStateOf("") }
            var grokApiKey by remember { mutableStateOf("") }
            var geminiModel by remember { mutableStateOf("") }
            var grokModel by remember { mutableStateOf("") }
            var defaultProvider by remember { mutableStateOf("grok") }

            var customPrompt by remember { mutableStateOf("") }
            var themeOption by remember { mutableIntStateOf(0) }
            var isLoadingState by remember { mutableStateOf(true) }
            var isFixing by remember { mutableStateOf(false) }

            val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                if (res.resultCode == Activity.RESULT_OK) {
                    res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.let { text = it }
                }
            }

            LaunchedEffect(Unit) {
                geminiApiKey = SettingsManager.getApiKey(context).first() ?: ""
                grokApiKey = SettingsManager.getGrokApiKey(context).first() ?: ""
                geminiModel = SettingsManager.getGeminiModel(context).first()
                grokModel = SettingsManager.getGrokModel(context).first()
                defaultProvider = SettingsManager.getDefaultProvider(context).first()

                customPrompt = SettingsManager.getCustomPrompt(context).first()
                themeOption = SettingsManager.getThemeOption(context).first()
                isLoadingState = false
            }

            if (!isLoadingState) {
                val isDark = when (themeOption) {
                    1 -> false
                    2 -> true
                    else -> isSystemInDarkTheme()
                }

                AppTheme(darkTheme = isDark) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .statusBarsPadding()
                            .imePadding()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Top Bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your text...")
                                        }
                                        speechLauncher.launch(intent)
                                    }) {
                                        Icon(Icons.Default.Mic, contentDescription = "Voice input", tint = MaterialTheme.colorScheme.onSurface)
                                    }
                                    IconButton(onClick = {
                                        startActivity(Intent(context, SettingsActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        })
                                        finish()
                                    }) {
                                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
                                    }
                                    IconButton(onClick = { finish() }) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                val activeApiKey = if (defaultProvider == "grok") grokApiKey else geminiApiKey
                                val activeModel = if (defaultProvider == "grok") grokModel else geminiModel
                                val providerName = if (defaultProvider == "grok") "Grok" else "Gemini"

                                if (activeApiKey.isBlank()) {
                                    // Missing API Key State
                                    Text("API Key Required", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Please add your $providerName API Key in the settings.", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            startActivity(Intent(context, SettingsActivity::class.java).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            })
                                            finish()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Go to Settings")
                                    }
                                } else {
                                    // Normal State
                                    OutlinedTextField(
                                        value = text,
                                        onValueChange = { text = it },
                                        placeholder = { Text("Enter your text here...", color = Color.Gray) },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 5,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedBorderColor = Color.Gray,
                                            focusedBorderColor = Color.Gray
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    val isButtonEnabled = !isFixing && text.isNotBlank()

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                isFixing = true
                                                originalText = text
                                                result = fixGrammarAndTone(defaultProvider, activeModel, activeApiKey, customPrompt, text)
                                                isFixing = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.onSurface,
                                            contentColor = MaterialTheme.colorScheme.surface,
                                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                        ),
                                        contentPadding = PaddingValues(vertical = 12.dp),
                                        enabled = isButtonEnabled
                                    ) {
                                        if (isFixing) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.surface)
                                        } else {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Fix Grammar")
                                        }
                                    }

                                    if (result.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        val diffString = computeTextDiff(originalText, result, isDark)
                                        OutlinedCard(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
                                        ) {
                                            Text(diffString, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("TyZy Typer", result))
                                                finish()
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Copy & Close") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun disableTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    override fun finish() {
        super.finish()
        disableTransitions()
    }

    private fun computeTextDiff(original: String, corrected: String, isDark: Boolean): AnnotatedString {
        val originalWords = original.split("\\s+".toRegex())
        val correctedWords = corrected.split("\\s+".toRegex())
        
        val highlightColor = if (isDark) Color(0xFF1E4620) else Color(0xFFD4EDDA)
        val highlightSpan = SpanStyle(background = highlightColor)

        return buildAnnotatedString {
            // A very simple token-based diff. 
            // Realistically you'd use a Levenshtein library, but this approximates the Copilot effect cleanly.
            for (i in correctedWords.indices) {
                val currentWord = correctedWords[i]
                
                // If the word doesn't exist in the original text (or nearby), it's probably new/changed
                val isChanged = !originalWords.contains(currentWord)
                
                if (isChanged) {
                    withStyle(highlightSpan) {
                        append(currentWord)
                    }
                } else {
                    append(currentWord)
                }
                
                if (i < correctedWords.size - 1) append(" ")
            }
        }
    }
}
