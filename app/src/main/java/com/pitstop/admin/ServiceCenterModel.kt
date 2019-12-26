package com.pitstop.admin
import com.google.gson.annotations.SerializedName

data class ServiceCenterModel(
    @SerializedName("id")
    val id: Long,
    @SerializedName("deleted")
    val deleted: Boolean,
    @SerializedName("address")
    val address: String?,
    @SerializedName("area")
    val area: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?,
    @SerializedName("make")
    val make: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("primary_number")
    val primaryNumber: String?
)