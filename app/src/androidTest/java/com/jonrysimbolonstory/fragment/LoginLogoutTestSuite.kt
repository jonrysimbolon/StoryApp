package com.jonrysimbolonstory.fragment

import com.jonrysimbolonstory.fragment.authentication.login.LoginFragmentTest
import com.jonrysimbolonstory.fragment.home.HomeFragmentTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses


@RunWith(Suite::class)
@SuiteClasses(
    LoginFragmentTest::class,
    HomeFragmentTest::class
)
class LoginLogoutTestSuite