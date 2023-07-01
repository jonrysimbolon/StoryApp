package com.jonrysimbolonstory.main

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.jonrysimbolonstory.R
import com.jonrysimbolonstory.databinding.ActivityMainBinding
import com.jonrysimbolonstory.model.UserPreferences
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val userPreferences: UserPreferences by inject()

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        observeIsLogin()
        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            setTitle(destination)
            configureActionBar(destination)
            configureUpButton(destination)
        }
        backPressed()
    }

    private fun observeIsLogin() {
        userPreferences.isLogin().onEach { isLogin ->
            val navController = navHostFragment.findNavController()
            val destinationId = navController.currentDestination?.id
            if (isLogin) {
                if (destinationId == R.id.authenticationFragment) navController.navigate(R.id.action_authenticationFragment_to_homeFragment)
            } else {
                if (destinationId == R.id.homeFragment) navController.navigate(R.id.action_homeFragment_to_authenticationFragment)
            }
        }.launchIn(lifecycleScope)
    }

    private fun setTitle(destination: NavDestination) {
        title = destination.label
    }

    private fun configureActionBar(destination: NavDestination) {
        if (destination.id == R.id.authenticationFragment) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
    }

    private fun configureUpButton(destination: NavDestination) {
        supportActionBar?.setDisplayHomeAsUpEnabled(
            !(destination.id == R.id.homeFragment || destination.id == R.id.authenticationFragment)
        )
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })
    }

    fun onBack() {
        navHostFragment.findNavController().let {
            when (it.currentDestination?.id) {
                R.id.homeFragment -> finish()
                R.id.authenticationFragment -> finish()
                else -> it.popBackStack()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBack()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}