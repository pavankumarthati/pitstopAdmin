package com.pitstop.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.carserviceapp.ext.formatPhoneVerifySubLabel
import com.carserviceapp.ext.getViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_phone_verify.view.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

const val OTP_REQUEST_KEY = "otp_req_key"
const val OTP_RESPONSE_KEY = "otp_response_key"
const val COUNTRY_ISO3_CODE = "COUNTRY_ISO3_CODE"

class PhoneNoVerificationFragment: Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    lateinit var mActivity: LoginActivity
    lateinit var mPhoneNoVerificationViewModel: PhoneNoVerificationViewModel
    lateinit var mPhoneLoginViewModel: PhoneNoLoginViewModel
    @Inject
    lateinit var mCcService: CcService
    lateinit var mPhoneLoginResponseDt: PhoneLoginDt
    lateinit var mPhoneLoginRequest: PhoneLoginRequest
    private var compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var mUserManagement: UserManagement
    lateinit var countryISO3Code: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (mActivity.application as AdminApp).appComponent.inject(this)
        mPhoneNoVerificationViewModel = getViewModel {
            PhoneNoVerificationViewModel(mCcService, this@PhoneNoVerificationFragment)
        }

        mPhoneLoginViewModel = getViewModel {
            PhoneNoLoginViewModel(mCcService, this@PhoneNoVerificationFragment)
        }
        mPhoneLoginResponseDt = arguments?.getParcelable(OTP_RESPONSE_KEY) ?: throw IllegalArgumentException("phone login request is null")
        mPhoneLoginRequest = arguments?.getParcelable(OTP_REQUEST_KEY)!!
        countryISO3Code = arguments?.getString(COUNTRY_ISO3_CODE)!!
        mPhoneNoVerificationViewModel.startTimer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phone_verify, container, false)
        view.verifyPhoneSubLabelTv.text = this.context!!.formatPhoneVerifySubLabel(getString(
            R.string.verify_phone_sub_label,
            "${mPhoneLoginRequest.identityType}-${mPhoneLoginRequest.identityValue}"
        ), R.font.proxima_nova_bold, 1.0f)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mPhoneNoVerificationViewModel.startTimer().observe(this@PhoneNoVerificationFragment.viewLifecycleOwner, Observer {
            view?.resendOtpLabelTv?.text = resources.getString(R.string.resend_otp_timer, it)
            if (it == 0) {
                view?.resendBtn?.isEnabled = true
            }
        })
        view?.otpView?.getTextStream()?.let {
            if (compositeDisposable.isDisposed) {
                compositeDisposable = CompositeDisposable()
            }
            compositeDisposable.add(
                it.subscribe {_ ->
                    if (view?.otpView?.getText()?.length == view?.otpView?.getFamilySize()) {
                        updateVerifyBtnState(true)
                    } else {
                        updateVerifyBtnState(false)
                    }
                }
            )
        }


        view?.resendOtpLabelTv?.text = resources.getString(R.string.resend_otp_timer, OTP_TIMER)
        view?.changeNumberBtn?.setOnClickListener {
            findNavController().popBackStack()
        }
        view?.resendBtn?.setOnClickListener {
            mPhoneLoginViewModel.doPhoneLogin(mPhoneLoginRequest, countryISO3Code).observe(this@PhoneNoVerificationFragment, Observer {phoneLoginResource ->

                when (phoneLoginResource.status) {
                    Status.LOADING -> {
                        view?.loader?.visibility = View.VISIBLE
                        Glide.with(this@PhoneNoVerificationFragment.mActivity)
                            .load(R.drawable.loader)
                            .into(view?.loader as AppCompatImageView)
                    }
                    Status.SUCCESS -> {
                        view?.resendOtpLabelTv?.visibility = View.VISIBLE
                        view?.resendBtn?.isEnabled = false
                        mPhoneNoVerificationViewModel.resetTimer()
                        view?.loader?.visibility = View.GONE
                        mPhoneLoginResponseDt = phoneLoginResource.data!!
                    }
                    Status.ERROR -> {
                        view?.loader?.visibility = View.GONE
                        //TODO:  make mPhoneLoginResponseDt null
                        Log.e("PVF","$phoneLoginResource.message - ${phoneLoginResource.status}")
                    }
                }
            })
        }
        view?.verifyBtn?.setOnClickListener {
            val otp = view?.otpView?.getText()?.toLong()!!
            val phoneVerifyRequest = PhoneVerifyRequest(
                mPhoneLoginRequest.identityValue,
                mPhoneLoginRequest.identityType,
                otp,
                mPhoneLoginResponseDt.verificationId,
                mPhoneLoginResponseDt.tokenType,
                mPhoneLoginResponseDt.tokenDetails.accessToken
            )

            mPhoneNoVerificationViewModel.doPhoneVerification(phoneVerifyRequest, countryISO3Code).observe(this, Observer {userProfileResource ->
                when (userProfileResource.status) {
                    Status.LOADING -> {
                        view?.loader?.visibility = View.VISIBLE
                        Glide.with(this@PhoneNoVerificationFragment.mActivity)
                            .load(R.drawable.loader)
                            .into(view?.loader as AppCompatImageView)
                    }
                    Status.SUCCESS -> {
                        view?.loader?.visibility = View.GONE
                        mUserManagement.setUser(userProfileResource.data!!)
                        if (phoneVerifyRequest.tokenType == LOGINTYPE.LOGIN) {
                            if (mActivity.callingActivity != null) {
                                mActivity.setResult(Activity.RESULT_OK)
                                mActivity.finish()
                            } else {
                                startActivity(Intent(mActivity, MainActivity::class.java))
                                mActivity.finish()
                            }
                        } else {
                            startActivity(Intent(mActivity, MainActivity::class.java))
                            mActivity.finish()
                        }
                    }
                    Status.ERROR -> {
                        view?.loader?.visibility = View.GONE
                        Log.e("PVF", userProfileResource.message)
                    }
                }
            })
        }
    }

    fun updateVerifyBtnState(enable: Boolean) {
        view?.verifyBtn?.isEnabled = enable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineContext.cancelChildren()
        Log.i("PHNOV", "cancelled children")
        compositeDisposable.dispose()
    }
}

const val OTP_TIMER = 60 // secs

class PhoneNoVerificationViewModel(private val ccService: CcService, private val coroutineScope: CoroutineScope): ViewModel() {

    private val mPhoneVerifyTrigger =  MutableLiveData<Pair<PhoneVerifyRequest, String>>()
    private val mPhoneVerificationResponse = Transformations.switchMap(mPhoneVerifyTrigger) {
        fetchPhoneVerificationResponse(it)
    }

    private var timerLiveData = MutableLiveData<Int>()
    private var timerJob: Job? = null

    fun startTimer(): LiveData<Int> {
        return timerLiveData.apply {
            Log.i("PHNOV", "recreated job")
            timerJob = coroutineScope.launch {
                repeat(OTP_TIMER) {
                    delay(1000)
                    Log.i("PHNOV", "$it")
                    postValue(OTP_TIMER - (it + 1))
                }
            }
        }
    }

    fun resetTimer(): LiveData<Int> {
        timerJob?.cancel()
        return startTimer()
    }

    fun doPhoneVerification(phoneVerifyRequest: PhoneVerifyRequest, countryISO3Code: String): LiveData<Resource<UserProfile>> {
        mPhoneVerifyTrigger.value = Pair(phoneVerifyRequest, countryISO3Code)
        return mPhoneVerificationResponse
    }

    private fun fetchPhoneVerificationResponse(phoneVerifyRequest: Pair<PhoneVerifyRequest, String>): LiveData<Resource<UserProfile>> {
        return coroutineScope.makeNetworkCall<PhoneVerifyDt, UserProfile> {
            ccService.getPhoneVerificationResponse(phoneVerifyRequest.first, phoneVerifyRequest.second, phoneVerifyRequest.first.accessToken)
        }
    }

}