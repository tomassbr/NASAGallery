import OSLog

public extension Logger {
    static let app = Logger(subsystem: Bundle.main.bundleIdentifier ?? "-", category: "App")
    static let lifecycle = Logger(subsystem: Bundle.main.bundleIdentifier ?? "-", category: "Lifecycle")
}
