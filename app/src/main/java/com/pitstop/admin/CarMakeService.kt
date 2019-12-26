package com.pitstop.admin

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.io.InputStreamReader

inline fun <reified T> Gson.fromJson(reader: JsonReader) : T = this.fromJson<T>(reader, object : TypeToken<T>() {}.type)
inline fun <reified T> Gson.fromJson(jsonString: String?): T = this.fromJson(jsonString, object: TypeToken<T>() {}.type)
inline fun <reified T> Gson.toAJson(src: T) : String = this.toJson(src, object : TypeToken<T>() {}.type)


interface CarMakeService {
    @GET("/car_make_service")
    suspend fun getCarMakerList(context: Context) : Response<out Array<CarMakerEntity>>? {
        return withContext(Dispatchers.IO) {
            //delay(100L)
            val stream = context.resources.openRawResource(R.raw.car_model_make)
            val model = Gson().fromJson<Array<CarMakerEntity>>(JsonReader(InputStreamReader(stream)))
            val result = Response.success(model)
            result
        }
    }
}

object FakeCarMakeService : CarMakeService