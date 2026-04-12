package kmp.android.shared.style

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val NasaTypography = Typography(
    // Display
    h3 = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    h4 = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.5).sp),
    // Headline
    h5 = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    h6 = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    subtitle1 = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    subtitle2 = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    // Body
    body1 = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal),
    body2 = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
    caption = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    overline = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
    // Button
    button = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
)
