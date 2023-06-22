package com.story.fragment

import com.story.fragment.authentication.login.LoginFragmentTest
import com.story.fragment.home.HomeFragmentTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses


@RunWith(Suite::class)
@SuiteClasses(
    LoginFragmentTest::class,
    HomeFragmentTest::class
)
class LoginLogoutTestSuite