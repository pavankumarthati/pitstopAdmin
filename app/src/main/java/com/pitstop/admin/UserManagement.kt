package com.pitstop.admin

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

const val USER_PROFILE_PREF_KEY = "user_profile_pref_key"

@Singleton
class UserManagement @Inject constructor(@Named("user_pref") private val userPrefs: SharedPreferences) {

    var userProfile: UserProfile? = Gson().fromJson<UserProfile>(userPrefs.getString(USER_PROFILE_PREF_KEY, null))
        set(userProfile) {
            field = if (userProfile == null) {
                userPrefs.edit().clear().apply()
                null
            } else {
                val userProfileStr = Gson().toJson(userProfile)
                userPrefs.edit().putString(USER_PROFILE_PREF_KEY, userProfileStr).apply()
                userProfile
            }
        }

    fun updateUserProfile(
        userPhone: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        identities: List<Identity>? = null
    ): Boolean {
        val userProfile = this.userProfile?.let {
            it.copy(
                userName = it.userName,
                userId = it.userId,
                userPhone = it.userPhone,
                email = it.email,
                city = it.city,
                accessToken = it.accessToken,
                isoCode = it.isoCode
            )
        }
        return userProfile != null
    }

    fun setUser(userProfile: UserProfile?) {
        this.userProfile = userProfile
    }

    fun getUserId(): Long? {
        return userProfile?.userId
    }

    fun getUserName(): String? {
        return userProfile?.let {
            it.userName
        }
    }

    fun getRole(): ROLE? {
        return userProfile?.let {
            it.role
        }
    }

    fun logout() {
        this.userProfile = null
    }

}

data class UserProfile(
    val userName: String,
    val firstName: String?,
    val lastName: String?,
    val userId: Long,
    val userPhone: String,
    val city: String,
    val email: String?,
    val accessToken: String?,
    val isoCode: String?,
    val role: ROLE,
    val idProofImageUrl: String?,
    val idProofNumber: String?,
    val idProofType: String?
)

enum class ROLE(val value: String) {
    EXECUTIVE("Executive"),
    ADMIN("Admin"),
    DRIVER("Driver")
}

data class Identity(
    @SerializedName("deleted")
    val deleted: Boolean?,
    @SerializedName("id")
    val id: Long?,
    @SerializedName("identity_type")
    val identityType: String,
    @SerializedName("identity_value")
    val identityValue: String,
    @SerializedName("user_id")
    val userId: Long?,
    @SerializedName("verified")
    val verified: Boolean?
)



data class Address(
    @SerializedName("address_line")
    val addressLine: String?,
    @SerializedName("address_metadata")
    var addressMetadata: HashMap<String, Any>? = null,
    @SerializedName("address_name")
    val addressName: String?,
    @SerializedName("customer_name")
    val customerName: String?,
    @SerializedName("address_type")
    val addressType: String?,
    @SerializedName("area")
    val area: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("country")
    val country: String?,
    var countryCode: String?,
    @SerializedName("pin_code")
    val pinCode: String?,
    @SerializedName("deleted")
    val deleted: Boolean? = false,
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("landmark")
    val landmark: String? = null,
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null,
    @Expose(serialize = false,deserialize = false)
    val placeId: String? = null,
    @SerializedName("user_id")
    val userId: Int? = null,
    @Expose(serialize = false, deserialize = false)
    var selected: Boolean? = false,
    @Expose(serialize = false, deserialize = false)
    var default: Boolean? = false
): ResponseDt<Address> {
    override fun retrieveData(): Address {
        return this
    }
}

interface ResponseDt<out T> {
    fun retrieveData(): T
}