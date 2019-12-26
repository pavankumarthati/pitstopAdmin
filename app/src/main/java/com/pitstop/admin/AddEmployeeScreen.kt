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
import kotlinx.android.synthetic.main.fragment_add_employee.*
import kotlinx.android.synthetic.main.fragment_add_employee.addressTextEt
import kotlinx.android.synthetic.main.fragment_add_employee.citySpinner
import kotlinx.android.synthetic.main.fragment_add_package.saveBtn
import kotlinx.android.synthetic.main.fragment_add_service_center.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddEmployeeScreen: Fragment(), CoroutineScope {

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
        val view = inflater.inflate(R.layout.fragment_add_employee, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {


        firstNameTextInputEt.textChanges().subscribe({
            saveBtn.isEnabled = !((firstNameTextInputEt.text).isNullOrEmpty()
                    || (lastNameTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItemPosition == 0)
                    || (phNoTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (emailTextInputEt.text.isNullOrEmpty())
                    || (passwordTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (citySpinner.selectedItemPosition == 0)
                    || ((citySpinner.selectedItem as? String).isNullOrEmpty()))
        }, {

        })

        lastNameTextInputEt.textChanges().subscribe({
            saveBtn.isEnabled = !((firstNameTextInputEt.text).isNullOrEmpty()
                    || (lastNameTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItemPosition == 0)
                    || (phNoTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (emailTextInputEt.text.isNullOrEmpty())
                    || (passwordTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (citySpinner.selectedItemPosition == 0)
                    || ((citySpinner.selectedItem as? String).isNullOrEmpty()))
        }, {

        })

        phNoTextInputEt.textChanges().subscribe({
            saveBtn.isEnabled = !((firstNameTextInputEt.text).isNullOrEmpty()
                    || (lastNameTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItemPosition == 0)
                    || (phNoTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (emailTextInputEt.text.isNullOrEmpty())
                    || (passwordTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (citySpinner.selectedItemPosition == 0)
                    || ((citySpinner.selectedItem as? String).isNullOrEmpty()))
        }, {

        })


        emailTextInputEt.textChanges().subscribe({
            saveBtn.isEnabled = !((firstNameTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItemPosition == 0)
                    || (lastNameTextInputEt.text).isNullOrEmpty()
                    || (phNoTextInputEt.text).isNullOrEmpty()
                    || (emailTextInputEt.text.isNullOrEmpty())
                    || (profileTypeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (passwordTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (citySpinner.selectedItemPosition == 0)
                    || ((citySpinner.selectedItem as? String).isNullOrEmpty()))
        }, {

        })

        passwordTextInputEt.textChanges().subscribe({
            saveBtn.isEnabled = !((firstNameTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItemPosition == 0)
                    || (lastNameTextInputEt.text).isNullOrEmpty()
                    || (phNoTextInputEt.text).isNullOrEmpty()
                    || (emailTextInputEt.text.isNullOrEmpty())
                    || (profileTypeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (passwordTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (citySpinner.selectedItemPosition == 0)
                    || ((citySpinner.selectedItem as? String).isNullOrEmpty()))
        }, {

        })

        addressTextEt.textChanges().subscribe({
            saveBtn.isEnabled = !((firstNameTextInputEt.text).isNullOrEmpty()
                    || (lastNameTextInputEt.text).isNullOrEmpty()
                    || (phNoTextInputEt.text).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItem as? String).isNullOrEmpty()
                    || (profileTypeSpinner.selectedItemPosition == 0)
                    || (emailTextInputEt.text.isNullOrEmpty())
                    || (passwordTextInputEt.text.isNullOrEmpty())
                    || (addressTextEt.text.isNullOrEmpty())
                    || (citySpinner.selectedItemPosition == 0)
                    || ((citySpinner.selectedItem as? String).isNullOrEmpty()))
        }, {

        })

        val cities = ArrayList<String>()
        cities.add("City")
        cities.addAll(ConfigManager.configModel!!.availableCities!!)
        citySpinner.adapter = OrderFilterAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, cities)
        (citySpinner.adapter as OrderFilterAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = citySpinner.adapter

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

                saveBtn.isEnabled = !((firstNameTextInputEt.text).isNullOrEmpty()
                        || (lastNameTextInputEt.text).isNullOrEmpty()
                        || (phNoTextInputEt.text).isNullOrEmpty()
                        || (profileTypeSpinner.selectedItem as? String).isNullOrEmpty()
                        || (profileTypeSpinner.selectedItemPosition == 0)
                        || (emailTextInputEt.text.isNullOrEmpty())
                        || (passwordTextInputEt.text.isNullOrEmpty())
                        || (addressTextEt.text.isNullOrEmpty())
                        || (citySpinner.selectedItemPosition == 0)
                        || ((citySpinner.selectedItem as? String).isNullOrEmpty()))
            }

        }

        val roles = ArrayList<String>()
        roles.add("Executive")
        roles.add("Admin")
        roles.add("Driver")
        roles.add(0, "Role")
        val roleAdapter = OrderFilterAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, roles)
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        profileTypeSpinner.adapter = roleAdapter

        profileTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
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

                saveBtn.isEnabled = !((firstNameTextInputEt.text).isNullOrEmpty()
                        || (lastNameTextInputEt.text).isNullOrEmpty()
                        || (profileTypeSpinner.selectedItemPosition == 0)
                        || (profileTypeSpinner.selectedItem as? String).isNullOrEmpty()
                        || (phNoTextInputEt.text).isNullOrEmpty()
                        || (emailTextInputEt.text.isNullOrEmpty())
                        || (passwordTextInputEt.text.isNullOrEmpty())
                        || (addressTextEt.text.isNullOrEmpty())
                        || ((citySpinner.selectedItem as String).isNullOrEmpty()))
            }

        }



        saveBtn.setOnClickListener {
            launch {

                val addEmployeeModel = AddEmployeeModel(null, true, addressTextEt.text.toString(), citySpinner.selectedItem as String, emailTextInputEt.text.toString(), firstNameTextInputEt.text.toString(), null, null, null, lastNameTextInputEt.text.toString(), passwordTextInputEt.text.toString(), phNoTextInputEt.text.toString(), null, profileTypeSpinner.selectedItem as String)
                val res = retrofit.create(EmployeeService::class.java).createEmployee(addEmployeeModel, addEmployeeModel.profileType!!)

                res?.let {
                    if (res.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmployeeScreen.context, "Employee added successfully", Toast.LENGTH_LONG).show()
                            mActivity.supportFragmentManager.popBackStack()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddEmployeeScreen.context, "Oops! Unable to save Package", Toast.LENGTH_LONG).show()

                        }
                    }
                }

            }
        }



        super.onActivityCreated(savedInstanceState)
    }
}