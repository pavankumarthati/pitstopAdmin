package com.pitstop.admin

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import retrofit2.Response
import retrofit2.http.*

interface CcService {
    @POST("user/workflow/generate_admin_otp")
    suspend fun getPhoneLoginResponse(@Body phoneLoginRequest: PhoneLoginRequest, @Header("x-country") country: String): Response<PhoneLoginDt>
    @POST("user/workflow/verify_admin_otp")
    suspend fun getPhoneVerificationResponse(@Body phoneVerifyRequest: PhoneVerifyRequest, @Header("x-country") country: String, @Header("x-access-token") accessToken: String): Response<PhoneVerifyDt>
    @GET("v1/user/{id}")
    suspend fun getProfile(@Path("id") id: Long, @Header("x-country") country: String): Response<PhoneVerifyDt>
    @PUT("v1/user/{id}")
    suspend fun editProfile(@Path("id") id: Long, @Body editUserProfileRequest: EditUserProfileRequest, @Header("x-country") country: String): Response<PhoneVerifyDt>
    @GET("v1/user/{id}/address/all")
    suspend fun getAddresses(@Path("id") id: Long, @Header("x-country") country: String): Response<AddressesDt>
    @POST("v1/user/{userId}/address")
    suspend fun addAddress(@Path("userId") id: Long, @Body address: Address, @Header("x-country") country: String): Response<Address>
    @PUT("v1/user/{userId}/address/{addressId}")
    suspend fun updateAddress(@Path("userId") id: Long, @Path("addressId") addressId: Long,  @Body address: Address, @Header("x-country") country: String): Response<Address>
    @DELETE("v1/user/{userId}/address/{addressId}")
    suspend fun removeAddress(@Path("userId") id: Long, @Path("addressId") addressId: Long, @Header("x-country") country: String): Response<Unit>
}

data class UpdateAddressRequest(val useId: Long, val addressId: Long, val address: Address, val countryCode: String)

@Parcelize
data class PhoneLoginRequest(
    @SerializedName("dialing_code") val identityType: String, @SerializedName(
        "phone_number"
    ) val identityValue: String
): Parcelable


data class PhoneVerifyRequest(
    @SerializedName("phone_number") val phoneNumber: String, @SerializedName(
        "dialing_code"
    ) val dailingCode: String, val otp: Long, @SerializedName("verification_id") val verificationId: Long, @SerializedName(
        "token_type"
    ) val tokenType: LOGINTYPE,
    @SerializedName("x-access-token")
    val accessToken: String
)

enum class LOGINTYPE {
    LOGIN,
    SIGNUP
}

enum class LOGINMODE {
    PHONE,
    EMAIL
}

data class EditUserProfileRequest(
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("id")
    val id: Long,
    @SerializedName("identities")
    val identities: List<Identity>
)

data class PhoneVerifyDt(
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
    val id: Long?,
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
    @SerializedName("dialing_code")
    val isdCode: String?,
    @SerializedName("profile_photo_url")
    val profilePhotoUrl: String?,
    @SerializedName("role")
    val role: ROLE,
    @SerializedName("token_details")
    val tokenDetails: TokenDetails?
) : ResponseDt<UserProfile>, Mappable<UserProfile> {
    override fun retrieveData(): UserProfile {
        return mapToData()
    }

    override fun mapToData(): UserProfile {
        return UserProfile("$firstName $lastName", firstName, lastName, id!!, phoneNumber!!, city!!, email, tokenDetails?.accessToken, isdCode, role, idProofImageUrl, idProofNumber, idProofType)
    }

    override fun isValid(): Boolean {
        return true
    }
}

data class TokenDetails(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?
)

@Parcelize
data class TokenDetailDt(@SerializedName("access_token") val accessToken: String): Parcelable


data class AddressesDt(
    @SerializedName("addresses")
    val addresses: List<Address>?
): ResponseDt<List<Address>?>, Mappable<List<Address>> {
    override fun retrieveData(): List<Address>? {
        return mapToData()
    }

    override fun mapToData(): List<Address> {
        return addresses ?: emptyList()
    }

    override fun isValid(): Boolean {
        return true
    }
}

@Parcelize
data class PhoneLoginDt(@SerializedName("token_type") val tokenType: LOGINTYPE, @SerializedName("verification_id") val verificationId: Long, @SerializedName("token_details") val tokenDetails: TokenDetailDt): ResponseDt<PhoneLoginDt>, Parcelable {
    override fun retrieveData(): PhoneLoginDt {
        return this
    }
}