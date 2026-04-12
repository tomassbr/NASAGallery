import Factory
import KMPShared
import SharedDomain

public extension Container {
    // Koin singleton
    private var kmp: Factory<KMPDependency> { self { KMPKoinDependency() }.singleton }

    // NASA Gallery
    var authViewModel: Factory<AuthViewModel> { self { self.kmp().get(AuthViewModel.self) } }
    var apodViewModel: Factory<ApodViewModel> { self { self.kmp().get(ApodViewModel.self) } }
    var galleryViewModel: Factory<GalleryViewModel> { self { self.kmp().get(GalleryViewModel.self) } }
    var searchViewModel: Factory<SearchViewModel> { self { self.kmp().get(SearchViewModel.self) } }
    var favoritesViewModel: Factory<FavoritesViewModel> { self { self.kmp().get(FavoritesViewModel.self) } }
    var profileViewModel: Factory<ProfileViewModel> { self { self.kmp().get(ProfileViewModel.self) } }

    // Sample
    var sampleFeatureViewModel: Factory<SampleFeatureViewModel> { self { self.kmp().get(SampleFeatureViewModel.self) } }
}
