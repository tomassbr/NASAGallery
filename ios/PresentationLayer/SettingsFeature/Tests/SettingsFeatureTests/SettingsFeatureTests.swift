@testable import SettingsFeature
import XCTest

final class SettingsFeatureTests: XCTestCase {
    func testSettingsFeatureViewExists() throws {
        let view = SettingsFeatureView()
        XCTAssertNotNil(view)
    }
}
