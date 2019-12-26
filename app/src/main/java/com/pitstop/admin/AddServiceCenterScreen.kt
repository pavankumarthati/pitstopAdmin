package com.pitstop.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.fragment_add_service_center.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddServiceCenterScreen: Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    @Inject
    lateinit var retrofit: Retrofit
    private lateinit var mActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (mActivity.application as AdminApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_service_center, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val carMakes = ArrayList<String>()
        carMakes.addAll(ConfigManager.configModel!!.carMakerEntity!!.map { it.make })
        carMakes.add(0, "Car Make")
        val carMakeAdapter = OrderFilterAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, carMakes)
        carMakeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        carMakeSpinner.adapter = carMakeAdapter
        carMakeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0 && parent != null && parent!!.childCount > 0) {
                    (parent?.getChildAt(0) as TextView).setTextColor(context!!.resources.getColor(R.color.gray_300))
                    return
                }

                saveBtn.isEnabled = !((carMakeSpinner.selectedItem as? String).isNullOrEmpty()
                        || (carMakeSpinner.selectedItemPosition == 0)
                        || (citySpinner.selectedItem as String).isNullOrEmpty()
                        || (nameTextInputEt.text.isNullOrEmpty())
                        || (addressTextEt.text.isNullOrEmpty())
                        || (areaEt.text.isNullOrEmpty())
                        || (phoneNumberEt.text.isNullOrEmpty()))
            }
        }

        val cities = ArrayList<String>()
        cities.addAll(ConfigManager.configModel!!.availableCities!!)
        cities.add(0, "City")
        val cityAdapter = OrderFilterAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter

        citySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position == 0 && parent != null && parent!!.childCount > 0) {
                    (parent?.getChildAt(0) as TextView).setTextColor(context!!.resources.getColor(R.color.gray_300))
                    return
                }

                saveBtn.isEnabled = !((carMakeSpinner.selectedItem as? String).isNullOrEmpty()
                        || (carMakeSpinner.selectedItemPosition == 0)
                        || (citySpinner.selectedItem as String).isNullOrEmpty()
                        || (nameTextInputEt.text.isNullOrEmpty())
                        || (addressTextEt.text.isNullOrEmpty())
                        || (areaEt.text.isNullOrEmpty())
                        || (phoneNumberEt.text.isNullOrEmpty()))
            }

        }

        nameTextInputEt.textChanges().subscribe({
            saveBtn.isEnabled = !((carMakeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (carMakeSpinner.selectedItemPosition == 0)
                    || (citySpinner.selectedItem as String).isNullOrEmpty()
                    || (nameTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (areaEt.text.isNullOrEmpty())
                    || (phoneNumberEt.text.isNullOrEmpty()))
        }, {

        })

        addressTextEt.textChanges().subscribe({
            saveBtn.isEnabled = !((carMakeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (carMakeSpinner.selectedItemPosition == 0)
                    || (citySpinner.selectedItem as String).isNullOrEmpty()
                    || (nameTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (areaEt.text.isNullOrEmpty())
                    || (phoneNumberEt.text.isNullOrEmpty()))
        }, {

        })

        areaEt.textChanges().subscribe({
            saveBtn.isEnabled = !((carMakeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (carMakeSpinner.selectedItemPosition == 0)
                    || (citySpinner.selectedItem as String).isNullOrEmpty()
                    || (nameTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (areaEt.text.isNullOrEmpty())
                    || (phoneNumberEt.text.isNullOrEmpty()))
        }, {

        })

        phoneNumberEt.textChanges().subscribe({
            saveBtn.isEnabled = !((carMakeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (carMakeSpinner.selectedItemPosition == 0)
                    || (citySpinner.selectedItem as String).isNullOrEmpty()
                    || (nameTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (areaEt.text.isNullOrEmpty())
                    || (phoneNumberEt.text.isNullOrEmpty()))
        }, {

        })

        saveBtn.setOnClickListener {
            launch {
                val serviceCenterModel = ServiceCenterModel(0, false, addressTextEt.text.toString(), areaEt.text.toString(), citySpinner.selectedItem as String, 0.0, 0.0, carMakeSpinner.selectedItem as String, nameTextInputEt.text.toString(), phoneNumberEt.text.toString())
                val success = retrofit.create(ServiceCentersService::class.java).createServiceCenter(serviceCenterModel).isSuccessful
                if (success) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddServiceCenterScreen.context, "Service center added successfully", Toast.LENGTH_LONG).show()
                        mActivity.supportFragmentManager.popBackStack()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddServiceCenterScreen.context, "Oops! Unable to save service center", Toast.LENGTH_LONG).show()

                    }
                }

            }
        }



        super.onActivityCreated(savedInstanceState)
    }
}