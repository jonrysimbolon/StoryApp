package com.storyapp.main

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.storyapp.R
import com.storyapp.databinding.ActivityMainBinding
import com.storyapp.utils.LoadingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModel<MainViewModel>()
    private val loadingDialog by lazy { LoadingDialog(this) }

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        observeIsLogin()
        observeLoading()
        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            setTitle(destination)
            configureActionBar(destination)
            configureUpButton(destination)
        }
        backPressed()
    }

    private fun observeLoading() {
        viewModel.loading.observe(this){ show ->
            loadingDialog.show(show)
        }
    }

    private fun observeIsLogin() {
        viewModel.isLogin().observe(this) { isLogin ->
            val destinationId = navHostFragment.findNavController().currentDestination?.id
            val navController = navHostFragment.findNavController()
            if (isLogin) {
                if (destinationId == R.id.authenticationFragment) navController.navigate(R.id.action_authenticationFragment_to_homeFragment)
            } else {
                if (destinationId == R.id.homeFragment) navController.navigate(R.id.action_homeFragment_to_authenticationFragment)
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.show(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBack()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}