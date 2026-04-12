@testable import DashboardFeature
import XCTest

final class DashboardFeatureTests: XCTestCase {
    func testDashboardFeatureViewExists() throws {
        // Verify the public entry point can be instantiated
        let view = DashboardFeatureView()
        XCTAssertNotNil(view)
    }
}
