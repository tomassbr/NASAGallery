import Foundation

public enum ApiFlavor {
    case alpha
    case production
}

public enum BuildVariant {
    case debug
    case release
}

/// Process-wide environment (set from `AppDelegate` on launch, read from main + KMP bridge).
private final class EnvironmentStore: @unchecked Sendable {
    static let shared = EnvironmentStore()

    private let lock = NSLock()
    private var _api: ApiFlavor = .alpha
    private var _build: BuildVariant = .debug
    private var _locale: Locale = .current

    var api: ApiFlavor {
        get { lock.withLock { _api } }
        set { lock.withLock { _api = newValue } }
    }

    var build: BuildVariant {
        get { lock.withLock { _build } }
        set { lock.withLock { _build = newValue } }
    }

    var locale: Locale {
        get { lock.withLock { _locale } }
        set { lock.withLock { _locale = newValue } }
    }
}

private extension NSLock {
    func withLock<T>(_ body: () -> T) -> T {
        lock()
        defer { unlock() }
        return body()
    }
}

public enum Environment {
    public static var api: ApiFlavor {
        get { EnvironmentStore.shared.api }
        set { EnvironmentStore.shared.api = newValue }
    }

    public static var build: BuildVariant {
        get { EnvironmentStore.shared.build }
        set { EnvironmentStore.shared.build = newValue }
    }

    public static var locale: Locale {
        get { EnvironmentStore.shared.locale }
        set { EnvironmentStore.shared.locale = newValue }
    }
}
