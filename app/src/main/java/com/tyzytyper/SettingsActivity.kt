package com.tyzytyper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.tyzytyper.ui.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AppScreen(val title: String) {
    SettingsList("Typer Settings"),
    AiProvidersConfig("AI Providers"),
    PromptConfig("Default Pre-prompt"),
    ThemeConfig("App Theme"),
    FloatingButtonConfig("Floating Button")
}

class SettingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            
            var currentScreen by remember { mutableStateOf(AppScreen.SettingsList) }
            
            var apiKey by remember { mutableStateOf("") }
            var grokApiKey by remember { mutableStateOf("") }
            var geminiModel by remember { mutableStateOf("") }
            var grokModel by remember { mutableStateOf("") }
            var defaultProvider by remember { mutableStateOf("") }
            
            var customPrompt by remember { mutableStateOf("") }
            var themeOption by remember { mutableIntStateOf(0) } // 0: System, 1: Light, 2: Dark
            var floatingEnabled by remember { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(true) }
            
            LaunchedEffect(Unit) {
                apiKey = SettingsManager.getApiKey(context).first() ?: ""
                grokApiKey = SettingsManager.getGrokApiKey(context).first() ?: ""
                geminiModel = SettingsManager.getGeminiModel(context).first()
                grokModel = SettingsManager.getGrokModel(context).first()
                defaultProvider = SettingsManager.getDefaultProvider(context).first()
                
                customPrompt = SettingsManager.getCustomPrompt(context).first()
                themeOption = SettingsManager.getThemeOption(context).first()
                floatingEnabled = SettingsManager.isFloatingButtonEnabled(context).first()
                if (floatingEnabled && Settings.canDrawOverlays(context)) {
                    context.startService(Intent(context, FloatingButtonService::class.java))
                }
                isLoading = false
            }

            if (!isLoading) {
                val isDark = when (themeOption) {
                    1 -> false
                    2 -> true
                    else -> isSystemInDarkTheme()
                }

                AppTheme(darkTheme = isDark) {
                    // Navigate back to list on back button press
                    BackHandler(enabled = currentScreen != AppScreen.SettingsList) {
                        currentScreen = AppScreen.SettingsList
                    }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    if (currentScreen == AppScreen.SettingsList) {
                                        Image(
                                            painter = painterResource(id = if (isDark) R.drawable.typer_logo_dark_theme else R.drawable.typer_logo_light_theme),
                                            contentDescription = "Typer Settings",
                                            modifier = Modifier.height(256.dp)
                                        )
                                    } else {
                                        Text(currentScreen.title, color = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                navigationIcon = {
                                    if (currentScreen != AppScreen.SettingsList) {
                                        IconButton(onClick = { currentScreen = AppScreen.SettingsList }) {
                                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            when (currentScreen) {
                                AppScreen.SettingsList -> {
                                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                        ListItem(
                                            headlineContent = { Text("AI Providers") },
                                            supportingContent = { Text("Configure API Keys and Models") },
                                            leadingContent = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            modifier = Modifier.clickable { currentScreen = AppScreen.AiProvidersConfig }
                                        )
                                        HorizontalDivider()
                                        ListItem(
                                            headlineContent = { Text("Default Pre-prompt") },
                                            supportingContent = { Text("Instructions prepended to your text") },
                                            leadingContent = { Icon(Icons.Default.Message, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            modifier = Modifier.clickable { currentScreen = AppScreen.PromptConfig }
                                        )
                                        HorizontalDivider()
                                        ListItem(
                                            headlineContent = { Text("App Theme") },
                                            supportingContent = { Text("System, Light, or Dark") },
                                            leadingContent = { Icon(Icons.Default.ColorLens, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            modifier = Modifier.clickable { currentScreen = AppScreen.ThemeConfig }
                                        )
                                        HorizontalDivider()
                                        ListItem(
                                            headlineContent = { Text("Floating Button") },
                                            supportingContent = { Text("Persistent on-screen shortcut") },
                                            leadingContent = { Icon(Icons.Default.ToggleOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                            modifier = Modifier.clickable { currentScreen = AppScreen.FloatingButtonConfig }
                                        )
                                        HorizontalDivider()
                                    }
                                }
                                AppScreen.AiProvidersConfig -> {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                                        Text("AI Providers Configuration", style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                                            // Gemini Section
                                            Card(modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        RadioButton(selected = defaultProvider == "gemini", onClick = { defaultProvider = "gemini" })
                                                        Text("Default Provider: Gemini", style = MaterialTheme.typography.titleSmall)
                                                    }
                                                    var geminiPasswordVisible by remember { mutableStateOf(false) }
                                                    OutlinedTextField(
                                                        value = apiKey,
                                                        onValueChange = { apiKey = it },
                                                        label = { Text("Gemini API Key") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        visualTransformation = if (geminiPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                                        trailingIcon = { IconButton(onClick = { geminiPasswordVisible = !geminiPasswordVisible }) { Icon(if (geminiPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null) } }
                                                    )
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                    ProviderModelDropdown("Gemini Model", listOf("gemini-2.5-flash", "gemini-3-flash", "gemini-2.5-flash-lite"), geminiModel, { geminiModel = it })
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            // Grok Section
                                            Card(modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        RadioButton(selected = defaultProvider == "grok", onClick = { defaultProvider = "grok" })
                                                        Text("Default Provider: Grok", style = MaterialTheme.typography.titleSmall)
                                                    }
                                                    var grokPasswordVisible by remember { mutableStateOf(false) }
                                                    OutlinedTextField(
                                                        value = grokApiKey,
                                                        onValueChange = { grokApiKey = it },
                                                        label = { Text("Grok API Key") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true,
                                                        visualTransformation = if (grokPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                                        trailingIcon = { IconButton(onClick = { grokPasswordVisible = !grokPasswordVisible }) { Icon(if (grokPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null) } }
                                                    )
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                    ProviderModelDropdown("Grok Model", listOf("grok-4-1-fast-non-reasoning", "grok-3-mini"), grokModel, { grokModel = it })
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { 
                                                scope.launch { 
                                                    SettingsManager.saveApiKey(context, apiKey)
                                                    SettingsManager.saveGrokApiKey(context, grokApiKey)
                                                    SettingsManager.saveGeminiModel(context, geminiModel)
                                                    SettingsManager.saveGrokModel(context, grokModel)
                                                    SettingsManager.saveDefaultProvider(context, defaultProvider)
                                                } 
                                                currentScreen = AppScreen.SettingsList
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Save Settings") }
                                    }
                                }
                                AppScreen.PromptConfig -> {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                                        Text("Default Pre-prompt", style = MaterialTheme.typography.titleMedium)
                                        Text("The instructions prepended to your text. You can change this to define exactly how the AI should behave.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                            value = customPrompt,
                                            onValueChange = { customPrompt = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            minLines = 3
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Button(
                                            onClick = { 
                                                scope.launch { SettingsManager.saveCustomPrompt(context, customPrompt) } 
                                                currentScreen = AppScreen.SettingsList
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Save Prompt Template") }
                                    }
                                }
                                AppScreen.ThemeConfig -> {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                                        Text("App Theme", style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                            FilterChip(
                                                selected = themeOption == 0,
                                                onClick = { 
                                                    themeOption = 0 
                                                    scope.launch { SettingsManager.saveThemeOption(context, 0) }
                                                },
                                                label = { Text("System") }
                                            )
                                            FilterChip(
                                                selected = themeOption == 1,
                                                onClick = { 
                                                    themeOption = 1 
                                                    scope.launch { SettingsManager.saveThemeOption(context, 1) }
                                                },
                                                label = { Text("Light") }
                                            )
                                            FilterChip(
                                                selected = themeOption == 2,
                                                onClick = { 
                                                    themeOption = 2 
                                                    scope.launch { SettingsManager.saveThemeOption(context, 2) }
                                                },
                                                label = { Text("Dark") }
                                            )
                                        }
                                    }
                                }
                                AppScreen.FloatingButtonConfig -> {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                                        Text("Floating Typer Button", style = MaterialTheme.typography.titleMedium)
                                        Text("Adds a permanent floating button on your screen so you don't have to pull down Quick Settings.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(24.dp))
                                        
                                        val overlayPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                                            if (Settings.canDrawOverlays(context)) {
                                                scope.launch { SettingsManager.saveFloatingButtonEnabled(context, true) }
                                                context.startService(Intent(context, FloatingButtonService::class.java))
                                                floatingEnabled = true
                                            } else {
                                                floatingEnabled = false
                                                scope.launch { SettingsManager.saveFloatingButtonEnabled(context, false) }
                                            }
                                        }
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Enable Floating Button", style = MaterialTheme.typography.bodyLarge)
                                            Switch(
                                                checked = floatingEnabled,
                                                onCheckedChange = { isChecked ->
                                                    if (isChecked) {
                                                        if (!Settings.canDrawOverlays(context)) {
                                                            overlayPermissionLauncher.launch(
                                                                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                                                            )
                                                        } else {
                                                            scope.launch { SettingsManager.saveFloatingButtonEnabled(context, true) }
                                                            context.startService(Intent(context, FloatingButtonService::class.java))
                                                            floatingEnabled = true
                                                        }
                                                    } else {
                                                        scope.launch { SettingsManager.saveFloatingButtonEnabled(context, false) }
                                                        context.stopService(Intent(context, FloatingButtonService::class.java))
                                                        floatingEnabled = false
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderModelDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}