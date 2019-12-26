package com.pitstop.admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.markodevcic.peko.Peko
import com.markodevcic.peko.PermissionResult
import kotlinx.android.synthetic.main.fragment_order_list.view.*
import kotlinx.android.synthetic.main.order_layout.view.*
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OrderListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [OrderListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

const val CITY_HINT = "city"
const val AREA_HINT = "area"
const val SERVICE_CENTER_HINT = "service centers"

class OrderListFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var cityFilterSpinner: AppCompatSpinner
    private lateinit var areaFilterSpinner: AppCompatSpinner
    private lateinit var ordersRv: RecyclerView
    private lateinit var cityList: ArrayList<String>
    private lateinit var areaList: ArrayList<String>
    private lateinit var serviceCenterList: ArrayList<String>
    private lateinit var mActivity: MainActivity
    @Inject
    lateinit var retrofit: Retrofit
    @Inject
    lateinit var userManagement: UserManagement

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (mActivity.application as AdminApp).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_order_list, container, false)
        cityFilterSpinner = rootView.cityFilter
        areaFilterSpinner = rootView.areaFilter
        ordersRv = rootView.ordersRv
        return rootView
    }

    val spinnerAdapter = object: AdapterView.OnItemSelectedListener {
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
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cityList = ArrayList<String>().apply {
            add("city")
            ConfigManager.configModel?.availableCities?.let {
                addAll(it)
            }
        }
        val cityAdapter = OrderFilterAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, cityList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cityFilterSpinner.adapter = cityAdapter
        cityFilterSpinner.onItemSelectedListener = spinnerAdapter
        areaFilterSpinner.onItemSelectedListener = spinnerAdapter

        val gridItemDecoration = RecyclerViewChildGridMarginDecoration(resources.getDimensionPixelSize(R.dimen.item_space))
        ordersRv.addItemDecoration(gridItemDecoration)
        ordersRv.layoutManager = GridLayoutManager(this.context, resources.getInteger(R.integer.span_count), RecyclerView.VERTICAL, false)

        ordersRv.adapter = OrdersAdapter(mActivity, this)

        launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                view?.loader?.visibility = View.VISIBLE
                Glide.with(this@OrderListFragment.mActivity)
                    .load(R.drawable.loader)
                    .into(view?.loader as AppCompatImageView)
            }

            var res: Response<OrdersDt>? = null

            if (userManagement.getRole() == ROLE.ADMIN) {
                res = retrofit.create(OrderService::class.java).fetchOrders("bangalore")
            } else if (userManagement.getRole() == ROLE.EXECUTIVE) {
                res = retrofit.create(OrderService::class.java).fetchOrders("bangalore", ROLE.EXECUTIVE.name, userManagement.getUserId())
            }

            withContext(Dispatchers.Main) {
                view?.loader?.visibility = View.GONE
                if (res!!.isSuccessful) {
                    (ordersRv.adapter as OrdersAdapter).addItems(res.body()!!.orders!!)
                } else {
                    Toast.makeText(
                        this@OrderListFragment.context,
                        "Oops! Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            //throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        val TAG = "OrderListFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


class OrdersAdapter(val activity: MainActivity, val coroutineScope: CoroutineScope): RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    private var items: List<Order>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_layout, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.jobId.text = items?.get(position)?.orderId
        holder.serviceCenterNameTv.text = items?.get(position)?.serviceCenterDetails?.name
        holder.viewCartBtn.text = items?.get(position)?.status
        holder.servicecenterAddressTv.text = items?.get(position)?.serviceCenterDetails?.address
        holder.serviceCenterPhone.text = items?.get(position)?.serviceCenterDetails?.primaryNumber
        holder.vehicleInfoTv.text = items?.get(position)?.vehicleMetadata?.registrationNumber
        holder.customerNameTv.text = ""
        holder.customerAddressTv.text = ""
        holder.customerPhoneTv.text = ""
        holder.serviceExecutiveNameTv.text = items?.get(position)?.assignedExecutiveDetails?.firstName + " " + items?.get(position)?.assignedExecutiveDetails?.lastName
        holder.serviceExecutivePhoneTv.text = items?.get(position)?.assignedExecutiveDetails?.phoneNumber
        holder.callIv.setOnClickListener {
            val phNo = it.tag as String?
            phNo?.let {
                checkAndRequestPermissions(phNo)
            }
        }

    }

    private fun checkAndRequestPermissions(phNo: String) {
        coroutineScope.launch {
            val result = Peko.requestPermissionsAsync(activity, Manifest.permission.CALL_PHONE)

            when (result) {
                is PermissionResult.Granted -> {
                    withContext(Dispatchers.Main) {
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel: $phNo"))
                        activity.startActivity(intent)
                    }

                }
                is PermissionResult.Denied.NeedsRationale -> {
                    withContext(Dispatchers.Main) {
                        MaterialDialog(activity).show {
                            title(R.string.phone_call_permission_title)
                            message(R.string.phone_call_permission_msg)
                            // literal, internally converts to dp so 16dp
                            cornerRadius(res = R.dimen.md_cornder_radius)

                            positiveButton(R.string.agree) {
                                checkAndRequestPermissions(phNo)
                            }
                            negativeButton(R.string.disagree) {
                                Log.i("ODF", "Need Phone call permission to make call")
                                Toast.makeText(activity, "Need phone call permission to make call", Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                }
                is PermissionResult.Denied.DeniedPermanently -> {
                    Log.i("ODF", "Need Phone call permission to make call")
                    Toast.makeText(activity, "Need phone call permission to make call", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Log.i("ODF", "Need Phone call permission to make call")
                    Toast.makeText(activity, "Need phone call permission to make call", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun addItems(items: List<Order>?) {
        this.items = items
        notifyDataSetChanged()
    }


    class OrderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val jobId: AppCompatTextView = view.jobIdTv
        val serviceCenterNameTv: AppCompatTextView = view.serviceCenterNameTv
        val viewCartBtn: MaterialButton = view.viewCartBtn
        val servicecenterAddressTv: AppCompatTextView = view.serviceCenterAddressTv
        val serviceCenterPhone: AppCompatTextView = view.serviceCenterPhone
        val vehicleInfoTv: AppCompatTextView = view.vehicleInfoTv
        val customerNameTv: AppCompatTextView = view.customerNameTv
        val customerAddressTv: AppCompatTextView = view.customerAddressTv
        val customerPhoneTv: AppCompatTextView = view.customerPhoneTv
        val serviceExecutiveNameTv: AppCompatTextView = view.serviceExecutiveNameTv
        val serviceExecutivePhoneTv: AppCompatTextView = view.serviceExecutivePhoneTv
        val callIv: AppCompatImageView = view.callIv

    }
}
