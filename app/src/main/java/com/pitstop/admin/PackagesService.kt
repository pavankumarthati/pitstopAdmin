package com.pitstop.admin

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PackagesService {
    @GET("admin/package/all")
    suspend fun getPackages(@Query("service_id") serviceId: Long = 6): Response<PackageAllModel>
}

data class PackageAllModel(val serviceId: Long, val city: String, val make: String, val model: String, val packages: List<AddPackageModel>)