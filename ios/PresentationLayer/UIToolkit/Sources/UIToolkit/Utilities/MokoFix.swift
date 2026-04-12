import Foundation
import KMPShared

 #warning("TODO: Remove this workaround when issue [https://github.com/icerockdev/moko-resources/issues/714] is resolved")
 public func fixMokoResourcesForTests() {
    if ProcessInfo.processInfo.processName == "xctest" {
        MokoResourcesWorkaroundKt.nsBundle = Bundle(for: KotlinBase.self)
    }
 }

 #warning("TODO: Remove this workaround when issue [https://github.com/icerockdev/moko-resources/issues/714] is resolved")
 public func fixMokoResourcesForPreviews() {
    if ProcessInfo.processInfo.processName == "XCPreviewAgent" {
        MokoResourcesWorkaroundKt.nsBundle = Bundle(for: KotlinBase.self)
    }
 }
