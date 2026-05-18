package kmp.android.shared.style

import androidx.compose.ui.graphics.Color

// Source of truth: Figma 32OyUPY6rvNLgEpVgKWV92 — Style Guide color tokens
object NasaColor {
    // Backgrounds
    val Background          = Color(0xFF0b0d14)  // bg/canvas
    val BackgroundSecondary = Color(0xFF050608)  // bg/base

    // Surfaces
    val Surface             = Color(0xFF12151f)  // surface/level1 — cards
    val SurfaceElevated     = Color(0xFF161925)  // surface/level2 — skeleton/hover

    // Content
    val OnBackground        = Color(0xFFf1f5f9)  // text/primary   (slate-100)
    val OnSurface           = Color(0xFFf1f5f9)  // text/primary
    val OnSurfaceVariant    = Color(0xFFcbd5e1)  // text/secondary (slate-300)
    val Subtle              = Color(0xFF64748b)  // text/muted     (slate-500)

    // Brand
    val Primary             = Color(0xFF818cf8)  // accent/primary  (indigo-400)
    val OnPrimary           = Color(0xFFFFFFFF)
    val Accent              = Color(0xFF2dd4bf)  // accent/secondary (teal-400)

    // Semantic
    val Success             = Color(0xFF22c55e)
    val Error               = Color(0xFFfb7185)  // status/error (rose-400)
    val OnError             = Color(0xFFFFFFFF)
    val Warning             = Color(0xFFf59e0b)  // status/warning (amber-500)

    // Borders
    val BorderSubtle        = Color(0xFF1e2333)  // border/subtle

    // Overlays
    val MediaOverlay        = Color(0x73000000)
}
