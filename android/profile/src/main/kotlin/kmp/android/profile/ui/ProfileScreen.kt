package kmp.android.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.tomasbrand.nasagallery.profile.presentation.ProfileEvent
import cz.tomasbrand.nasagallery.profile.presentation.ProfileIntent
import cz.tomasbrand.nasagallery.profile.presentation.ProfileState
import cz.tomasbrand.nasagallery.profile.presentation.ProfileViewModel
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Radius
import kmp.android.shared.style.Space
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ProfileRoute() {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var apiKeyDraft by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.onViewAppeared()
        viewModel.events.collectLatest { event ->
            when (event) {
                ProfileEvent.CacheCleared -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Cache cleared")
                    }
                }
            }
        }
    }

    LaunchedEffect(state.isEditingApiKey) {
        if (state.isEditingApiKey) {
            apiKeyDraft = state.apiKey
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        backgroundColor = NasaColor.Background,
    ) { innerPadding ->
        ProfileScreen(
            modifier = Modifier.padding(innerPadding),
            state = state,
            onIntent = viewModel::onIntent,
            onRequestClearCacheDialog = { showClearCacheDialog = true },
        )
    }

    if (showClearCacheDialog) {
        ClearCacheDialog(
            onDismiss = { showClearCacheDialog = false },
            onConfirm = {
                viewModel.onIntent(ProfileIntent.ClearCache)
                showClearCacheDialog = false
            },
        )
    }

    if (state.isEditingApiKey) {
        ApiKeyEditorDialog(
            draft = apiKeyDraft,
            onDraftChange = { apiKeyDraft = it },
            onDismiss = { viewModel.onIntent(ProfileIntent.CancelApiKeyEdit) },
            onSave = { viewModel.onIntent(ProfileIntent.SaveApiKey(apiKeyDraft)) },
        )
    }
}

@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    onRequestClearCacheDialog: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NasaColor.Background),
    ) {
        ProfileTopBar()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.screenHorizontal),
        ) {
            UserCard()
            SectionHeader("PREFERENCES")
            PreferencesSection(
                darkTheme = state.darkThemeEnabled,
                dataSaver = state.dataSaverEnabled,
                onDarkTheme = { onIntent(ProfileIntent.ToggleDarkTheme(it)) },
                onDataSaver = { onIntent(ProfileIntent.ToggleDataSaver(it)) },
            )
            SectionHeader("DATA & STORAGE")
            StorageSection(onClearCache = onRequestClearCacheDialog)
            SectionHeader("ADVANCED")
            AdvancedSection(
                apiKey = state.apiKey,
                onEditApiKey = { onIntent(ProfileIntent.EditApiKey) },
            )
        }
    }
}

// MARK: - Top Bar

@Composable
private fun ProfileTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(text = "Settings", style = MaterialTheme.typography.h6, color = NasaColor.OnBackground)
                Text(text = "PREFERENCES", style = MaterialTheme.typography.overline, color = NasaColor.Subtle)
            }
        },
        backgroundColor = NasaColor.Background,
        elevation = 0.dp,
    )
}

// MARK: - User Card

@Composable
private fun UserCard() {
    SettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Space.MD),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserAvatar()
            Spacer(Modifier.width(Space.MD))
            UserInfo()
        }
    }
}

@Composable
private fun UserAvatar() {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = null,
        tint = NasaColor.Subtle,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(NasaColor.SurfaceElevated)
            .padding(Space.SM),
    )
}

@Composable
private fun UserInfo() {
    Column {
        Text(text = "Local Explorer", style = MaterialTheme.typography.body1, color = NasaColor.OnSurface)
        Text(text = "Device Storage", style = MaterialTheme.typography.overline, color = NasaColor.Subtle)
    }
}

// MARK: - Preferences

@Composable
private fun PreferencesSection(
    darkTheme: Boolean,
    dataSaver: Boolean,
    onDarkTheme: (Boolean) -> Unit,
    onDataSaver: (Boolean) -> Unit,
) {
    SettingsCard {
        SettingsToggleRow(
            icon = Icons.Default.NightsStay,
            title = "Dark Theme",
            checked = darkTheme,
            onCheckedChange = onDarkTheme,
        )
        Divider(color = NasaColor.SurfaceElevated)
        SettingsToggleRow(
            icon = Icons.Default.SignalWifiOff,
            title = "Data Saver",
            checked = dataSaver,
            onCheckedChange = onDataSaver,
        )
    }
}

// MARK: - Storage

@Composable
private fun StorageSection(onClearCache: () -> Unit) {
    SettingsCard {
        SettingsActionRow(icon = Icons.Default.Download, title = "Offline Downloads", value = "0 MB")
        Divider(color = NasaColor.SurfaceElevated)
        SettingsActionRow(
            icon = Icons.Default.Delete,
            title = "Clear Image Cache",
            isDestructive = true,
            onClick = onClearCache,
        )
    }
}

// MARK: - Advanced

@Composable
private fun AdvancedSection(
    apiKey: String,
    onEditApiKey: () -> Unit,
) {
    SettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Space.MD),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "NASA API Key", style = MaterialTheme.typography.body2, color = NasaColor.OnSurface)
                Text(
                    text = apiKey,
                    style = MaterialTheme.typography.caption,
                    color = NasaColor.Subtle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TextButton(onClick = onEditApiKey) {
                Text(text = "Edit", style = MaterialTheme.typography.caption, color = NasaColor.Primary)
            }
        }
    }
}

// MARK: - Dialogs

@Composable
private fun ClearCacheDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear Image Cache") },
        text = {
            Text("All cached images will be removed. They will be re-downloaded when needed.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Clear", color = NasaColor.Error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NasaColor.Subtle)
            }
        },
        backgroundColor = NasaColor.Surface,
        contentColor = NasaColor.OnSurface,
    )
}

@Composable
private fun ApiKeyEditorDialog(
    draft: String,
    onDraftChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("NASA API Key") },
        text = {
            Column {
                Text(
                    text = "Enter your NASA API key from api.nasa.gov",
                    style = MaterialTheme.typography.body2,
                    color = NasaColor.Subtle,
                    modifier = Modifier.padding(bottom = Space.SM),
                )
                TextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = NasaColor.OnBackground,
                        backgroundColor = NasaColor.Surface,
                        cursorColor = NasaColor.Primary,
                        focusedIndicatorColor = NasaColor.Primary,
                        unfocusedIndicatorColor = NasaColor.Subtle,
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save", color = NasaColor.Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NasaColor.Subtle)
            }
        },
        backgroundColor = NasaColor.Surface,
        contentColor = NasaColor.OnSurface,
    )
}

// MARK: - Reusable Components

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.overline,
        color = NasaColor.Subtle,
        modifier = Modifier.padding(top = Space.MD, bottom = Space.XS),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Card))
            .background(NasaColor.Surface),
    ) {
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(Space.MD),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = NasaColor.Primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Space.MD))
        Text(text = title, style = MaterialTheme.typography.body2, color = NasaColor.OnSurface, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NasaColor.Primary,
                checkedTrackColor = NasaColor.Primary.copy(alpha = 0.5f),
                uncheckedThumbColor = NasaColor.Subtle,
                uncheckedTrackColor = NasaColor.SurfaceElevated,
            ),
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String? = null,
    isDestructive: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val tint = if (isDestructive) NasaColor.Error else NasaColor.Accent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(Space.MD),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(Space.MD))
        Text(
            text = title,
            style = MaterialTheme.typography.body2,
            color = if (isDestructive) NasaColor.Error else NasaColor.OnSurface,
            modifier = Modifier.weight(1f),
        )
        if (value != null) {
            Text(text = value, style = MaterialTheme.typography.caption, color = NasaColor.Subtle)
        }
    }
}
