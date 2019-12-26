package com.pitstop.admin

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var userManagement: UserManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = MainViewModel(application)
        val navView: NavigationView = findViewById(R.id.nav_view)
        (application as AdminApp).appComponent.inject(this)

        if (userManagement.getUserId() == null) {
            startActivityForResult(
                Intent(
                    this,
                    LoginActivity::class.java)
                , LOGIN_REQUEST_CODE)
        } else {
            Handler().postDelayed(Runnable {
                profileName.text = userManagement.getUserName()
                profileRole.text = userManagement.getRole()?.value
            }, 100)

        }

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        userManagement.userProfile?.let {
            when (it.role) {
                ROLE.EXECUTIVE -> {

                }
                ROLE.DRIVER -> {

                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        nav_view.setCheckedItem(R.id.nav_home)
        onNavigationItemSelected(nav_view.menu.getItem(0))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                if (supportFragmentManager.findFragmentByTag(OrderListFragment.TAG) != null) return true
                supportFragmentManager.beginTransaction().replace(R.id.content_main ,OrderListFragment(), OrderListFragment.TAG).commit()
            }
            R.id.nav_service_centers -> {
                if (supportFragmentManager.findFragmentByTag(ServiceCentersFragment.TAG) != null) return true
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main ,ServiceCentersFragment(), ServiceCentersFragment.TAG)
                    .commit()
            }
            R.id.nav_employees -> {
                if (supportFragmentManager.findFragmentByTag(EmployeeListFragment.TAG) != null) return true
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main ,EmployeeListFragment(), EmployeeListFragment.TAG)
                    .commit()

            }
            R.id.nav_packages -> {
                if (supportFragmentManager.findFragmentByTag(PackagesFragment.TAG) != null) return true
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main ,PackagesFragment(), PackagesFragment.TAG)
                    .commit()
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(OrderListFragment.TAG) == null) {
            nav_view.setCheckedItem(R.id.nav_home)
            onNavigationItemSelected(nav_view.menu.getItem(0))
        } else {
            super.onBackPressed()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                profileName.text = userManagement.getUserName()
                profileRole.text = userManagement.getRole()?.value
            } else {
                finish()
            }
        }
    }
}

class MainViewModel(
    val app: Application): AndroidViewModel(app) {

    val carMakeModelLiveData = MutableLiveData<CarModel>()

    fun onCarModelSelect(carModel: CarModel) {
        carMakeModelLiveData.postValue(carModel)
    }
}

