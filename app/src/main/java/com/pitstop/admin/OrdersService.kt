package com.pitstop.admin

import com.google.gson.Gson
import retrofit2.Response
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import retrofit2.http.*
import java.io.InputStreamReader


interface OrderService {
    @GET("admin/orders/{city}")
    suspend fun fetchOrders(@Path("city") city: String, @Query("role") role: String?= null, @Query("employee_id") employeeId: Long? = null): Response<OrdersDt>
}


data class OrdersDt(
    @SerializedName("orders")
    val orders: List<Order>?
)

data class Order(
    @SerializedName("assigned_driver_details")
    val assignedDriverDetails: AssignedDriverDetails?,
    @SerializedName("assigned_driver_id")
    val assignedDriverId: Int?,
    @SerializedName("assigned_executive_details")
    val assignedExecutiveDetails: AssignedExecutiveDetails?,
    @SerializedName("assigned_executive_id")
    val assignedExecutiveId: Int?,
    @SerializedName("cart")
    val cart: Cart?,
    @SerializedName("cart_value")
    val cartValue: Double?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("created")
    val created: Long?,
    @SerializedName("customer_pickup_lat")
    val customerPickupLat: Double?,
    @SerializedName("customer_pickup_lng")
    val customerPickupLng: Double?,
    @SerializedName("date_time_slot")
    val dateTimeSlot: Long?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("order_id")
    val orderId: String?,
    @SerializedName("pickup")
    val pickup: Boolean?,
    @SerializedName("pickup_address")
    val pickupAddress: String?,
    @SerializedName("service_center_details")
    val serviceCenterDetails: ServiceCenterDetails?,
    @SerializedName("service_center_id")
    val serviceCenterId: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("vehicle_id")
    val vehicleId: Int?,
    @SerializedName("vehicle_metadata")
    val vehicleMetadata: VehicleMetadata?
)

data class AssignedDriverDetails(
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("deleted")
    val deleted: Boolean?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("id_proof_image_url")
    val idProofImageUrl: String?,
    @SerializedName("id_proof_number")
    val idProofNumber: String?,
    @SerializedName("id_proof_type")
    val idProofType: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("profile_photo_url")
    val profilePhotoUrl: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("token_details")
    val tokenDetails: Any?
)

data class AssignedExecutiveDetails(
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("deleted")
    val deleted: Boolean?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("id_proof_image_url")
    val idProofImageUrl: String?,
    @SerializedName("id_proof_number")
    val idProofNumber: String?,
    @SerializedName("id_proof_type")
    val idProofType: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("profile_photo_url")
    val profilePhotoUrl: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("token_details")
    val tokenDetails: Any?
)

data class Cart(
    @SerializedName("complaints")
    val complaints: List<String?>?,
    @SerializedName("package_ids")
    val packageIds: List<Int?>?,
    @SerializedName("selected_package_details")
    val selectedPackageDetails: List<SelectedPackageDetail?>?
)

data class SelectedPackageDetail(
    @SerializedName("city")
    val city: String?,
    @SerializedName("discountedPrice")
    val discountedPrice: Double?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("inclusions")
    val inclusions: List<String?>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("price")
    val price: Double?
)

data class ServiceCenterDetails(
    @SerializedName("address")
    val address: String?,
    @SerializedName("area")
    val area: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("landmark")
    val landmark: Any?,
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

data class VehicleMetadata(
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("fuel_type")
    val fuelType: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("kms_travelled")
    val kmsTravelled: Int?,
    @SerializedName("make")
    val make: String?,
    @SerializedName("make_year")
    val makeYear: Int?,
    @SerializedName("model")
    val model: String?,
    @SerializedName("registration_number")
    val registrationNumber: String?,
    @SerializedName("vehicle_type")
    val vehicleType: String?
)