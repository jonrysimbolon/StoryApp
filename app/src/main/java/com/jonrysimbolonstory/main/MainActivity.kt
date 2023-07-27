package com.jonrysimbolonstory.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.jonrysimbolonstory.R
import com.jonrysimbolonstory.databinding.ActivityMainBinding
import com.jonrysimbolonstory.model.UserPreferences
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val userPreferences: UserPreferences by inject()

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navHostFragment.findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        observeIsLogin()
        setupActionBarWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            configureActionBar(destination)
        }
    }

    private fun configureActionBar(destination: NavDestination) {
        if (destination.id == R.id.authenticationFragment) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
    }

    private fun observeIsLogin() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userPreferences.isLogin().collect { isLogin ->
                    val destinationId = navController.currentDestination?.id
                    if (isLogin) {
                        if (destinationId == R.id.authenticationFragment)
                            navController.navigate(R.id.action_authenticationFragment_to_homeFragment)
                    } else {
                        if (destinationId == R.id.homeFragment)
                            navController.navigate(R.id.action_homeFragment_to_authenticationFragment)
                    }
                }
            }
        }
    }

    private fun onBack() {
        navController.popBackStack()
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