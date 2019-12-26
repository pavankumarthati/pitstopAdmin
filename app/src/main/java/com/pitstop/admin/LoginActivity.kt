package com.pitstop.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

val LOGIN_TOP_LEVEL_NAVIGATION = setOf(R.id.PhoneNoLoginFragment)

const val LOGIN_REQUEST_CODE = 1010
const val ACCOUNT_LOGIN_REQUEST_CODE = 1011

class LoginActivity: AppCompatActivity() {

    lateinit var mToolbar: Toolbar
    private val navController by lazy(LazyThreadSafetyMode.NONE) {
        findNavController(R.id.loginNavHostFragment)
    }
    lateinit var mAppBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupNavigation()
    }

    private fun setupNavigation() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mAppBarConfiguration = AppBarConfiguration(LOGIN_TOP_LEVEL_NAVIGATION)
        setupActionBarWithNavController(navController, mAppBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}