package com.pitstop.admin

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query


interface EmployeeService {
    @POST("admin/create_employee")
    suspend fun createEmployee(@Body addEmployeeModel: AddEmployeeModel, @Query("role") role: String): Response<ServiceCenterModel>
    @GET("admin/get_all_executives?city=bangalore")
    suspend fun getEmployees(): Response<EmployeeList>
}


data class EmployeeList(@SerializedName("employees") val employees: List<AddEmployeeModel>)

data class AddEmployeeModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("id_proof_image_url")
    val idProofImageUrl: String?,
    @SerializedName("id_proof_number")
    val idProofNumber: String?,
    @SerializedName("id_proof_type")
    val idProofType: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("profile_photo_url")
    val profilePhotoUrl: String?,
    @SerializedName("role")
    val profileType: String?
)