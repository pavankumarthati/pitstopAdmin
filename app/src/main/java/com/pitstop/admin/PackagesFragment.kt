package com.pitstop.admin

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_order_list.view.*
import kotlinx.android.synthetic.main.fragment_packages.*
import kotlinx.android.synthetic.main.package_item_layout.view.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import com.google.android.flexbox.JustifyContent
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager



class PackagesFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO
    private lateinit var mActivity: MainActivity
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var cityFilterSpinner: AppCompatSpinner
    private lateinit var areaFilterSpinner: AppCompatSpinner
    private lateinit var serviceCenterFilter: AppCompatSpinner
    private lateinit var packagesRv: RecyclerView
    private lateinit var cityList: ArrayList<String>
    private lateinit var areaList: ArrayList<String>
    private lateinit var packageList: ArrayList<String>
    @Inject
    lateinit var retrofit: Retrofit

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as MainActivity
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
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
        val rootView = inflater.inflate(R.layout.fragment_packages, container, false)
        cityFilterSpinner = rootView.cityFilter
        areaFilterSpinner = rootView.areaFilter
        packagesRv = rootView.ordersRv
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cityList = ArrayList()
        cityList.add("City")
        cityList.addAll(ConfigManager.configModel!!.availableCities!!)
        val cityAdapter = OrderFilterAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, cityList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                if (position == 0 && parent != null && parent!!.childCount > 0) {
                    (parent?.getChildAt(0) as TextView).setTextColor(context!!.resources.getColor(R.color.gray_300))
                    return
                }
            }

        }

        val gridItemDecoration = RecyclerViewChildGridMarginDecoration(resources.getDimensionPixelSize(R.dimen.item_space))
        packagesRv.addItemDecoration(gridItemDecoration)
//        packagesRv.layoutManager = GridLayoutManager(this.context, resources.getInteger(R.integer.package_span_count), RecyclerView.VERTICAL, false)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        packagesRv.layoutManager = layoutManager

        packagesRv.adapter = PackagesAdapter()
        launch {
            /*withContext(Dispatchers.Main) {
                view?.loader?.visibility = View.VISIBLE
                Glide.with(this@PackagesFragment.mActivity)
                    .load(R.drawable.loader)
                    .into(view?.loader as AppCompatImageView)
            }*/
            val res = retrofit.create(PackagesService::class.java).getPackages(6)
            withContext(Dispatchers.Main) {
//                view?.loader?.visibility = View.GONE
                if (res.isSuccessful) {
                    (packagesRv.adapter as PackagesAdapter).addItems(res.body()!!.packages)
                } else {
                    Toast.makeText(
                        this@PackagesFragment.context,
                        "Oops! Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
        }


        addBtn.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction().replace(R.id.content_main ,AddPackageScreen()).addToBackStack(null).commit()
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
        val TAG = "PackagesFragment"
    }

}

class PackagesAdapter: RecyclerView.Adapter<PackagesAdapter.PackageViewHolder>() {

    var items: List<AddPackageModel>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.package_item_layout, parent, false)
        return PackageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.inclusionsTv.text = TextUtils.join(", ", items?.get(position)?.inclusions)
        holder.packageNameTv.text = items?.get(position)?.name
        holder.packageOriginalPrice.text = items?.get(position)?.price?.toString()
        holder.packageSalePrice.text = items?.get(position)?.discountedPrice?.toString()

    }

    fun addItems(items: List<AddPackageModel>) {
        this.items = items
        notifyDataSetChanged()
    }


    class PackageViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val packageNameTv: AppCompatTextView = view.packageNameTv
        val packageSalePrice: AppCompatTextView = view.packageSalePrice
        val packageOriginalPrice = view.packageOriginalPrice
        val inclusionsTv = view.inclusionsTv
    }
}
