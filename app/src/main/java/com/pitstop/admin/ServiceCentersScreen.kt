package com.pitstop.admin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_service_centers.*
import kotlinx.android.synthetic.main.fragment_service_centers.view.*
import kotlinx.android.synthetic.main.service_center_item.view.*
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ServiceCentersFragment: Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    private var mOriginalServiceCenters: ArrayList<ServiceCenterCatalogue>? = null
    private var mDisplayedServiceCenters: ArrayList<ServiceCenterCatalogue>? = null
    private lateinit var serviceCentersRv: RecyclerView
    private lateinit var mActivity: MainActivity

    @Inject
    lateinit var retrofit: Retrofit
    @Inject
    lateinit var userManagement: UserManagement

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
        val rootView = inflater.inflate(R.layout.fragment_service_centers, container, false)
        serviceCentersRv = rootView.employeesRv
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        serviceCentersRv.layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
        serviceCentersRv.addItemDecoration(VerticalDividerItemDecoration(context!!, 0, resources.getDrawable(R.drawable.horizontal_line_separator_drawable, activity!!.theme)))
        serviceCentersRv.adapter = ServiceCentersAdapter()

        launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                view?.loader?.visibility = View.VISIBLE
                Glide.with(this@ServiceCentersFragment.mActivity)
                    .load(R.drawable.loader)
                    .into(view?.loader as AppCompatImageView)
            }

            var res: Response<ServiceCentersDt>? = null

            res = retrofit.create(ServiceCentersService::class.java).fetchServiceCenters("bangalore")

            withContext(Dispatchers.Main) {
                view?.loader?.visibility = View.GONE
                if (res!!.isSuccessful) {
                    (serviceCentersRv.adapter as ServiceCentersAdapter).addItems(res.body()!!.serviceCenters!!)
                } else {
                    Toast.makeText(
                        this@ServiceCentersFragment.context,
                        "Oops! Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

        serviceCentersBtn.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction().add(R.id.content_main ,AddServiceCenterScreen()).addToBackStack(null).commit()
        }

    }

    companion object {
        val TAG = "ServiceCentersFragment"
    }

}


class ServiceCentersAdapter: RecyclerView.Adapter<ServiceCentersAdapter.ServiceCenterViewHolder>() {

    var items: List<ServiceCenter>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceCenterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.service_center_item, parent, false)
        return ServiceCenterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ServiceCenterViewHolder, position: Int) {
        holder.nameTv.text = items?.get(position)?.name
        holder.jobsAssignedTv.text = "N.A"
        holder.jobsCompletedTv.text = "N.A"
        holder.jobsPendingTv.text = "N.A"
        holder.remarksTv.text = "N.A"
    }

    fun addItems(serviceCenters: List<ServiceCenter>?) {
        this.items = serviceCenters
        notifyDataSetChanged()
    }


    class ServiceCenterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val nameTv: AppCompatTextView = view.nameTv
        val jobsAssignedTv: AppCompatTextView = view.jobsAssignedTv
        val jobsCompletedTv: AppCompatTextView = view.jobsCompletedTv
        val jobsPendingTv: AppCompatTextView = view.jobsPendingTv
        val remarksTv: AppCompatTextView = view.remarksTv

    }
}