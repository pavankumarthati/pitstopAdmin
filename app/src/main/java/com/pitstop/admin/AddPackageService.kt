package com.pitstop.admin

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName


interface AddPackageService {
    @POST("admin/package/create")
    suspend fun addPackage(@Body serviceCenterModel: AddPackageModel): Response<AddPackageModel>
}

data class AddPackageModel(
    @SerializedName("car_make")
    val carMake: String?,
    @SerializedName("car_model")
    val carModel: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("discounted_price")
    val discountedPrice: Int?,
    @SerializedName("inclusions")
    val inclusions: List<String?>?,
    @SerializedName("metadata")
    val metadata: Metadata?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("price")
    val price: Int?,
    @SerializedName("service_id")
    val serviceId: Int = 6
)

data class Metadata(
    @SerializedName("imageId")
    val imageId: String?
)