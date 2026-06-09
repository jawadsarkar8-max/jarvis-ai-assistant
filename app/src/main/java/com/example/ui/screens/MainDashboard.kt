package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProjectEntity
import com.example.data.model.WebPage
import com.example.data.model.MemoryEntity
import com.example.ui.components.HologramWaves
import com.example.ui.components.WebPreviewContainer
import com.example.ui.components.AnimeAvatar
import com.example.ui.components.CodeMirrorEditor
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.JarvisViewModel
import com.example.ui.viewmodel.SystemBackup
import com.example.ui.viewmodel.AiSuggestion
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Color Palette Definition - High Density Futuristic Dark Theme
val ColorBg = Color(0xFF07090D) // Ultra Deep Space black background 
val ColorSurface = Color(0xFF13151E) // Sleek slate carbon container
val ColorCard = Color(0xFF1B1E29) // Glowing tech container fill
val ColorCyan = Color(0xFF00F0FF) // Cyber neon cyan
val ColorPurple = Color(0xFF7000FF) // Hyper violet
val ColorMagenta = Color(0xFFFF007F) // Lucid magenta accent glow
val ColorTextPrimary = Color(0xFFF0F2F6) // Crisp white text
val ColorTextSecondary = Color(0xFF8B9AAC) // Muted metallic gray
val ColorGreenGlow = Color(0xFF0DF29F) // Glowing matrix green
val ColorGlassBg = Color(0x35141622) // Translucent glass backdrop
val ColorGlassBorder = Color(0x3CFFFFFF) // Translucent reflective rim border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val allProjects by viewModel.allProjects.collectAsState()
    val allMemory by viewModel.allMemory.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()
    val projectPages by viewModel.projectPages.collectAsState()
    val selectedPage by viewModel.selectedPage.collectAsState()
    
    val isGenerating by viewModel.isGenerating.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val status by viewModel.jarvisStatus.collectAsState()
    val terminalLogs by viewModel.terminalLogs.collectAsState()
    val aiScore by viewModel.aiScoreOptimized.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()

    val backgroundLearningEnabled by viewModel.backgroundLearningEnabled.collectAsState()
    val privacyConsentGranted by viewModel.privacyConsentGranted.collectAsState()
    val autoUpdateModeActive by viewModel.autoUpdateModeActive.collectAsState()
    
    val learningSources by viewModel.learningSources.collectAsState()
    val learningLogs by viewModel.learningLogs.collectAsState()
    val monetizationPlan by viewModel.monetizationPlan.collectAsState()
    val seoScoreAdvice by viewModel.seoScoreAdvice.collectAsState()

    // Evolution and version control states
    val systemVersion by viewModel.systemVersion.collectAsState()
    val systemBackups by viewModel.systemBackups.collectAsState()
    val systemChangeLogs by viewModel.systemChangeLogs.collectAsState()
    val activeAiSuggestion by viewModel.activeAiSuggestion.collectAsState()

    var chatInputText by remember { mutableStateOf("") }
    val chatListState = rememberLazyListState()

    // Scroll chat to the last item automatically
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            chatListState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Neon Nav Bar
            NavigationBar(
                containerColor = ColorSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.border(0.5.dp, ColorPurple.copy(alpha = 0.2f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val navItems = listOf<Triple<String, String, ImageVector>>(
                    Triple("CHAT", "Core Hub", Icons.Default.Home),
                    Triple("CODE", "Code Sandbox", Icons.Default.Edit),
                    Triple("PREVIEW", "Live Web", Icons.Default.Search),
                    Triple("PROJECTS", "Engine Projects", Icons.Default.Star),
                    Triple("MEMORY", "AI Vault", Icons.Default.Settings)
                )

                navItems.forEach { (tab, label, icon) ->
                    val isSelected = activeTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.activeTab.value = tab },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) ColorCyan else ColorTextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ColorCyan else ColorTextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = ColorPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBg)
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // High Tech Glow Header (High Density Theme)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ColorSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .border(0.5.dp, ColorCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pulsing Core Icon
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isGenerating) ColorPurple else ColorGreenGlow
                                )
                                .border(1.dp, ColorTextPrimary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "JARVIS CORE v4.2",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(ColorGreenGlow)
                                        .border(0.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AUTONOMOUS ACTIVE // " + status.uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    color = ColorGreenGlow,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // System Performance HUD Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(ColorCard, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Efficiency",
                            tint = ColorCyan,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI RATING: $aiScore%",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorCyan
                        )
                    }
                }

                // System Health Grid (High Density)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Memory" to "14.8GB",
                        "Logic Gap" to "0.02ms",
                        "Sync" to "100%"
                    ).forEach { (label, value) ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = ColorSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = ColorTextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = value,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (label == "Sync") ColorGreenGlow else ColorCyan
                                )
                            }
                        }
                    }
                }

                // Main Section Changer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (activeTab) {
                        "CHAT" -> ChatAndVoiceSection(
                            chatHistory = chatHistory,
                            isListening = isListening,
                            isGenerating = isGenerating,
                            terminalLogs = terminalLogs,
                            chatInputText = chatInputText,
                            listState = chatListState,
                            onChatInputChange = { chatInputText = it },
                            onSendChat = {
                                if (chatInputText.isNotBlank()) {
                                    viewModel.sendChatMessage(chatInputText)
                                    chatInputText = ""
                                }
                            },
                            onTriggerVoiceHud = { viewModel.isListening.value = true },
                            onQuickPrompt = { prompt ->
                                viewModel.triggerWebsiteGeneration(prompt)
                            }
                        )
                        "CODE" -> CodeSandboxSection(
                            currentProject = viewModel.currentProject,
                            pages = projectPages,
                            selectedPage = selectedPage,
                            isGenerating = isGenerating,
                            onPageSelected = { viewModel.selectedPage.value = it },
                            onSaveCode = { name, code ->
                                viewModel.updatePageContent(name, code)
                            }
                        )
                        "PREVIEW" -> LivePreviewSection(
                            currentProject = viewModel.currentProject,
                            pages = projectPages,
                            selectedPage = selectedPage,
                            aiScore = aiScore,
                            isGenerating = isGenerating,
                            onPageSelected = { viewModel.selectedPage.value = it },
                            onBeautify = { 
                                viewModel.repairOrOptimizeProject("Verify responsive design, enhance layout gradients, and add high-contrast typography", false)
                            },
                            onRepairBugs = {
                                viewModel.repairOrOptimizeProject("Auto-correct layout incompatibilities, repair any static loops, and secure margins", true)
                            }
                        )
                        "PROJECTS" -> ProjectsRegistrySection(
                            allProjects = allProjects,
                            currentProject = viewModel.currentProject,
                            onProjectSelected = { viewModel.selectProject(it) },
                            onProjectDelete = { viewModel.deleteProject(it) }
                        )
                        "MEMORY" -> MemoryVaultSection(
                            allMemory = allMemory,
                            backgroundLearningEnabled = backgroundLearningEnabled,
                            privacyConsentGranted = privacyConsentGranted,
                            autoUpdateModeActive = autoUpdateModeActive,
                            learningSources = learningSources,
                            learningLogs = learningLogs,
                            monetizationPlan = monetizationPlan,
                            seoScoreAdvice = seoScoreAdvice,
                            systemVersion = systemVersion,
                            systemBackups = systemBackups,
                            systemChangeLogs = systemChangeLogs,
                            activeAiSuggestion = activeAiSuggestion,
                            onToggleBackgroundLearning = { viewModel.toggleBackgroundLearning() },
                            onTogglePrivacyConsent = { viewModel.togglePrivacyConsent() },
                            onToggleAutoUpdateMode = { viewModel.toggleAutoUpdateMode() },
                            onSubmitLearningSource = { viewModel.submitLearningSource(it) },
                            onRunSEOAndBusinessInference = { viewModel.runSEOAndBusinessInference() },
                            onPurgeSystemTelemetry = { viewModel.purgeSystemTelemetry() },
                            onSaveParam = { k, v -> viewModel.saveMemoryPreset(k, v) },
                            onApplySuggestedAiUpgrade = { viewModel.applySuggestedAiUpgrade(it) },
                            onrequestSystemUpgrade = { viewModel.requestSystemUpgrade(it) },
                            onRollbackToSystemBackup = { viewModel.rollbackToSystemBackup(it) }
                        )
                    }
                }
            }

            // High Energy Voice HUD Overlay Panel
            if (isListening) {
                VoiceHudPanel(
                    onDismiss = { viewModel.isListening.value = false },
                    onCommandSelected = { command ->
                        viewModel.isListening.value = false
                        viewModel.addTerminalLog("Voice command decoded successfully: $command")
                        coroutineScope.launch {
                            viewModel.chatHistory.value = viewModel.chatHistory.value + ChatMessage("USER", "Voice command: $command")
                            delay(100)
                            viewModel.triggerWebsiteGeneration(command)
                        }
                    }
                )
            }
        }
    }
}

// ======================================
// 1. CORE JARVIS CHAT HUB & TERMINAL SECTION
// ======================================
@Composable
fun FloatingGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    glowColor: Color = ColorCyan
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x35141622))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(glowColor.copy(alpha = 0.8f), ColorPurple.copy(alpha = 0.3f), glowColor.copy(alpha = 0.8f))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = glowColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text.uppercase(),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ChatAndVoiceSection(
    chatHistory: List<ChatMessage>,
    isListening: Boolean,
    isGenerating: Boolean,
    terminalLogs: List<String>,
    chatInputText: String,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onChatInputChange: (String) -> Unit,
    onSendChat: () -> Unit,
    onTriggerVoiceHud: () -> Unit,
    onQuickPrompt: (String) -> Unit
) {
    var languageIndex by remember { mutableStateOf(0) }
    val languagesList = listOf("Bangla + English", "বাংলা ভাষা", "English Only")
    
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        
        // 1. Premium Anime-Style AI Avatar Interface (Glassmorphic)
        AnimeAvatar(
            modifier = Modifier.padding(bottom = 10.dp),
            isGenerating = isGenerating,
            isListening = isListening,
            currentLanguageName = languagesList[languageIndex],
            onToggleLanguage = {
                languageIndex = (languageIndex + 1) % languagesList.size
            },
            onAvatarClick = {
                // Interactive avatar click interaction
            }
        )

        // 2. Row of Floating Glass Buttons with glow (Auto language/quick actions)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "► FLOATING GLASS PLATES",
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                color = ColorTextSecondary,
                fontWeight = FontWeight.Bold
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FloatingGlassButton(
                    text = "Bilingual Auto",
                    glowColor = ColorGreenGlow,
                    onClick = {
                        languageIndex = 0
                    }
                )
                FloatingGlassButton(
                    text = "Speed Optimize",
                    glowColor = ColorMagenta,
                    onClick = {
                        onQuickPrompt("Optimize processing speed and compression size of all core compiled websites.")
                    }
                )
            }
        }

        // Action Modules / System Progress indicators (Glassmorphic Tonal Accent)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Code View Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(0.8.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x281B2030))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = ColorCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Sandbox View",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .fillMaxHeight()
                                .background(
                                    brush = Brush.horizontalGradient(listOf(ColorCyan, ColorPurple)),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }

            // Auto-Improve Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(0.8.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x281B2030))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = ColorMagenta,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Auto-Heal Core",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight()
                                .background(
                                    brush = Brush.horizontalGradient(listOf(ColorPurple, ColorMagenta)),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        }

        // Deployment Status Bar (Glassmorphic)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .border(0.8.dp, ColorCyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0x2007090D))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Deployment Ready",
                        tint = ColorGreenGlow,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Vercel Edge Ready // Bangla Node Core",
                        fontSize = 10.sp,
                        color = ColorTextSecondary
                    )
                }
                Text(
                    text = "genesis-app.jarvis.sh",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = ColorCyan
                )
            }
        }

        // Split: Terminal logs at the top
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x2C13151E)),
            border = BorderStroke(0.8.dp, ColorPurple.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "► JARVIS SYSTEM LOG STREAM",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorCyan,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                    val reversedLogs = terminalLogs.asReversed()
                    items(reversedLogs) { log ->
                        Text(
                            text = log,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 7.sp,
                            color = if (log.contains("FAILURE") || log.contains("ERROR")) Color.Red else ColorTextSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Active conversational history
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0x1807090D), RoundedCornerShape(12.dp))
                .border(0.8.dp, ColorPurple.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            items(chatHistory) { msg ->
                val isJarvis = msg.sender == "JARVIS"
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = if (isJarvis) Alignment.Start else Alignment.End
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (isJarvis) {
                                        listOf(Color(0x2A13151E), Color(0x1F13151E))
                                    } else {
                                        listOf(ColorPurple.copy(alpha = 0.15f), ColorCyan.copy(alpha = 0.04f))
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 0.8.dp,
                                color = if (isJarvis) ColorCyan.copy(alpha = 0.15f) else ColorPurple.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isJarvis) Icons.Default.Settings else Icons.Default.AccountBox,
                                    contentDescription = null,
                                    tint = if (isJarvis) ColorCyan else ColorMagenta,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = msg.sender,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isJarvis) ColorCyan else ColorMagenta
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = msg.message,
                                fontSize = 13.sp,
                                color = ColorTextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Suggested website blueprints
        if (chatHistory.size < 4) {
            Text(
                text = "Sir, select a bilingual blueprints to auto-build instantly:",
                fontSize = 10.sp,
                color = ColorTextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                val quickPrompts = listOf(
                    "Crypto Landing (EN)",
                    "ব্যক্তিগত পোর্টফোলিও (BN)",
                    "SaaS Dashboard"
                )
                quickPrompts.forEach { p ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0x281B2030), RoundedCornerShape(8.dp))
                            .border(0.8.dp, ColorCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable { onQuickPrompt(p) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorCyan,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Typing & Action inputs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice listener mode activator
            IconButton(
                onClick = onTriggerVoiceHud,
                modifier = Modifier
                    .background(ColorPurple.copy(alpha = 0.25f), CircleShape)
                    .border(1.dp, ColorCyan.copy(alpha = 0.4f), CircleShape)
                    .testTag("voice_hud_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Voice Mode",
                    tint = ColorCyan
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Text input
            TextField(
                value = chatInputText,
                onValueChange = onChatInputChange,
                placeholder = { Text("E.g., Design a glassmorphic portfolio...", color = ColorTextSecondary) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary,
                    focusedContainerColor = Color(0x3213151E),
                    unfocusedContainerColor = Color(0x3213151E),
                    disabledContainerColor = Color(0x3213151E),
                    focusedIndicatorColor = ColorCyan,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                trailingIcon = {
                    if (chatInputText.isNotBlank()) {
                        IconButton(
                            onClick = onSendChat,
                            modifier = Modifier.testTag("send_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = ColorCyan
                            )
                        }
                    }
                }
            )
        }
    }
}

// ======================================
// 2. CODE EDITOR / SANDBOX SECTION
// ======================================
@Composable
fun CodeSandboxSection(
    currentProject: StateFlow<ProjectEntity?>,
    pages: List<WebPage>,
    selectedPage: WebPage?,
    isGenerating: Boolean,
    onPageSelected: (WebPage) -> Unit,
    onSaveCode: (String, String) -> Unit
) {
    val projectVal by currentProject.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf("") }

    // Synchronize content when selected file modifies
    LaunchedEffect(selectedPage) {
        editedContent = selectedPage?.content ?: ""
    }

    if (projectVal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Sir, no project is loaded. Generate a file block first.",
                color = ColorTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Project title banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PROJECT DIRECTORY // ${projectVal!!.name.uppercase()}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = ColorTextPrimary
            )

            // Direct Save trigger
            if (isEditing) {
                Button(
                    onClick = {
                        isEditing = false
                        selectedPage?.let { onSaveCode(it.fileName, editedContent) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorGreenGlow),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("DEPLOY CHANGES", color = ColorBg, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { isEditing = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPurple),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("EDIT CODES", color = ColorTextPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Multi-page index layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            pages.forEach { p ->
                val isSel = p.fileName == selectedPage?.fileName
                Box(
                    modifier = Modifier
                        .background(
                            if (isSel) ColorPurple.copy(alpha = 0.3f) else ColorSurface,
                            RoundedCornerShape(4.dp)
                        )
                        .border(
                            0.5.dp,
                            if (isSel) ColorCyan else ColorTextSecondary.copy(alpha = 0.2f),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onPageSelected(p) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = p.fileName,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSel) ColorCyan else ColorTextSecondary
                    )
                }
            }
        }

        // Active terminal editor container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(ColorSurface, RoundedCornerShape(8.dp))
                .border(0.5.dp, ColorPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            val page = selectedPage
            if (page != null) {
                CodeMirrorEditor(
                    content = if (isEditing) editedContent else page.content,
                    fileName = page.fileName,
                    isReadOnly = !isEditing,
                    onContentChange = { newCode ->
                        if (isEditing) {
                            editedContent = newCode
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sir, compile or load a file block template.",
                        color = ColorTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ======================================
// 3. LIVE WEBVIEW PREVIEW SECTION
// ======================================
@Composable
fun LivePreviewSection(
    currentProject: StateFlow<ProjectEntity?>,
    pages: List<WebPage>,
    selectedPage: WebPage?,
    aiScore: Int,
    isGenerating: Boolean,
    onPageSelected: (WebPage) -> Unit,
    onBeautify: () -> Unit,
    onRepairBugs: () -> Unit
) {
    val projectVal by currentProject.collectAsState()
    var previewMode by remember { mutableStateOf("DESKTOP") } // "DESKTOP", "TABLET", "MOBILE"

    if (projectVal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Sir, no loaded website was detected. Trigger synthesis in Jarvis channel first.",
                color = ColorTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Render view constraint configuration HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val views = listOf("DESKTOP", "TABLET", "MOBILE")
                views.forEach { v ->
                    val isS = previewMode == v
                    Box(
                        modifier = Modifier
                            .background(
                                if (isS) ColorCyan else ColorSurface,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { previewMode = v }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = v,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isS) ColorBg else ColorTextPrimary
                        )
                    }
                }
            }

            // Quick pages selector inside preview
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                pages.forEach { p ->
                    val isS = p.fileName == selectedPage?.fileName
                    Box(
                        modifier = Modifier
                            .background(
                                if (isS) ColorPurple else ColorSurface,
                                RoundedCornerShape(2.dp)
                            )
                            .clickable { onPageSelected(p) }
                            .padding(horizontal = 5.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = p.fileName,
                            fontSize = 8.sp,
                            color = ColorTextPrimary
                        )
                    }
                }
            }
        }

        // Live WebView box with responsive dimension wrap modifiers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(ColorSurface, RoundedCornerShape(8.dp))
                .border(2.dp, ColorPurple.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            val contentModifier = when (previewMode) {
                "MOBILE" -> Modifier.width(360.dp).fillMaxHeight()
                "TABLET" -> Modifier.width(600.dp).fillMaxHeight()
                else -> Modifier.fillMaxSize()
            }

            WebPreviewContainer(
                modifier = contentModifier,
                pages = pages,
                selectedPage = selectedPage,
                onPageNavigated = onPageSelected
            )
        }

        // Optimizations and actions panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBeautify,
                colors = ButtonDefaults.buttonColors(containerColor = ColorPurple),
                enabled = !isGenerating,
                modifier = Modifier.weight(1f).testTag("beautify_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("AI BEAUTIFY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onRepairBugs,
                colors = ButtonDefaults.buttonColors(containerColor = ColorSurface),
                enabled = !isGenerating,
                modifier = Modifier
                    .weight(1f)
                    .border(0.5.dp, ColorCyan, ButtonDefaults.shape)
                    .testTag("repair_bugs_button")
            ) {
                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp), tint = ColorCyan)
                Spacer(modifier = Modifier.width(4.dp))
                Text("REPAIR BUGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorCyan)
            }
        }
    }
}

// ======================================
// 4. STORAGE / PROJECTS REGISTRY SECTION
// ======================================
@Composable
fun ProjectsRegistrySection(
    allProjects: List<ProjectEntity>,
    currentProject: StateFlow<ProjectEntity?>,
    onProjectSelected: (ProjectEntity) -> Unit,
    onProjectDelete: (ProjectEntity) -> Unit
) {
    val projectVal by currentProject.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text(
            text = "JARVIS COMPILED REPOSITORY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ColorCyan,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (allProjects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No stored architectures in local databases.",
                    color = ColorTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(allProjects) { p ->
                val isActive = p.id == projectVal?.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProjectSelected(p) }
                        .border(
                            0.5.dp,
                            if (isActive) ColorCyan else ColorPurple.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = ColorSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = p.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) ColorCyan else ColorTextPrimary
                                )
                                Text(
                                    text = p.layoutStyle,
                                    fontSize = 10.sp,
                                    color = ColorTextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Row {
                                // One click Copy / Export button
                                IconButton(
                                    onClick = {
                                        // Simple share mechanism
                                        val sendIntent: Intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, "Project: ${p.name}\n\nGenerated files:\n${p.pagesJson}")
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, null)
                                        context.startActivity(shareIntent)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Share",
                                        tint = ColorCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(onClick = { onProjectDelete(p) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = p.prompt,
                            fontSize = 11.sp,
                            color = ColorTextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ======================================
// 5. COGNITIVE MEMORY VAULT SECTION (HIGH DENSITY INTEL)
// ======================================
@Composable
fun NeuralToggleSwitch(
    title: String,
    desc: String,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = ColorSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, if (isActive) ColorCyan.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title.uppercase(), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = desc, fontSize = 9.sp, color = ColorTextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) ColorCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                    .border(1.dp, if (isActive) ColorCyan else ColorTextSecondary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(2.dp),
                contentAlignment = if (isActive) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (isActive) ColorCyan else ColorTextSecondary)
                )
            }
        }
    }
}

@Composable
fun MemoryVaultSection(
    allMemory: List<MemoryEntity>,
    backgroundLearningEnabled: Boolean,
    privacyConsentGranted: Boolean,
    autoUpdateModeActive: Boolean,
    learningSources: List<String>,
    learningLogs: List<String>,
    monetizationPlan: String,
    seoScoreAdvice: String,
    systemVersion: String,
    systemBackups: List<SystemBackup>,
    systemChangeLogs: List<SystemBackup>,
    activeAiSuggestion: com.example.ui.viewmodel.AiSuggestion?,
    onToggleBackgroundLearning: () -> Unit,
    onTogglePrivacyConsent: () -> Unit,
    onToggleAutoUpdateMode: () -> Unit,
    onSubmitLearningSource: (String) -> Unit,
    onRunSEOAndBusinessInference: () -> Unit,
    onPurgeSystemTelemetry: () -> Unit,
    onSaveParam: (String, String) -> Unit,
    onApplySuggestedAiUpgrade: (com.example.ui.viewmodel.AiSuggestion) -> Unit,
    onrequestSystemUpgrade: (String) -> Unit,
    onRollbackToSystemBackup: (com.example.ui.viewmodel.SystemBackup) -> Unit
) {
    var customKey by remember { mutableStateOf("") }
    var customVal by remember { mutableStateOf("") }
    var externalUrlInput by remember { mutableStateOf("") }
    var manualUpgradeInput by remember { mutableStateOf("") }
    
    // Controlled Update Modal State
    var showConfirmationDialog by remember { mutableStateOf<com.example.ui.viewmodel.AiSuggestion?>(null) }
    
    // Switch between Backups and Changelogs view in the Version panel
    var selectedVersionTab by remember { mutableStateOf("BACKUPS") } // "BACKUPS" or "LOGS"

    if (showConfirmationDialog != null) {
        val suggestion = showConfirmationDialog!!
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = null },
            containerColor = ColorSurface,
            modifier = Modifier.border(1.dp, ColorCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = ColorCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COGNITIVE UPGRADE PERMISSION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Confirm integrating the active self-healing upgrade request?",
                        fontSize = 11.sp,
                        color = ColorTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ColorBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, ColorPurple.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "UPGRADE: ${suggestion.title.uppercase()}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = suggestion.description,
                                fontSize = 9.sp,
                                color = ColorTextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "► Auto-Update Rule: Jarvis will assemble code parameters, write a secure pre-state backup point, and inject compilation files automatically.",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ColorTextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onApplySuggestedAiUpgrade(suggestion)
                        showConfirmationDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorCyan),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "AUTHORIZE PROTOCOL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorBg,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.border(0.5.dp, ColorTextSecondary.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                ) {
                    Text(
                        text = "DISMISS",
                        fontSize = 10.sp,
                        color = ColorTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Section 1: HIGH ENERGY STATUS & CURRENT COGNITIVE VERSION CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x3213151E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(ColorCyan.copy(alpha = 0.6f), ColorPurple.copy(alpha = 0.2f))),
                        shape = RoundedCornerShape(14.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE COGNITIVE CORE SYSTEM",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = ColorTextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "JARVIS CORE INTERFACE: $systemVersion",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        
                        // Radiant Pulse Green Indicator
                        Box(
                            modifier = Modifier
                                .background(ColorGreenGlow.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, ColorGreenGlow, CircleShape)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(ColorGreenGlow, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SECURE // CALIBRATED",
                                    fontSize = 8.sp,
                                    color = ColorGreenGlow,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Your AI Assistant environment continuously supports manual upgrade requests and autonomous self-healing. Restoring any physical backup compiles code instantaneously.",
                        fontSize = 10.sp,
                        color = ColorTextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Section 2: USER-DRIVEN SYSTEM UPGRADE SCHEMAS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, ColorCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(bottom = 14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "► INJECT SYSTEM UPDATE DIRECTIVE (USER-DRIVEN)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorCyan,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Manually request custom additions like \"Add dark theme sidebar with green icons\", \"Optimize index latency\", \"Add SaaS page module\":",
                        fontSize = 9.sp,
                        color = ColorTextSecondary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = manualUpgradeInput,
                            onValueChange = { manualUpgradeInput = it },
                            placeholder = { Text("e.g. Add glowing statistics card...", fontSize = 10.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ColorBg,
                                unfocusedContainerColor = ColorBg,
                                focusedIndicatorColor = ColorCyan
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        )

                        Button(
                            onClick = {
                                if (manualUpgradeInput.isNotBlank()) {
                                    onrequestSystemUpgrade(manualUpgradeInput)
                                    manualUpgradeInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorPurple),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("COMPILE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Section 3: AI-DRIVEN CONTROLLED SELF-IMPROVEMENT SUGGESTIONS
        if (activeAiSuggestion != null) {
            val suggestion = activeAiSuggestion
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x2A1B1E29)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 0.8.dp,
                            brush = Brush.horizontalGradient(listOf(ColorMagenta.copy(alpha = 0.4f), ColorPurple.copy(alpha = 0.1f))),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(bottom = 14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(ColorMagenta, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI COGNITIVE SUGGESTED UPDATE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorMagenta,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "98.4% STATUS CONFIDENCE",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = ColorGreenGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = suggestion.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = suggestion.description,
                            fontSize = 10.sp,
                            color = ColorTextSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showConfirmationDialog = suggestion },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ColorMagenta.copy(alpha = 0.6f), ButtonDefaults.shape)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp), tint = ColorMagenta)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "AUTHORIZE & APPLY AUTO-UPDATE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorMagenta,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Section 4: DYNAMIC ROLLBACK ARCHIVE AND CHANGES HISTORY SEGMENTED HUB
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .padding(bottom = 14.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Segment Tab Selectors
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ColorBg, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedVersionTab == "BACKUPS") Color(0x2800F0FF) else Color.Transparent)
                                .clickable { selectedVersionTab = "BACKUPS" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BACKUPS (${systemBackups.size})",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedVersionTab == "BACKUPS") ColorCyan else ColorTextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedVersionTab == "LOGS") Color(0x2800F0FF) else Color.Transparent)
                                .clickable { selectedVersionTab = "LOGS" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "CHANGE LOGS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedVersionTab == "LOGS") ColorCyan else ColorTextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedVersionTab == "BACKUPS") {
                        if (systemBackups.isEmpty()) {
                            Text(
                                text = "No backup coordinate checkpoints compiled yet.",
                                fontSize = 10.sp,
                                color = ColorTextSecondary,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(10.dp)
                            )
                        } else {
                            systemBackups.forEach { bk ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(ColorBg, RoundedCornerShape(6.dp))
                                        .border(0.5.dp, ColorPurple.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (bk.type == "AI_COGNITIVE") ColorMagenta.copy(alpha = 0.15f) else ColorCyan.copy(alpha = 0.15f),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = bk.version,
                                                    fontSize = 8.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (bk.type == "AI_COGNITIVE") ColorMagenta else ColorCyan
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = bk.title,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = bk.changeLog,
                                            fontSize = 9.sp,
                                            color = ColorTextSecondary,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onRollbackToSystemBackup(bk) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x280DF29F)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier
                                            .border(0.5.dp, ColorGreenGlow.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .height(26.dp)
                                    ) {
                                        Text(
                                            text = "ROLLBACK",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorGreenGlow,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Change Logs List view
                        systemChangeLogs.forEach { log ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(ColorBg, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .background(ColorCyan.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = log.version,
                                                fontSize = 8.sp,
                                                color = ColorCyan,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = log.title,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorTextPrimary
                                        )
                                    }
                                    Text(
                                        text = if (log.type == "AI_COGNITIVE") "AI_AUTO" else "USER_DRIVEN",
                                        fontSize = 7.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (log.type == "AI_COGNITIVE") ColorMagenta else ColorCyan
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = log.changeLog,
                                    fontSize = 9.sp,
                                    color = ColorTextSecondary,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 5: BACKGROUND LEARNING, PRIVACY AND CORE POLICY
        item {
            Text(
                text = "► COGNITIVE BACKBONE SYSTEMS",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = ColorCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            NeuralToggleSwitch(
                title = "Jarvis Background Learning",
                desc = "Passively analyzes user selections, coding lessons, and visual trend patterns.",
                isActive = backgroundLearningEnabled,
                onToggle = onToggleBackgroundLearning
            )
            
            NeuralToggleSwitch(
                title = "Strict User Privacy Shield",
                desc = "Logs require explicit user permission. Flushing active caches guarantees immediate safety.",
                isActive = privacyConsentGranted,
                onToggle = onTogglePrivacyConsent
            )

            NeuralToggleSwitch(
                title = "Autonomous Software Auto-Update",
                desc = "Self-improves generated component templates and repairs bugs natively without codes.",
                isActive = autoUpdateModeActive,
                onToggle = onToggleAutoUpdateMode
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section 6: INPUT FEED FOR TUTORIAL LINKS, YOUTUBE & WEB TRENDS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, ColorPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "FEED VIDEO TUTORIAL / EDUCATION PORTAL LINK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorCyan,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Paste any YouTube web development guides, UX templates, or monetization lessons to update compiler memory:",
                        fontSize = 9.sp,
                        color = ColorTextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = externalUrlInput,
                            onValueChange = { externalUrlInput = it },
                            placeholder = { Text("https://youtube.com/watch?v=...", fontSize = 10.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ColorBg,
                                unfocusedContainerColor = ColorBg,
                                focusedIndicatorColor = ColorCyan
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        )

                        Button(
                            onClick = {
                                if (externalUrlInput.isNotBlank()) {
                                    onSubmitLearningSource(externalUrlInput)
                                    externalUrlInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorCyan),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("FEED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorBg)
                        }
                    }

                    if (learningSources.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ACTIVE INGESTED SOURCES:",
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            color = ColorTextSecondary
                        )
                        learningSources.take(3).forEach { src ->
                            Text(
                                text = "• $src",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = ColorGreenGlow,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Section 7: BUSINESS STRATEGY & SEO OPTIMIZER (HIGH-TEC STRATEGIST)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, ColorCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI BUSINESS STARTER & SEO INSIGHTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .background(ColorCyan.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("AUTONOMOUS", fontSize = 8.sp, color = ColorCyan, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = monetizationPlan,
                        fontSize = 11.sp,
                        color = ColorTextPrimary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.White.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "SEO REVENUE STRATEGY PRESET:",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ColorTextSecondary
                    )
                    Text(
                        text = seoScoreAdvice,
                        fontSize = 10.sp,
                        color = ColorGreenGlow,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRunSEOAndBusinessInference,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ANALYZE REPOSITORY BUSINESS FEASIBILITY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 8: INTERNAL LEARNING/COGNITION ENGINE LOGS
        item {
            Text(
                text = "► REALTIME MODEL SYNAPSE UPGRADE LOGS",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = ColorCyan,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = ColorBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    learningLogs.forEach { log ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(ColorCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = log,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = ColorTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Section 9: MANUAL PARAM WEIGHT INJECTION
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "MANUALLY INJECT CORE NEURAL PARAMETERS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorCyan,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = customKey,
                            onValueChange = { customKey = it },
                            placeholder = { Text("Weight key", fontSize = 10.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ColorBg,
                                unfocusedContainerColor = ColorBg,
                                focusedIndicatorColor = ColorCyan
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        )

                        TextField(
                            value = customVal,
                            onValueChange = { customVal = it },
                            placeholder = { Text("Value", fontSize = 10.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ColorBg,
                                unfocusedContainerColor = ColorBg,
                                focusedIndicatorColor = ColorCyan
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (customKey.isNotBlank() && customVal.isNotBlank()) {
                                onSaveParam(customKey, customVal)
                                customKey = ""
                                customVal = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, ColorCyan.copy(alpha = 0.4f), ButtonDefaults.shape)
                    ) {
                        Text("WRITE WEIGHT INTO SYNAPSE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ColorCyan)
                    }
                }
            }
        }

        // Section 10: PERSISTED VARIABLES IN COGNITION ARCHIVE
        item {
            Text(
                text = "► ACTIVE PERSISTED SYNAPSE VALUES",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = ColorTextSecondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (allMemory.isEmpty()) {
            item {
                Text(
                    text = "Matrix weight cache currently clean.",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = ColorTextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        } else {
            items(allMemory) { m ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .background(ColorSurface, RoundedCornerShape(6.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = m.key.uppercase(),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = ColorCyan
                    )

                    Text(
                        text = m.value,
                        fontSize = 11.sp,
                        color = ColorTextPrimary
                    )
                }
            }
        }

        // Section 11: PRIVACY / DANGER SECURE ERASE ZONE
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onPurgeSystemTelemetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color.Red, ButtonDefaults.shape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.Red)
                Spacer(modifier = Modifier.width(6.dp))
                Text("PURGE ALL PHYSICAL BRAIN TELEMETRY FILES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Red)
            }
        }
    }
}

// ======================================
// 6. HUD DIALOGUE SPEECH INTERRUPTER PANEL
// ======================================
@Composable
fun VoiceHudPanel(
    onDismiss: () -> Unit,
    onCommandSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurface),
            border = BorderStroke(1.dp, ColorCyan),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "► JARVIS VOICE COGNITION HUD v4",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorCyan,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // High Amplitude pulsing circle sound wave
                val infiniteTransition = rememberInfiniteTransition(label = "pulse_circle")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(ColorCyan.copy(alpha = 0.05f * pulseScale), CircleShape)
                        .border(1.5.dp * pulseScale, ColorCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Listening",
                        tint = ColorCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "LISTENING FOR DIRECTION...",
                    color = ColorTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "Sir, select or speak an advanced engineering instruction below to bypass mic latency:",
                    color = ColorTextSecondary,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Quick presets list
                val commands = listOf(
                    "Build an sleek NFT marketplace platform with dark layout",
                    "Design a cyber fitness coach portal with local logs",
                    "Create a minimal AI agency landing page with animations",
                    "Synthesize a space observatory blog",
                    "Add detailed interactive script calculations to the files"
                )

                commands.forEach { c ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(ColorBg, RoundedCornerShape(8.dp))
                            .border(0.5.dp, ColorPurple.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable { onCommandSelected(c) }
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "\"" + c + "\"",
                            fontSize = 11.sp,
                            color = ColorCyan,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onDismiss) {
                    Text("HALT LISTENER MODE", color = Color.Red, fontSize = 11.sp)
                }
            }
        }
    }
}
