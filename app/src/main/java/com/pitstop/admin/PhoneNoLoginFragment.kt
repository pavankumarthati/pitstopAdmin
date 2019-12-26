package com.pitstop.admin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.carserviceapp.ext.getViewModel
import com.jakewharton.rxbinding2.widget.textChanges
import com.pitstop.admin.AdminApp
import com.pitstop.admin.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.android.synthetic.main.fragment_phone_login.view.*

class PhoneNoLoginFragment: Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    private lateinit var mPhoneNoLoginViewModel: PhoneNoLoginViewModel
    @Inject
    lateinit var mCcService: CcService
    lateinit var mActivity: LoginActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (mActivity.application as AdminApp).appComponent.inject(this)
        mPhoneNoLoginViewModel = getViewModel {
            PhoneNoLoginViewModel(mCcService, this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_phone_login, container, false)
    }


    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mPhoneNoLoginViewModel.selectedCountryCode?.let {
            view?.findViewById<com.hbb20.CountryCodePicker>(R.id.countryCodePicker)?.setCountryForPhoneCode(it)
        }

        view?.phoneNumberTv?.filters = arrayOf(InputFilter.LengthFilter(10))

        (view?.phoneNumberTv as AppCompatEditText)?.textChanges().subscribe {
            view?.nextBtn?.isEnabled = !view?.phoneNumberTv?.text?.isEmpty()!!
        }

        view?.nextBtn?.setOnClickListener {_ ->
            val countryPlusCode = view?.findViewById<com.hbb20.CountryCodePicker>(R.id.countryCodePicker)?.selectedCountryCodeWithPlus
            mPhoneNoLoginViewModel.selectedCountryCode = view?.findViewById<com.hbb20.CountryCodePicker>(R.id.countryCodePicker)?.selectedCountryCode?.toInt()
            val countryIso3Code = "IN"
            val phone = view?.phoneNumberTv?.text
            val phoneLoginRequest = PhoneLoginRequest(countryPlusCode!!, phone?.toString()!!)
            mPhoneNoLoginViewModel.doPhoneLogin(phoneLoginRequest, countryIso3Code).observe(this@PhoneNoLoginFragment, Observer {phoneLoginResource ->

                when (phoneLoginResource.status) {
                    Status.LOADING -> {
                        view?.loader?.visibility = View.VISIBLE
                        Glide.with(this@PhoneNoLoginFragment.mActivity)
                            .load(R.drawable.loader)
                            .into(view?.loader as AppCompatImageView)
                    }
                    Status.SUCCESS -> {
                        view?.loader?.visibility = View.GONE
                        val bundle = Bundle().apply {
                            this.putParcelable(OTP_REQUEST_KEY, phoneLoginRequest)
                            this.putParcelable(OTP_RESPONSE_KEY, phoneLoginResource.data)
                            this.putString(COUNTRY_ISO3_CODE, countryIso3Code)
                        }
                        findNavController().navigate(R.id.PhoneNoVerificationFragment, bundle)
                    }
                    Status.ERROR -> {
                        view?.loader?.visibility = View.GONE
                        Log.e("PhoneNoLoginFragment","$phoneLoginResource.message - ${phoneLoginResource.status}")
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineContext.cancelChildren()
    }

}

class PhoneNoLoginViewModel(val phoneLoginService: CcService, val coroutineScope: CoroutineScope): ViewModel() {

    var selectedCountryCode: Int? = null
    private val mPhoneLoginTrigger =  MutableLiveData<Pair<PhoneLoginRequest, String>>()
    private val mPhoneLoginResponse = Transformations.switchMap(mPhoneLoginTrigger) {
        fetchPhoneLoginResponse(it)
    }

    fun doPhoneLogin(phoneLoginRequest: PhoneLoginRequest, countryISO3Code: String): LiveData<Resource<PhoneLoginDt>> {
        mPhoneLoginTrigger.value = Pair(phoneLoginRequest, countryISO3Code)
        return mPhoneLoginResponse
    }

    private fun fetchPhoneLoginResponse(phoneLoginRequest: Pair<PhoneLoginRequest, String>): LiveData<Resource<PhoneLoginDt>> {
        return coroutineScope.makeNetworkCall<PhoneLoginDt, PhoneLoginDt> {
            phoneLoginService.getPhoneLoginResponse(phoneLoginRequest.first, phoneLoginRequest.second)
        }
    }


}