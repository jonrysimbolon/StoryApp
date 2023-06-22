package com.story.fragment.home


import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.story.KoinTestRule
import com.story.R
import com.story.application.adapterModule
import com.story.application.bitmapModule
import com.story.application.customDialogModule
import com.story.application.dataStoreModule
import com.story.application.glideModule
import com.story.application.gsonModule
import com.story.application.repositoryModule
import com.story.application.retrofitModule
import com.story.application.viewModelModule
import com.story.local.StoryDatabase
import com.story.utils.EspressoIdlingResource
import com.story.utils.JsonConverter
import com.story.utils.Utils.testing_url
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeFragmentTest {

    private val localTestModule = module {
        single {
            Room.inMemoryDatabaseBuilder(
                androidContext(),
                StoryDatabase::class.java
            ).allowMainThreadQueries().build()
        }
    }

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            gsonModule,
            retrofitModule(testing_url),
            localTestModule,
            dataStoreModule,
            glideModule,
            repositoryModule,
            bitmapModule,
            customDialogModule,
            adapterModule,
            viewModelModule,
        )
    )

    private lateinit var mockWebServer: MockWebServer

    private fun startMockWebServer(): MockWebServer {
        val server = MockWebServer()
        server.start(8080)
        return server
    }

    private fun stopMockWebServer() {
        if (::mockWebServer.isInitialized) {
            mockWebServer.shutdown()
        }
    }

    @Before
    fun setUp() {
        mockWebServer = startMockWebServer()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        stopMockWebServer()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun logout_success() {

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        val scenario = launchFragmentInContainer<HomeFragment>()

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.main_navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response_home.json"))
        mockWebServer.enqueue(mockResponse)

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        onView(withText(R.string.logout)).perform(click())

        scenario.onFragment { fragment ->
            fragment.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToAuthenticationFragment())
        }

        val destinationId = navController.currentDestination?.id
        assert(destinationId == R.id.authenticationFragment)
    }
}
