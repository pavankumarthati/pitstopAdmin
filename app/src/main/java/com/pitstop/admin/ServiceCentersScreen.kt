package com.pitstop.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_service_centers.view.*

class ServiceCentersFragment: Fragment() {

    private var originalServiceCenters: ArrayList<ServiceCenterModel>? = null
    private var displayedServiceCenters: ArrayList<ServiceCenterModel>? = null
    private lateinit var serviceCentersRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}


class ServiceCentersAdapter(): RecyclerView.Adapter<ServiceCentersAdapter.ServiceCenterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceCenterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.service_center_item, parent, false)
        return ServiceCenterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: ServiceCenterViewHolder, position: Int) {

    }


    class ServiceCenterViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
}