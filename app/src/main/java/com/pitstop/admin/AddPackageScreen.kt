package com.pitstop.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.fragment_add_package.*
import kotlinx.android.synthetic.main.layout_add_package.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddPackageScreen: Fragment(), CoroutineScope {

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
        val view = inflater.inflate(R.layout.fragment_add_package, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        carMakeSpinner.setOnClickListener {
            val fragment = CarMakeFragment()
            fragment.arguments = Bundle().apply {
                putString(SOURCE_KEY, "Package")
            }
            mActivity.supportFragmentManager.beginTransaction().add(R.id.content_main , fragment).addToBackStack(null).commit()
        }

        mActivity.mainViewModel.carMakeModelLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                carMakeSpinner.text = it.make + " " + (it.model ?: "")
                carMakeSpinner.tag = it
                saveBtn.isEnabled = !((carMakeSpinner.text).isNullOrEmpty()
                        || (inclusionsSpinner.selectedItem as? String).isNullOrEmpty()
                        || (nameTextInputEt.text.isNullOrEmpty())
                        || (originalPriceEt.text.isNullOrEmpty())
                        || (saleEt.text.isNullOrEmpty()))
            }
        })

        val spinnerAdapter = ArrayAdapter<String>(this@AddPackageScreen.context!!, android.R.layout.simple_spinner_item, android.R.id.text1)
        val list = ArrayList<String>(ConfigManager.configModel!!.inclusionList!!)
        list.add(0, "Inclusions")
        spinnerAdapter.addAll(list)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inclusionsSpinner.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()
        inclusionsSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) return
                saveBtn.isEnabled = !((carMakeSpinner.text).isNullOrEmpty()
                        || (inclusionsSpinner.selectedItem as? String).isNullOrEmpty()
                        || (nameTextInputEt.text.isNullOrEmpty())
                        || (originalPriceEt.text.isNullOrEmpty())
                        || (saleEt.text.isNullOrEmpty()))

                val tv = AppCompatTextView(saveBtn.context)
                tv.setPadding(saveBtn.context.resources.getDimensionPixelSize(R.dimen.eight_dp), saveBtn.context.resources.getDimensionPixelSize(R.dimen.ten_dp), saveBtn.context.resources.getDimensionPixelSize(R.dimen.eight_dp), saveBtn.context.resources.getDimensionPixelSize(R.dimen.ten_dp))
                tv.text = inclusionsSpinner.selectedItem as String

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.topMargin = saveBtn.context.resources.getDimensionPixelSize(R.dimen.ten_dp)
                params.bottomMargin = saveBtn.context.resources.getDimensionPixelSize(R.dimen.ten_dp)

                customInclusionsLayout.addView(tv, params)
            }

        }

        nameTextInputEt.textChanges().subscribe({
            saveBtn.isEnabled = !((carMakeSpinner.text).isNullOrEmpty()
                    || (inclusionsSpinner.selectedItem as String).isNullOrEmpty()
                    || (inclusionsSpinner.selectedItem as String).isNullOrEmpty()
                    || (nameTextInputEt.text.isNullOrEmpty())
                    || (originalPriceEt.text.isNullOrEmpty())
                    || (saleEt.text.isNullOrEmpty()))
        }, {

        })

        originalPriceEt.textChanges().subscribe({
            saveBtn.isEnabled = !((carMakeSpinner.text).isNullOrEmpty()
                    || (inclusionsSpinner.selectedItem as String).isNullOrEmpty()
                    || (nameTextInputEt.text.isNullOrEmpty())
                    || (originalPriceEt.text.isNullOrEmpty())
                    || (saleEt.text.isNullOrEmpty()))
        }, {

        })

        saleEt.textChanges().subscribe({
            saveBtn.isEnabled = !((carMakeSpinner.text).isNullOrEmpty()
                    || (inclusionsSpinner.selectedItem as String).isNullOrEmpty()
                    || (nameTextInputEt.text.isNullOrEmpty())
                    || (originalPriceEt.text.isNullOrEmpty())
                    || (saleEt.text.isNullOrEmpty()))
        }, {

        })

        saveBtn.setOnClickListener {
            launch {

                val inclusions = ArrayList<String>()
                customInclusionsLayout.children.forEach {
                    inclusions.add((it as AppCompatTextView).text.toString())
                }

                val carModel = carMakeSpinner.tag as CarModel
                val addPackageModel = AddPackageModel(carModel.make, carModel.model, "bangalore", saleEt.text.toString().toInt(), inclusions, null, nameTextInputEt.text.toString(), originalPriceEt.text.toString().toInt(), 6)
                val res = retrofit.create(AddPackageService::class.java).addPackage(addPackageModel)

                res?.let {
                    if (res.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddPackageScreen.context, "Package added successfully", Toast.LENGTH_LONG).show()
                            mActivity.supportFragmentManager.popBackStack()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddPackageScreen.context, "Oops! Unable to save Package", Toast.LENGTH_LONG).show()

                        }
                    }
                }
                /*val serviceCenterModel = ServiceCenterModel(0, false, addressTextEt.text.toString(), areaEt.text.toString(), "", 0.0, 0.0, makeSpinner.selectedItem as String, nameTextInputEt.text.toString(), phoneNumberEt.text.toString())
                val success = retrofit.create(ServiceCentersService::class.java).getPackages(serviceCenterModel).isSuccessful
                if (success) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddPackageScreen.context, "Package added successfully", Toast.LENGTH_LONG).show()
                        mActivity.supportFragmentManager.popBackStack()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddPackageScreen.context, "Oops! Unable to save Package", Toast.LENGTH_LONG).show()

                    }
                }*/

            }
        }

        addInclusionBtn.setOnClickListener {
            val customInclusionEt = AppCompatEditText(this@AddPackageScreen.context)
            val params = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            customInclusionsLayout.addView(customInclusionEt, params)
        }



        super.onActivityCreated(savedInstanceState)
    }
}