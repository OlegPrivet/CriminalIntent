package com.example.criminalintent.adapters.crimeadapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.example.criminalintent.R
import com.example.criminalintent.adapters.baseadapter.BaseAdapterCallback
import com.example.criminalintent.adapters.baseadapter.BaseViewHolder
import com.example.criminalintent.models.Crime
import com.example.criminalintent.utils.Utils

class CrimeListAdapter: ListAdapter<Crime, BaseViewHolder<Crime>>(DiffCallback()) {

    private var mCallback: BaseAdapterCallback<Crime>? = null

    fun attachCallback(callback: BaseAdapterCallback<Crime>) {
        this.mCallback = callback
    }

    fun detachCallback() {
        this.mCallback = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.crime_item, parent, false)
        return CrimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Crime>, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            mCallback?.onItemClick(getItem(position), holder.itemView)
        }
        holder.itemView.setOnLongClickListener {
            if (mCallback == null) {
                false
            } else {
                mCallback!!.onLongClick(getItem(position), holder.itemView)
            }

        }
    }

    class CrimeViewHolder(itemView: View) : BaseViewHolder<Crime>(itemView) {
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

    class DiffCallback: ItemCallback<Crime>() {

        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return  oldItem.id == newItem.id
                    && oldItem.title == newItem.title
                    && oldItem.date == newItem.date
                    && oldItem.isSolved == newItem.isSolved
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }

}
