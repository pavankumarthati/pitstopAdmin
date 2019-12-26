package com.pitstop.admin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.fragment_car_model_chooser_screen.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

const val SOURCE_KEY = "source_key"

class CarMakeFragment: Fragment(), CoroutineScope {

    val TAG = "CarMakeFragment"
    private lateinit var viewModel : CarModelSelectionViewModel
    private lateinit var carModelAdapter: CarModelAdapter
    private lateinit var activity: MainActivity
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + supervisorJob
    private val onClickCallback: (CarModel) -> Unit = {
        launch {
            withContext(Dispatchers.IO) {
                activity.mainViewModel.onCarModelSelect(it)
            }
            fragmentManager?.popBackStack()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)
            .get(CarModelSelectionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_car_model_chooser_screen, container, false)
        view.carModelRv.layoutManager = LinearLayoutManager(container?.context, RecyclerView.VERTICAL, false)
        carModelAdapter = CarModelAdapter(onClickCallback, arguments?.getString(SOURCE_KEY))
        view.carModelRv.adapter = carModelAdapter
        return view
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launch {
            withContext(Dispatchers.IO) {
                viewModel.onViewCreated(view.carModelSearchEt.textChanges())
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.carModelLiveData.observe(viewLifecycleOwner, Observer {
            carModelAdapter.setItems(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        supervisorJob.cancelChildren()
    }

}