package com.example.criminalintent.adapters.crimeadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.criminalintent.R
import com.example.criminalintent.adapters.baseadapter.BaseAdapter
import com.example.criminalintent.adapters.baseadapter.BaseViewHolder
import com.example.criminalintent.models.Crime
import com.example.criminalintent.utils.Utils

class CrimeRecycleAdapter : BaseAdapter<Crime>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Crime> {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.crime_item, parent, false))
    }

    class ViewHolder(itemView: View) : BaseViewHolder<Crime>(itemView) {

        val tv_title: TextView = itemView.findViewById(R.id.crime_title)
        val tv_date: TextView = itemView.findViewById(R.id.crime_date)
        val iv_solved: ImageView = itemView.findViewById(R.id.crime_solved)

        override fun bind(model: Crime) {
            tv_title.text = model.title
            tv_date.text = Utils.dateFormat(model.date)
            iv_solved.visibility = if (model.isSolved) {
                View.VISIBLE
            } else View.GONE
        }

    }
}