import SwiftUI
import UIKit

public enum AppTheme {

    /// Semantic color definitions for system UI components (nav bar, tab bar).
    /// Screens and components use NASA color tokens directly via Color.nasaXxx.
    public enum Colors {

        // Navigation bar
        public static let navBarBackground = Color.nasaBackground
        public static let navBarTitle = Color.nasaOnBackground

        // Tab bar
        public static let primaryColor = Color.nasaPrimary

        // Toast / snackbar
        public static let toastSuccessColor = Color.nasaSuccess
        public static let toastErrorColor = Color.nasaError
        public static let toastInfoColor = Color.nasaPrimary

        // Whisper
        public static let whisperBackgroundInfo = Color.nasaSurface
        public static let whisperBackgroundSuccess = Color.nasaSuccess
        public static let whisperBackgroundError = Color.nasaError
        public static let whisperMessage = Color.nasaOnSurface
    }
}
