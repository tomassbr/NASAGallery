package kmp.android.apod.ui

import android.app.DatePickerDialog
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cz.tomasbrand.nasagallery.apod.presentation.ApodEvent
import cz.tomasbrand.nasagallery.apod.presentation.ApodIntent
import cz.tomasbrand.nasagallery.apod.presentation.ApodState
import cz.tomasbrand.nasagallery.apod.presentation.ApodViewModel
import kmp.android.shared.components.ApodHeroCard
import kmp.android.shared.components.NasaErrorView
import kmp.android.shared.components.NasaFullScreenLoadingView
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Space
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
internal fun TodayRoute(navController: NavHostController) {
    val viewModel: ApodViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.onViewAppeared()
        viewModel.events.collectLatest { event ->
            when (event) {
                is ApodEvent.Share -> {
                    val apod = event.apod
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, apod.title)
                        putExtra(Intent.EXTRA_TEXT, "${apod.title}\n\n${apod.url}")
                    }
                    context.startActivity(Intent.createChooser(intent, "Share APOD"))
                }
                else -> Unit
            }
        }
    }

    TodayScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun TodayScreen(
    state: ApodState,
    onIntent: (ApodIntent) -> Unit,
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dateStr = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                onIntent(ApodIntent.LoadForDate(dateStr))
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            setOnCancelListener { showDatePicker = false }
        }.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NasaColor.Background),
    ) {
        TopAppBar(
            title = { Text("Today", style = MaterialTheme.typography.h6, color = NasaColor.OnBackground) },
            backgroundColor = NasaColor.Background,
            elevation = 0.dp,
            actions = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Pick date",
                        tint = MaterialTheme.colors.primary,
                    )
                }
            },
        )

        when {
            state.isLoading -> {
                NasaFullScreenLoadingView()
            }

            state.apod != null -> {
                val apod = state.apod!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Space.screenHorizontal),
                ) {
                    ApodHeroCard(
                        title = apod.title,
                        imageUrl = apod.displayUrl,
                        date = apod.date,
                        copyright = apod.copyright,
                        onShare = { onIntent(ApodIntent.Share) },
                        onViewFullscreen = { onIntent(ApodIntent.OpenFullscreen(apod)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(Space.MD))

                    Text(
                        text = "Explanation",
                        style = MaterialTheme.typography.subtitle1,
                        color = NasaColor.OnSurface,
                    )

                    Spacer(Modifier.height(Space.SM))

                    Text(
                        text = apod.explanation,
                        style = MaterialTheme.typography.body2,
                        color = NasaColor.OnSurfaceVariant,
                    )

                    Spacer(Modifier.height(Space.XXXL))
                }
            }

            state.error != null -> {
                NasaErrorView(
                    message = state.error?.throwable?.message ?: "Something went wrong",
                    modifier = Modifier.fillMaxSize().padding(Space.screenHorizontal),
                    onRetry = { onIntent(ApodIntent.LoadToday) },
                )
            }
        }
    }
}
