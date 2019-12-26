package com.pitstop.admin

import android.app.Application
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import java.io.InputStreamReader
import javax.inject.Inject

class AdminApp : Application() {

    val appComponent : AppComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    @Inject
    lateinit var retrofit: Retrofit


    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)

        GlobalScope.launch {
            val res = retrofit.create(ConfigService::class.java).config()
            if (res.isSuccessful) {
                ConfigManager.configModel = res.body()?.retrieveData()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AdminApp,
                        "Oops! Unable to fetch configuration",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }
}