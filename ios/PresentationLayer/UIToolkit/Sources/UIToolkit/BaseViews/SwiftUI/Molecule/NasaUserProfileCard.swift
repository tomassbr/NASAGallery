import SFSafeSymbols
import SwiftUI

// MARK: - NasaUserProfileCard
// Avatar circle + name + subtitle row — used at the top of Profile/Settings screens.
public struct NasaUserProfileCard: View {
    private let name: String
    private let subtitle: String
    private let symbol: SFSymbol

    public init(name: String, subtitle: String, symbol: SFSymbol = .personFill) {
        self.name = name
        self.subtitle = subtitle
        self.symbol = symbol
    }

    public var body: some View {
        HStack(spacing: .spaceMD) {
            avatar
            info
            Spacer()
        }
        .padding(.horizontal, .spaceMD)
        .padding(.vertical, .spaceSM)
    }

    private var avatar: some View {
        ZStack {
            Circle()
                .fill(Color.nasaPrimary.opacity(0.15))
                .frame(width: 52, height: 52)
            Image(systemSymbol: symbol)
                .font(.system(size: 22))
                .foregroundStyle(Color.nasaPrimary)
        }
    }

    private var info: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(name)
                .font(.nasaTitleMedium)
                .foregroundStyle(Color.nasaOnBackground)
            Text(subtitle)
                .font(.nasaLabelSmall)
                .foregroundStyle(Color.nasaSubtle)
        }
    }
}
