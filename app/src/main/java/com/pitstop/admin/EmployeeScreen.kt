package com.pitstop.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.employee_layout.view.*
import kotlinx.android.synthetic.main.fragment_employees.*
import kotlinx.android.synthetic.main.fragment_service_centers.view.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow
import kotlin.math.roundToInt

class EmployeeListFragment: Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO


    private lateinit var employeesRv: RecyclerView
    lateinit var mActivity: MainActivity
    @Inject
    lateinit var retrofit: Retrofit

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
        val rootView = inflater.inflate(R.layout.fragment_employees, container, false)
        employeesRv = rootView.employeesRv
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        employeesRv.layoutManager = GridLayoutManager(this.context, resources.getInteger(R.integer.span_count), RecyclerView.VERTICAL, false)
        val gridItemDecoration = RecyclerViewChildGridMarginDecoration(resources.getDimensionPixelSize(R.dimen.item_space))
        employeesRv.addItemDecoration(gridItemDecoration)
        employeesRv.adapter = EmployeesAdapter()

        launch {
            withContext(Dispatchers.Main) {
                view?.loader?.visibility = View.VISIBLE
                Glide.with(this@EmployeeListFragment.mActivity)
                    .load(R.drawable.loader)
                    .into(view?.loader as AppCompatImageView)
            }
            val res = retrofit.create(EmployeeService::class.java).getEmployees()
            withContext(Dispatchers.Main) {
                view?.loader?.visibility = View.GONE
                if (res.isSuccessful) {
                    (employeesRv.adapter as EmployeesAdapter).addItems(res.body()!!.employees)
                } else {
                    Toast.makeText(
                        this@EmployeeListFragment.context,
                        "Oops! Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        addEmployeeBtn.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction().replace(R.id.content_main , AddEmployeeScreen()).addToBackStack(null).commit()
        }
    }

    companion object {
        val TAG = "EmployeeFragment"
    }

}


class EmployeesAdapter: RecyclerView.Adapter<EmployeesAdapter.EmployeeViewHolder>() {

    private var items: List<AddEmployeeModel>? = null

    private val serviceCengters = listOf<String>("VARUN MOTORS", "VIVEK MOTORS", "CARSIGO", "CARSONWHEELS")
    private val serviceCengterAddressList = listOf<String>("HSR Layout", "Koramangala", "Indira Nagar", "Sarjapur")
    private val serviceCengterPhoneList = listOf<String>("+91 9440499125", "+91 9440499143", "+91 9440499134", "+91 9440499187")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.employee_layout, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size?: 0
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.empId.text = (Math.random() * (10.0.pow(6.0))).roundToInt().toString()
        holder.employeeAddressTv.text = items?.get(position)?.address
        holder.employeeNameTv.text = items?.get(position)?.firstName + " " + items?.get(position)?.lastName
        holder.employeePhoneTv.text = items?.get(position)?.phoneNumber
        holder.employeeRoleTv.text = "EXECUTIVE"
        holder.serviceCenterNameTv.text = serviceCengters[position % 4]
        holder.serviceCenterAddressTv.text = serviceCengterAddressList[position % 4]
        holder.serviceCenterPhoneTv.text = serviceCengterPhoneList[position % 4]
    }

    fun addItems(items: List<AddEmployeeModel>?) {
        this.items = items
        notifyDataSetChanged()
    }


    class EmployeeViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val empId = view.employeeIdTv
        val employeeNameTv = view.employeeNameTv
        val employeeRoleTv = view.employeeRoleTv
        val employeeAddressTv = view.employeeAddressTv
        val employeePhoneTv = view.employeePhoneTv
        val serviceCenterNameTv = view.serviceCenterNameTv
        val serviceCenterAddressTv = view.serviceCenterAddressTv
        val serviceCenterPhoneTv = view.serviceCenterPhoneTv
    }
}