package com.pitstop.admin

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class OrderFilterAdapter(
    context: Context,
    resource: Int,
    objects: List<String>
) : ArrayAdapter<String>(context, resource, objects) {

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView

        if (position == 0) {
            view.setTextColor(view.context.resources.getColor(R.color.gray_300))
        } else {
            view.setTextColor(view.context.resources.getColor(R.color.black))
        }
        return view
    }
}
