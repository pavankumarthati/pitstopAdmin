package com.pitstop.admin

import com.google.gson.Gson
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import retrofit2.http.GET
import java.io.InputStreamReader


interface ConfigService {
    @GET("admin/config")
    suspend fun config(): Response<ConfigModelDt>
}

data class ConfigModelDt(
    @SerializedName("available_models")
    var availableModels: HashMap<String, List<String>>?,
    @SerializedName("available_cities")
    var availableCities: List<String>?,
    @SerializedName("inclusion_list")
    val inclusionList: List<String>?,
    @SerializedName("services_package_details")
    val servicesPackageDetails: HashMap<String, Any>?

) : ResponseDt<ConfigModel>, Mappable<ConfigModel> {
    override fun retrieveData(): ConfigModel {
        return mapToData()
    }

    override fun mapToData(): ConfigModel {
        val carMakesList = arrayListOf<CarMakerEntity>().apply {
            availableModels?.entries?.forEach {
                val carMake = CarMakerEntity(Math.random().toString(), it.key, it.value)
                add(carMake)
            }
        }
        return ConfigModel(availableCities, carMakesList, inclusionList, servicesPackageDetails)
    }

    override fun isValid(): Boolean {
        return true
    }
}


data class ConfigModel(val availableCities: List<String>?, val carMakerEntity: List<CarMakerEntity>?, val inclusionList: List<String>?, val servicesPackageDetails: HashMap<String, Any>?)