package com.pitstop.admin

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().add(R.id.content_main ,OrderListFragment()).commit()
            }
            R.id.nav_service_centers -> {
                supportFragmentManager.beginTransaction().add(R.id.content_main ,ServiceCentersFragment()).commit()
            }
            R.id.nav_employees -> {

            }
            R.id.nav_tools -> {

            }
        }
        return true
    }
}
