package com.nafis.coilmovieapp.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nafis.coilmovieapp.MainActivity
import com.nafis.coilmovieapp.movie.domain.models.Movie
import com.nafis.coilmovieapp.movie_detail.domain.models.MovieDetail
import com.nafis.coilmovieapp.ui.FakeMovieDetailRepository
import com.nafis.coilmovieapp.ui.detail.DetailState
import com.nafis.coilmovieapp.ui.detail.DetailViewModel
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var homeViewModel: HomeViewModel

    @BindValue
    @JvmField
    val detailViewModel: DetailViewModel =
        DetailViewModel(FakeMovieDetailRepository(), SavedStateHandle().apply {
            this["id"] = 558449
        })

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testHomeScreenNavigation() = runTest(timeout = 100.seconds) {
        val discoveries = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                posterPath = "/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg",
                backdropPath = "backdrop1",
                genreIds = listOf("1", "2"),
                originalLanguage = "en",
                originalTitle = "Movie 1",
                overview = "Overview 1",
                popularity = 7.5,
                releaseDate = "2023-01-01",
                voteAverage = 8.0,
                voteCount = 100,
                video = false
            )
        )
        val trendings = listOf(
            Movie(
                id = 2,
                title = "Movie 2",
                posterPath = "/cRTctVlwvMdXVsaYbX5qfkittDP.jpg",
                backdropPath = "backdrop2",
                genreIds = listOf("3", "4"),
                originalLanguage = "en",
                originalTitle = "Movie 2",
                overview = "Overview 2",
                popularity = 8.0,
                releaseDate = "2023-02-01",
                voteAverage = 8.5,
                voteCount = 200,
                video = false
            )
        )

        Thread.sleep(2000L)

        println("Discover Movies: ${homeViewModel.homeState.value.discoverMovies.size}")
        println("Trending Movies: ${homeViewModel.homeState.value.trendingMovies.size}")

        // Discover Movies
        // Tunggu hingga UI selesai di-render
        composeTestRule.waitUntil(10000) {
            composeTestRule.onNodeWithTag("Discover LazyRow").assertExists()
            true
        }

        homeViewModel.setHomeState(
            HomeState(
                discoverMovies = discoveries,
                trendingMovies = trendings,
                error = null,
                isLoading = false
            )
        )

        composeTestRule.waitUntil(10000L) {
            composeTestRule.onNodeWithContentDescription(discoveries.first().title).isDisplayed()
        }

        discoveries.forEach { movie ->
            composeTestRule.onNodeWithContentDescription(movie.title).assertIsDisplayed()
        }

        trendings.forEach { movie ->
            composeTestRule.onNodeWithContentDescription(movie.title).assertIsDisplayed()
        }

        Thread.sleep(2000)

        val clickedItem = discoveries.first()

        composeTestRule.onNodeWithContentDescription(clickedItem.title).performClick()

        Thread.sleep(2000)

        composeTestRule.waitForIdle()

        detailViewModel.setDetailState(
            newState = DetailState(
                movieDetail = MovieDetail(
                    posterPath = clickedItem.posterPath,
                    title = clickedItem.title,
                    id = clickedItem.id,
                    voteCount = clickedItem.voteCount,
                    voteAverage = clickedItem.voteAverage,
                    releaseDate = clickedItem.releaseDate,
                    popularity = clickedItem.popularity,
                    overview = clickedItem.overview,
                    originalLanguage = clickedItem.originalLanguage,
                    backdropPath = clickedItem.backdropPath,
                    genreIds = clickedItem.genreIds,
                    originalTitle = clickedItem.originalTitle,
                    video = clickedItem.video,
                    cast = emptyList(),
                    language = emptyList(),
                    productionCountry = emptyList(),
                    reviews = emptyList(),
                    runTime = "",
                ),
                error = null,
                isLoading = false,
                castList = emptyList()
            )
        )

        composeTestRule.waitUntil(10000L) {
            composeTestRule.onNodeWithText(clickedItem.title).isDisplayed()
        }

        Thread.sleep(2000)

        composeTestRule.onNodeWithText(clickedItem.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(clickedItem.title).assertTextEquals(discoveries.first().title)
        composeTestRule.waitUntil(10000L) {
            composeTestRule.onNodeWithContentDescription("DetailImage_${clickedItem.title}").isDisplayed()
        }


        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        Thread.sleep(2000)

        composeTestRule.waitUntil(10000) {
            composeTestRule.onNodeWithTag("Discover LazyRow").isDisplayed()
        }

        Thread.sleep(2000)

        // Click Wishlist
        composeTestRule.waitUntil(10000) {
            composeTestRule.onNodeWithContentDescription("Watchlist_${discoveries.first().id}").isDisplayed()
        }

        composeTestRule.onNodeWithContentDescription("Watchlist_${discoveries.first().id}").performClick()
        composeTestRule.onNodeWithContentDescription("Watchlist").performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithText(discoveries.first().title).isDisplayed()

        Thread.sleep(2000)

        // Go to Home
        composeTestRule.onNodeWithContentDescription("Home").performClick()

        Thread.sleep(2000)

        // Click Favorite
        composeTestRule.onNodeWithContentDescription("Favorite_${discoveries.first().id}").performClick()
        composeTestRule.onNodeWithContentDescription("Favorite").performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithText(discoveries.first().title).isDisplayed()

        Thread.sleep(2000)

        // Go to Home
        composeTestRule.onNodeWithContentDescription("Home").performClick()

        val posterPaths = listOf(
            "/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg",
            "/1sQA7lfcF9yUyoLYC0e6Zo3jmxE.jpg",
            "/x7NPbBlrvFRJrpinBSRlMOOUWom.jpg",
            "/cdqLnri3NEGcmfnqwk2TSIYtddg.jpg",
            "/aosm8NMQ3UyoBVpSxyimorCQykC.jpg",
            "/lurEK87kukWNaHd0zYnsi3yzJrs.jpg",
            "/hE9SAMyMSUGAPsHUGdyl6irv11v.jpg",
            "/2E1x1qcHqGZcYuYi4PzVZjzg8IV.jpg",
            "/m0SbwFNCa9epW1X60deLqTHiP7x.jpg",
            "/cRTctVlwvMdXVsaYbX5qfkittDP.jpg",
        )

        val discoveries2 = (0..9).map {
            Movie(
                id = it,
                title = "Movie $it",
                posterPath = posterPaths[it],
                backdropPath = "backdrop$it",
                genreIds = listOf("1", "2"),
                originalLanguage = "en",
                originalTitle = "Movie $it",
                overview = "Overview $it",
                popularity = 7.5,
                releaseDate = "2023-01-01",
                voteAverage = 8.0,
                voteCount = 100,
                video = false
            )
        }
        val trendings2 = (0..9).map {
            Movie(
                id = it,
                title = "Movie $it",
                posterPath = posterPaths[it],
                backdropPath = "backdrop$it",
                genreIds = listOf("3", "4"),
                originalLanguage = "en",
                originalTitle = "Movie $it",
                overview = "Overview $it",
                popularity = 8.0,
                releaseDate = "2023-02-01",
                voteAverage = 8.5,
                voteCount = 200,
                video = false
            )
        }

        homeViewModel.setHomeState(
            HomeState(
                discoverMovies = discoveries2,
                trendingMovies = trendings2,
                error = null,
                isLoading = false
            )
        )

        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("Discover LazyRow").performScrollToIndex(discoveries2.lastIndex)

        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("Discover LazyRow").performScrollToIndex(0)

        Thread.sleep(2000)

        composeTestRule.onNodeWithContentDescription("More discover movies").performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("Movie LazyColumn").performScrollToIndex(discoveries2.lastIndex / 2)

        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("Movie LazyColumn").performScrollToIndex(0)

        Thread.sleep(2000)
    }

    @Test
    fun testEmptyDiscoverMovies() = runTest {
        // Atur state ViewModel secara manual
        homeViewModel.setHomeState(
            HomeState(
                discoverMovies = emptyList(),
                trendingMovies = emptyList(),
                error = null,
                isLoading = false
            )
        )

        // Tunggu hingga UI selesai di-render
        composeTestRule.waitUntil(100000) {
            composeTestRule.onNodeWithTag("Discover LazyRow").assertExists()
            true
        }

        // Pastikan tidak ada item yang di-render
        composeTestRule.onNodeWithTag("Favorite_1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("Watchlist_1").assertDoesNotExist()
    }
}