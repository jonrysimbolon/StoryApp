package com.story.fragment.authentication.login

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaEditTextInteractions.writeTo
import com.adevinta.android.barista.interaction.BaristaScrollInteractions.scrollTo
import com.story.KoinTestRule
import com.story.R
import com.story.application.customDialogModule
import com.story.application.dataStoreModule
import com.story.application.gsonModule
import com.story.application.repositoryModule
import com.story.application.retrofitModule
import com.story.application.viewModelModule
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

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginFragmentTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            gsonModule,
            retrofitModule(testing_url),
            dataStoreModule,
            repositoryModule,
            customDialogModule,
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
    fun login_Success() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInContainer<LoginFragment>(themeResId = R.style.Theme_StoryApp).onFragment { fragment ->
            navController.setGraph(R.navigation.main_navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        writeTo(R.id.ed_login_email, "email@email.com")
        writeTo(R.id.ed_login_password, "password123")
        scrollTo(R.id.loginBtn)
        clickOn(R.id.loginBtn)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response_login.json"))
        mockWebServer.enqueue(mockResponse)

        val destinationId = navController.currentDestination?.id
        assert(destinationId == R.id.homeFragment)
    }

}