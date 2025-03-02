package com.nafis.coilmovieapp.ui.home

import android.os.Environment
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nafis.coilmovieapp.MainActivity
import com.nafis.coilmovieapp.di.MovieDetailModule.provideMovieDetailApiService
import com.nafis.coilmovieapp.di.MovieDetailModule.provideMovieDetailMapper
import com.nafis.coilmovieapp.di.MovieDetailModule.provideMovieListMapper
import com.nafis.coilmovieapp.movie_detail.data.remote.repository_impl.MovieDetailRepositoryImpl
import com.nafis.coilmovieapp.movie_detail.domain.repository.MovieDetailRepository
import com.nafis.coilmovieapp.ui.detail.DetailViewModel
import com.nafis.coilmovieapp.utils.extractLoadingTimesFromLogcat
import com.nafis.coilmovieapp.utils.writeLoadingTimesToExcel
import com.nafis.coilmovieapp.utils.writeLogcatLoadingTimesToExcel
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeScreenApiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var homeViewModel: HomeViewModel

    @BindValue
    @JvmField
    val savedStateHandle: SavedStateHandle = SavedStateHandle().apply {
        this["id"] = 558449
    }

    @BindValue
    @JvmField
    val movieDetailRepository: MovieDetailRepository = MovieDetailRepositoryImpl(
        movieDetailApiService = provideMovieDetailApiService(),
        apiDetailMapper = provideMovieDetailMapper(),
        apiMovieMapper = provideMovieListMapper()
    )

    @BindValue
    @JvmField
    val detailViewModel: DetailViewModel = DetailViewModel(
        repository = movieDetailRepository,
        savedStateHandle = savedStateHandle
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        homeViewModel.clearLoadingTimes()
        detailViewModel.clearLoadingTimes()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWithRealApiCoil() = runTest(timeout = 300.seconds) {
        // Initialize loading time tracking
        val loadingTimes = mutableMapOf<String, MutableList<Pair<String, Long>>>()

        // Helper function to record loading time
        fun recordLoadingTime(section: String, title: String, time: Long) {
            if (time > 0) {
                // Add image URL placeholder to match Excel format
                val titleWithUrl = "$title-https://image.tmdb.org/t/p/original"
                loadingTimes.getOrPut(section) { mutableListOf() }.add(titleWithUrl to time)
            }
        }

        // Tunggu hingga data dari API selesai diambil
        composeTestRule.waitUntil(30000) {
            homeViewModel.homeState.value.discoverMovies.isNotEmpty() &&
                    homeViewModel.homeState.value.trendingMovies.isNotEmpty()
        }

        // Verifikasi bahwa data dari API ditampilkan di UI
        composeTestRule.onNodeWithTag("Discover LazyRow").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Trending LazyRow").assertIsDisplayed()

        // Ambil data dari ViewModel
        val discoveries = homeViewModel.homeState.value.discoverMovies
        val trendings = homeViewModel.homeState.value.trendingMovies
        val cast = detailViewModel.detailState.value.castList

        // Record initial loading times
        homeViewModel.loadingTimes.forEach { (title, time) ->
            recordLoadingTime("Top Content", title, time)
        }

        // Scroll ke semua item di Discover LazyRow dengan jeda 1 detik
        discoveries.forEachIndexed { index, movie ->
            Log.d("Test", "Scrolling to discover movie at index $index: ${movie.title}")
            composeTestRule.onNodeWithTag("Discover LazyRow").performScrollToIndex(index)
            Thread.sleep(1000)
            composeTestRule.awaitIdle()
        }

        // Record loading times for Discover Movies
        homeViewModel.loadingTimes.forEach { (title, time) ->
            recordLoadingTime("Discover Movie", title, time)
        }

        // Scroll kembali ke awal Discover LazyRow dengan jeda 1 detik
        composeTestRule.onNodeWithTag("Discover LazyRow").performScrollToIndex(0)
        Thread.sleep(1000)
        composeTestRule.awaitIdle()

        // Scroll ke semua item di Trending LazyRow dengan jeda 1 detik
        trendings.forEachIndexed { index, movie ->
            Log.d("Test", "Scrolling to trending movie at index $index: ${movie.title}")
            composeTestRule.onNodeWithTag("Trending LazyRow").performScrollToIndex(index)
            Thread.sleep(1000)
            composeTestRule.awaitIdle()
        }

        // Record loading times for Trending Now
        homeViewModel.loadingTimes.forEach { (title, time) ->
            recordLoadingTime("Trending Now", title, time)
        }

        // Scroll kembali ke awal Trending LazyRow dengan jeda 1 detik
        composeTestRule.onNodeWithTag("Trending LazyRow").performScrollToIndex(0)
        Thread.sleep(1000)
        composeTestRule.awaitIdle()

        // Jeda tambahan untuk memastikan semua gambar selesai dimuat
        Thread.sleep(5000)

        // Log data loading time
        homeViewModel.loadingTimes.forEach { (title, time) ->
            Log.d("LoadingTimeData", "Movie: $title, Loading Time: $time ms")
        }

        // Jeda untuk menampilkan average loading time
        Thread.sleep(5000)

        // Verifikasi bahwa item pertama dari Discover Movies ditampilkan
        val firstDiscovery = homeViewModel.homeState.value.discoverMovies.first()
        Log.d("Test", "Verifying DiscoverMovie_${firstDiscovery.id} is displayed")
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag("DiscoverMovie_${firstDiscovery.id}").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithTag("DiscoverMovie_${firstDiscovery.id}").assertIsDisplayed()

        Thread.sleep(1000)

        // Verifikasi bahwa item pertama dari Trending Movies ditampilkan
        val firstTrending = trendings.first()
        Log.d("Test", "Verifying TrendingMovie_${firstTrending.id} is displayed")
        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithTag("TrendingMovie_${firstTrending.id}").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithTag("TrendingMovie_${firstTrending.id}").assertIsDisplayed()

        Thread.sleep(1000)

        // Klik item pertama dari Discover Movies
        detailViewModel.id = firstDiscovery.id
        composeTestRule.onNodeWithTag("DiscoverMovie_${firstDiscovery.id}").performClick()

        Thread.sleep(1000)

        // Tunggu hingga detail movie selesai dimuat
        composeTestRule.waitUntil(20000) {
            detailViewModel.detailState.value.movieDetail != null
        }

        // Record loading times for Movie Detail
        detailViewModel.loadingTimes.forEach { (title, time) ->
            recordLoadingTime("Movie Detail", title, time)
        }

        // Ambil data movieDetail dari ViewModel
        val movieDetail = detailViewModel.detailState.value.movieDetail!!

        // Verifikasi bahwa detail movie ditampilkan
        composeTestRule.onNodeWithText(movieDetail.title).assertIsDisplayed()

        Thread.sleep(1000)

        // Scroll ke "Actor & Cast" dengan jeda 1 detik
        if (movieDetail.cast.isNotEmpty()) {
            composeTestRule.onNodeWithTag("ActorLazyRow").performScrollToIndex(movieDetail.cast.size - 1)
            Thread.sleep(1000)
            composeTestRule.awaitIdle()
        }

        Thread.sleep(1000)

        // Klik More Cast & Crew
        composeTestRule.onNodeWithContentDescription("More Cast & Crew").performClick()

        composeTestRule.onNodeWithTag("CastCrewScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CastCrewScreen").performScrollToIndex(cast.lastIndex / 2)
        Thread.sleep(1000)
        composeTestRule.awaitIdle()

        // Record loading times for Cast & Crew
        detailViewModel.loadingTimes.forEach { (title, time) ->
            recordLoadingTime("Detail Top Content", title, time)
        }

        composeTestRule.onNodeWithTag("CastCrewScreen").performScrollToIndex(0)
        Thread.sleep(1000)
        composeTestRule.awaitIdle()

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        Thread.sleep(2000)

        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithTag("DetailBodyContent").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Scroll ke "More Like This" hingga paling bawah dengan jeda 1 detik
        composeTestRule.onNodeWithTag("DetailBodyContent").performScrollToIndex(1)
        Thread.sleep(1000)
        composeTestRule.awaitIdle()

        Thread.sleep(1000)

        // Tunggu hingga data "More Like This" selesai dimuat
        composeTestRule.waitUntil(20000) {
            detailViewModel.detailState.value.movies.isNotEmpty()
        }

        Thread.sleep(1000)

        // Scroll pada "More Like This" hanya jika data tidak kosong
        val movies = detailViewModel.detailState.value.movies
        if (movies.isNotEmpty()) {
            val itemCount = composeTestRule.onNodeWithTag("MoreLikeThisLazyRow").fetchSemanticsNode().children.size
            if (itemCount > 0) {
                composeTestRule.onNodeWithTag("MoreLikeThisLazyRow").performScrollToIndex(itemCount - 1)
                Thread.sleep(1000)
                composeTestRule.awaitIdle()
            }
        }

        // Record loading times for More Like This
        detailViewModel.loadingTimes.forEach { (title, time) ->
            recordLoadingTime("More Like This", title, time)
        }

        Thread.sleep(1000)

        // Kembali ke Home Screen
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        Thread.sleep(1000)

        // Verifikasi bahwa Home Screen ditampilkan kembali
        composeTestRule.waitUntil(20000) {
            try {
                composeTestRule.onNodeWithTag("Discover LazyRow").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        Thread.sleep(1000)

        // Click Watchlist
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithContentDescription("DiscoverMovie_Watchlist_${discoveries.first().id}").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        Thread.sleep(1000)

        composeTestRule.onNodeWithContentDescription("DiscoverMovie_Watchlist_${discoveries.first().id}").performClick()
        composeTestRule.onNodeWithContentDescription("Watchlist").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText(discoveries.first().title).isDisplayed()
        Thread.sleep(1000)
        // Go to Home
        composeTestRule.onNodeWithContentDescription("Home").performClick()
        Thread.sleep(1000)
        // Click Favorite
        composeTestRule.onNodeWithContentDescription("DiscoverMovie_Favorite_${discoveries.first().id}").performClick()
        composeTestRule.onNodeWithContentDescription("Favorite").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText(discoveries.first().title).isDisplayed()
        Thread.sleep(1000)
        // Go to Home
        composeTestRule.onNodeWithContentDescription("Home").performClick()

        Thread.sleep(5000)

        // Klik "More discover movies"
        composeTestRule.onNodeWithContentDescription("More discover movies").performClick()

        // Scroll pada Movie LazyColumn dengan jeda 1 detik
        composeTestRule.onNodeWithTag("Movie LazyColumn").performScrollToIndex(discoveries.lastIndex / 2)
        Thread.sleep(1000)
        composeTestRule.awaitIdle()

        composeTestRule.onNodeWithTag("Movie LazyColumn").performScrollToIndex(0)
        Thread.sleep(1000)
        composeTestRule.awaitIdle()

        // Go to Home
        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()

        homeViewModel.loadingTimes
            .filter { it.value > 0 } // Hanya log loading time yang lebih dari 0
            .forEach { (title, time) ->
                Log.d("LoadingTimeData", "Movie: $title, Loading Time: $time ms")
            }

        // Ambil dan gabungkan data loading time dari HomeViewModel dan DetailViewModel
        val combinedLoadingTimes = getCombinedLoadingTimes(homeViewModel, detailViewModel)

        // Log semua data loading time yang sudah digabungkan
        combinedLoadingTimes.forEach { (title, time) ->
            Log.d("CombinedLoadingTime", "Movie: $title, Loading Time: $time ms")
        }

        // Hitung total loading time
        val totalLoadingTime = combinedLoadingTimes.values.sum()
        println("Total combined loading time for this run: $totalLoadingTime ms")
        Log.d("CombinedLoadingTimeResult", "Total combined loading time for this run: $totalLoadingTime ms")

        // Hitung rata-rata loading time
        val validLoadingTimes = combinedLoadingTimes.filter { it.value > 0 }
        val averageLoadingTime = if (validLoadingTimes.isNotEmpty()) {
            validLoadingTimes.values.average()
        } else {
            -1.0 // Jika tidak ada data valid
        }
        println("Average combined loading time for this run: $averageLoadingTime ms")
        Log.d("CombinedLoadingTimeResult", "Average combined loading time for this run: $averageLoadingTime ms")

        // Write loading times to Excel
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        var fileIndex = 1
        var filePath: String

        do {
            filePath = "${downloadsDir.absolutePath}/loading_times_running_$fileIndex.xlsx"
            fileIndex++
        } while (File(filePath).exists())

        writeLoadingTimesToExcel(loadingTimes, filePath)
        Log.d("ExcelExport", "Loading times exported to $filePath")

        // Extract loading times from logcat
        Thread.sleep(5000)
        val logcatLoadingTimes = extractLoadingTimesFromLogcat()

        logcatLoadingTimes.forEach { entry ->
            if (entry.second.first == "Memory Usage") {
                Log.d("MemoryData", "Memory usage for ${entry.second.second}: ${entry.second.third} MB")
            }
        }

        fileIndex = 1

        do {
            filePath = "${downloadsDir.absolutePath}/loading_times_coil_logcat_running_$fileIndex.xlsx"
            fileIndex++
        } while (File(filePath).exists())

        // Gunakan fungsi baru untuk menulis ke Excel
        writeLogcatLoadingTimesToExcel(logcatLoadingTimes, filePath)
        Log.d("ExcelExport", "Logcat loading times exported to $filePath")
    }

    @Test
    fun testEmptyDiscoverMoviesWithRealApi() = runTest {
        // Atur state ViewModel secara manual untuk mensimulasikan data kosong
        homeViewModel.setHomeState(
            HomeState(
                discoverMovies = emptyList(),
                trendingMovies = emptyList(),
                error = null,
                isLoading = false
            )
        )

        // Tunggu hingga UI selesai di-render
        composeTestRule.waitUntil(10000) {
            composeTestRule.onNodeWithTag("Discover LazyRow").assertExists()
            true
        }

        // Verifikasi bahwa tidak ada item yang ditampilkan
        composeTestRule.onNodeWithTag("Favorite_1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("Watchlist_1").assertDoesNotExist()
    }

    private fun getCombinedLoadingTimes(
        homeViewModel: HomeViewModel,
        detailViewModel: DetailViewModel
    ): Map<String, Long> {
        val combinedLoadingTimes = mutableMapOf<String, Long>()

        // Tambahkan loading times dari HomeViewModel yang valid
        homeViewModel.loadingTimes
            .filter { it.value > 0 }
            .forEach { (title, time) ->
                combinedLoadingTimes[title] = time
            }

        // Tambahkan loading times dari DetailViewModel yang valid
        detailViewModel.loadingTimes
            .filter { it.value > 0 }
            .forEach { (title, time) ->
                // Hindari menimpa data yang sudah ada
                if (!combinedLoadingTimes.containsKey(title)) {
                    combinedLoadingTimes[title] = time
                }
            }

        return combinedLoadingTimes
    }
}