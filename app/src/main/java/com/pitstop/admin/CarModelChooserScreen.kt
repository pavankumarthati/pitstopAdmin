package com.pitstop.admin

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carserviceapp.ext.getWidth
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_car_model_chooser_screen.view.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class CarModelChooserScreenFragment : DialogFragment(), CoroutineScope {

    val TAG = CarModelChooserScreenFragment::class.java.simpleName
    private lateinit var viewModel : CarModelSelectionViewModel
    private lateinit var carModelAdapter: CarModelAdapter
    private lateinit var activity: MainActivity
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + supervisorJob
    private val onClickCallback: (CarModel) -> Unit = {
        launch {
            withContext(Dispatchers.IO) {
                //activity.mainViewModel.onTempCarModelSelect(it.make, it.model)
            }
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Widget_CarModelChooserDialogStyle)
        viewModel = ViewModelProviders.of(this)
            .get(CarModelSelectionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_car_model_chooser_screen, container, false)
        view.carModelRv.layoutManager = LinearLayoutManager(container?.context, RecyclerView.VERTICAL, false)
        carModelAdapter = CarModelAdapter(onClickCallback)
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

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window!!
        val windowParams: WindowManager.LayoutParams = window.attributes
        val height = resources.getDimensionPixelSize(R.dimen.car_model_selection_rv_height)
        val width = getWidth(activity) - .20 * getWidth(activity)
        windowParams.width = width.roundToInt()
        windowParams.height = height
        windowParams.dimAmount = 0.90f
        windowParams.flags = windowParams.flags.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = windowParams
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode ==  android.view.KeyEvent.KEYCODE_BACK) {
                activity.finish()
                true
            }
            else
                false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        supervisorJob.cancelChildren()
    }
}

class CarModelAdapter(onClickCallback: (CarModel) -> Unit, val sourceKey: String? = null) : RecyclerView.Adapter<CarModelAdapter.CarModelViewHolder>() {

    private val SECTION_HEADER = 0
    private val SECTION_ITEM = 1

    private var items: List<CarModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarModelViewHolder {
        val view = if (viewType == SECTION_HEADER) {
            LayoutInflater.from(parent.context).inflate(R.layout.car_make_list_item_header, parent, false) as TextView
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.car_make_list_item, parent, false) as TextView
        }
        return CarModelViewHolder(view)
    }

    override fun getItemCount() = items?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return if (items?.get(position)?.isHeader == true) {
             SECTION_HEADER
        } else {
             SECTION_ITEM
        }
    }

    private val onClicked: View.OnClickListener = View.OnClickListener {
        onClickCallback(items?.get(it.tag as Int)!!)
    }

    override fun onBindViewHolder(holder: CarModelViewHolder, position: Int) {
        if (items?.get(position)?.isHeader == true) {
            holder.view.text = items?.get(position)?.make
        } else {
            holder.view.text = items?.get(position)?.model
        }
        holder.view.tag = position
        val viewType = getItemViewType(position)

        if (sourceKey == null) {
            if (viewType == SECTION_ITEM) {
                holder.view.setOnClickListener(onClicked)
            }
        } else {
            holder.view.setOnClickListener(onClicked)
        }
    }

    fun setItems(items: List<CarModel>?) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class CarModelViewHolder(val view : TextView) : RecyclerView.ViewHolder(view)

}

data class CarModel(val model: String?, val make: String, val isHeader: Boolean? = null)

class CarModelSelectionViewModel(application: Application) : AndroidViewModel(application) {
    private val publishSubject = BehaviorSubject.create<Array<CarMakerEntity>>()
    val carModelLiveData = MutableLiveData<List<CarModel>>()
    private val EDIT_TEXT_DEBOUNCE = 50L


    suspend fun onViewCreated(_editTextStream: Observable<CharSequence>) {
        prepareEditTextStream(_editTextStream)
        getModelList()

    }

    @SuppressLint("CheckResult")
    private fun prepareEditTextStream(_editTextStream: Observable<CharSequence>) {
        val editTextStream = _editTextStream
            .map { it.toString() }
            .distinctUntilChanged()
            .debounce(EDIT_TEXT_DEBOUNCE, TimeUnit.MILLISECONDS)
        Observable
            .combineLatest(
                editTextStream,
                publishSubject,
                BiFunction {
                        t1:String, t2: Array<CarMakerEntity> -> Pair(t1, t2) })
            .map { t: Pair<String, Array<CarMakerEntity>> ->
                val list = mutableListOf<CarModel>()
                t.second.forEach {carMake ->
                    if (carMake.make.contains(t.first, true)) {
                        list.add(CarModel(null, carMake.make, true))
                        carMake.models.forEach {
                            list.add(CarModel(it, carMake.make, false))
                        }
                    } else {
                        val subList = carMake.models.filter { it.contains(t.first, true) }
                        if (!subList.isNullOrEmpty()) {
                            list.add(CarModel(null, carMake.make, true))
                            subList.forEach {
                                list.add(CarModel(it, carMake.make, false))
                            }
                        }
                    }
                }
                list }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                carModelLiveData.postValue(it)
            }, {
                Toast.makeText(getApplication(), "Oops! Something Went wrong!!", Toast.LENGTH_LONG).show()
            })
    }

    private suspend fun getModelList() {
        val res = ConfigManager.configModel?.carMakerEntity?.toTypedArray()
        publishSubject.onNext(res!!)
    }


    override fun onCleared() {
        super.onCleared()
        publishSubject.onComplete()
    }
}

@Parcelize
data class CarMakerEntity(val id: String, val make: String, val models: List<String>): Parcelable