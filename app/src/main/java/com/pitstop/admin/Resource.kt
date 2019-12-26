package com.pitstop.admin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import retrofit2.Response

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS,data, null)
        }

        fun <T> error(message: String?, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

interface Mappable<out T: Any?> {
    fun mapToData(): T
    fun isValid(): Boolean
}

sealed class NetworkResult<out T>

data class Success<T>(val response: T) : NetworkResult<T>()

data class NetworkError<T>(val response: T?, val statusCode: Int, val message: String) : NetworkResult<T>()


suspend fun <DATA : Any> Response<DATA>.getResult()
        : NetworkResult<DATA> = try {
    val res = this
    if (res.isSuccessful && res.body() != null) {
        Success(res.body()!!)
    } else {
        NetworkError(null, res.code(), res.message())
    }
} catch (e: Exception) {
    NetworkError(null, -1, e.message ?: e.localizedMessage)
}

suspend fun <RESPONSE: ResponseDt<*>, DATA: Any> makeNetworkCallAsync(block: suspend () -> Response<RESPONSE>):
        DATA? = CallHandler<RESPONSE, DATA>(block).makeCallAsync()

fun <RESPONSE: ResponseDt<*>, DATA: Any> CoroutineScope.makeNetworkCall(block: suspend () -> Response<RESPONSE>):
    MutableLiveData<Resource<DATA>> = CallHandler<RESPONSE, DATA>(block).makeCall(this)

class CallHandler<RESPONSE: Any, DATA: Any>(val block: suspend () -> Response<RESPONSE>) {

    fun makeCall(coroutineScope: CoroutineScope): MutableLiveData<Resource<DATA>> {
        val resourceLiveData = MutableLiveData<Resource<DATA>>()
        resourceLiveData.value = Resource.loading(null)

        coroutineScope.launch {
            supervisorScope {
                val deferred = async {

                    val client: Response<RESPONSE> = block()
                    val result = client.getResult()
                    val resource = when (result) {
                        is Success<RESPONSE> -> Resource.success((result.response as ResponseDt<DATA>).retrieveData())
                        is NetworkError<RESPONSE> -> Resource.error(result.message, null)
                    }
                    resourceLiveData.postValue(resource)
                }

                try {
                    deferred.await()
                } catch (e: Exception) {
                    val resource = Resource.error(e.localizedMessage, null)
                    resourceLiveData.postValue(resource)
                }
            }
        }
        return resourceLiveData
    }

    suspend fun makeCallAsync(): DATA? {
        return try {
            val client: Response<RESPONSE> = block()
            val result = client.getResult()
            when (result) {
                is Success<RESPONSE> -> (result.response as ResponseDt<DATA>).retrieveData()
                is NetworkError<RESPONSE> -> null
            }
        } catch (e: Exception) {
            Log.e("Resource", "failure in makeCallAsync ${e.message}")
            null
        }
    }
}