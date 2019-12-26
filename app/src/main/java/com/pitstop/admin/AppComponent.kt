package com.pitstop.admin

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Scope
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(app : AdminApp)
    fun inject(fragment: AddServiceCenterScreen)
    fun inject(fragment: AddPackageScreen)
    fun inject(fragment: PackagesFragment)
    fun inject(fragment: EmployeeListFragment)
    fun inject(fragment: AddEmployeeScreen)
    fun inject(fragment: PhoneNoVerificationFragment)
    fun inject(fragment: PhoneNoLoginFragment)
    fun inject(fragment: OrderListFragment)
    fun inject(fragment: ServiceCentersFragment)
}

@Module
class AppModule(val app: Application) {

    @Singleton
    @Provides
    fun getAppContext(): Context = app

    @Singleton
    @Provides
    fun getApplication(): Application = app

    @Singleton
    @Provides
    fun getOkHttpClient(@Named("device_id") deviceId: String, userManagement: UserManagement): OkHttpClient {
        val httpLoggingInterceptor =  HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val requestInterceptor = Interceptor { chain ->
            try {
                val original = chain.request()

                if (original.url().uri().path?.contains("verify_admin_otp") == true) {
                    val requestBuilder =
                        original.newBuilder()
                            .addHeader("x-tenant", "androidapp")
                            .header("x-device-id", deviceId)
                            .header("x-city", "Bangalore")

                    val newRequest = requestBuilder.build()
                    chain.proceed(newRequest)
                } else {
                    val requestBuilder =
                        original.newBuilder()
                            .addHeader("x-tenant", "androidapp")
                            .header("x-device-id", deviceId)
                            .header("x-city", "Bangalore")
                            .header(
                                "x-access-token",
                                userManagement?.userProfile?.accessToken ?: ""
                            )

                    val newRequest = requestBuilder.build()
                    chain.proceed(newRequest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(500).body(
                    ResponseBody.create(MediaType.parse("text/plain"), "No Content")).message("network error occurred").build()

            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://23.101.141.184:8070/v1/")
            .build()
    }

    @Singleton
    @Provides
    fun getOtpService(retrofit: Retrofit): CcService {
        return retrofit.create(CcService::class.java)
    }

    @Named("USER_PREF_FILE_NAME")
    @Provides
    @Singleton
    fun getPrefFileName(): String = "userPrefs"

    @Named("user_pref")
    @Singleton
    @Provides
    fun getSharedPreference(@Named("USER_PREF_FILE_NAME") prefFileName: String): SharedPreferences =
        app.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    @Named("device_id")
    fun getAndroidId(): String {
        return Settings.Secure.getString(app.contentResolver,
            Settings.Secure.ANDROID_ID)
    }

}

@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class FragmentScope