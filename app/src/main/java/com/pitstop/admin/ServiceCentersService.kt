package com.pitstop.admin

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName


interface ServiceCentersService {
    @GET("admin/service_center/all")
    suspend fun fetchServiceCenters(@Query("city") city: String = "bangalore"): Response<ServiceCentersDt>

    @POST("admin/service_center/create")
    suspend fun createServiceCenter(@Body serviceCenterModel: ServiceCenterModel): Response<ServiceCenterModel>
}


data class ServiceCentersDt(
    @SerializedName("city")
    val city: String?,
    @SerializedName("make")
    val make: String?,
    @SerializedName("service_centers")
    val serviceCenters: List<ServiceCenter>?
)

data class ServiceCenter(
    @SerializedName("address")
    val address: String?,
    @SerializedName("area")
    val area: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("landmark")
    val landmark: String?,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?,
    @SerializedName("make")
    val make: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("primary_number")
    val primaryNumber: Any?
)