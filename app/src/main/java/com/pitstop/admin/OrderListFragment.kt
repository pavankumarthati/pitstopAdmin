package com.pitstop.admin

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_order_list.view.*


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

class OrderListFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var cityFilterSpinner: AppCompatSpinner
    private lateinit var areaFilterSpinner: AppCompatSpinner
    private lateinit var serviceCenterFilter: AppCompatSpinner
    private lateinit var ordersRv: RecyclerView
    private lateinit var cityList: ArrayList<String>
    private lateinit var areaList: ArrayList<String>
    private lateinit var serviceCenterList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_order_list, container, false)
        cityFilterSpinner = rootView.cityFilter
        areaFilterSpinner = rootView.areaFilter
        serviceCenterFilter = rootView.employeeFilter
        ordersRv = rootView.ordersRv
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cityList = ArrayList()
        cityList.add("City")
        cityList.add("Malaysia")
        val cityAdapter = OrderFilterAdapter(context!!, android.R.layout.simple_spinner_item, cityList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        cityFilterSpinner.adapter = cityAdapter
        cityFilterSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    (parent?.getChildAt(0) as TextView).setTextColor(context!!.resources.getColor(R.color.gray_300))
                }
            }

        }


        ordersRv.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        ordersRv.addItemDecoration(
            RecyclerViewChildMarginDecoration(
                resources.getDimensionPixelSize(R.dimen.order_item_start_margin),
                resources.getDimensionPixelSize(R.dimen.order_item_start_margin),
                resources.getDimensionPixelSize(R.dimen.order_item_start_margin),
                resources.getDimensionPixelSize(R.dimen.order_item_start_margin),
                LinearLayout.VERTICAL
            )
        )

        ordersRv.adapter = OrdersAdapter()

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


class OrdersAdapter(): RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_layout, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

    }


    class OrderViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
}
