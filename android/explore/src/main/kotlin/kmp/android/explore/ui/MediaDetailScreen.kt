package kmp.android.explore.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cz.tomasbrand.nasagallery.gallery.domain.model.GalleryItem
import cz.tomasbrand.nasagallery.gallery.presentation.GalleryViewModel
import kmp.android.shared.components.NasaAsyncImage
import kmp.android.shared.components.NasaEmptyView
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Space
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun MediaDetailRoute(nasaId: String, navController: NavHostController) {
    val galleryViewModel: GalleryViewModel = koinViewModel()
    val state by galleryViewModel.state.collectAsStateWithLifecycle()
    val item = state.items.firstOrNull { it.nasaId == nasaId }

    MediaDetailScreen(
        item = item,
        onBack = { navController.popBackStack() },
    )
}

@Composable
private fun MediaDetailScreen(
    item: GalleryItem?,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NasaColor.Background),
    ) {
        TopAppBar(
            title = {},
            backgroundColor = NasaColor.Background,
            elevation = 0.dp,
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NasaColor.OnBackground,
                    )
                }
            },
            actions = {
                if (item != null) {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, item.title)
                            putExtra(Intent.EXTRA_TEXT, "${item.title}\n${item.thumbnailUrl}")
                        }
                        context.startActivity(Intent.createChooser(intent, "Share"))
                    }) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = NasaColor.OnBackground,
                        )
                    }
                }
            },
        )

        if (item == null) {
            NasaEmptyView(
                title = "Not Found",
                message = "This item is no longer available.",
                modifier = Modifier.fillMaxSize().padding(Space.screenHorizontal),
            )
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            NasaAsyncImage(
                url = item.thumbnailUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )

            Column(modifier = Modifier.padding(Space.screenHorizontal)) {
                Spacer(Modifier.height(Space.MD))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.h5,
                    color = NasaColor.OnBackground,
                )

                Spacer(Modifier.height(Space.SM))

                Row {
                    item.dateCreated?.let {
                        Text(text = it, style = MaterialTheme.typography.caption, color = NasaColor.OnSurfaceVariant)
                    }
                    item.photographer?.let {
                        Text(text = " · $it", style = MaterialTheme.typography.caption, color = NasaColor.OnSurfaceVariant)
                    }
                }

                if (!item.description.isNullOrBlank()) {
                    Spacer(Modifier.height(Space.MD))
                    Text(
                        text = item.description!!,
                        style = MaterialTheme.typography.body2,
                        color = NasaColor.OnSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(Space.XXXL))
            }
        }
    }
}
