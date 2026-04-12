package kmp.android.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kmp.android.shared.style.NasaColor
import kmp.android.shared.style.Radius
import kmp.android.shared.style.Space

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NasaChip(
    title: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Radius.Full),
        color = if (isSelected) MaterialTheme.colors.primary else NasaColor.SurfaceElevated,
        border = if (!isSelected) BorderStroke(1.dp, NasaColor.Subtle) else null,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.overline,
            color = if (isSelected) NasaColor.OnPrimary else NasaColor.OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = Space.MD, vertical = Space.SM),
        )
    }
}
