package kmp.android.shared.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val nasaDarkColors = darkColors(
    primary          = NasaColor.Primary,
    primaryVariant   = NasaColor.Primary,
    secondary        = NasaColor.Accent,
    secondaryVariant = NasaColor.Accent,
    background       = NasaColor.Background,
    surface          = NasaColor.Surface,
    error            = NasaColor.Error,
    onPrimary        = NasaColor.OnPrimary,
    onSecondary      = NasaColor.OnBackground,
    onBackground     = NasaColor.OnBackground,
    onSurface        = NasaColor.OnSurface,
    onError          = NasaColor.OnError,
)

val NasaShapes = Shapes(
    small  = RoundedCornerShape(Radius.SM),
    medium = RoundedCornerShape(Radius.Card),
    large  = RoundedCornerShape(Radius.Button),
)

@Composable
fun NasaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors     = nasaDarkColors,
        typography = NasaTypography,
        shapes     = NasaShapes,
        content    = content,
    )
}
