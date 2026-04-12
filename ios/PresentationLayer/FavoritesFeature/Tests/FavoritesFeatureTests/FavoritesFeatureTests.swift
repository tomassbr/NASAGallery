@testable import FavoritesFeature
import XCTest

final class FavoritesFeatureTests: XCTestCase {
    func testFavoritesFeatureViewExists() throws {
        let view = FavoritesFeatureView()
        XCTAssertNotNil(view)
    }
}
