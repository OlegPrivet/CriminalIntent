package com.example.criminalintent.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.criminalintent.models.Crime

class CrimeListDiffUtils<P>(private val oldList: List<P>, private val newList: List<P>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val crimeOld = oldList[oldItemPosition] as Crime
        val crimeNew = newList[newItemPosition] as Crime
        return crimeOld.id == crimeNew.id
                && crimeOld.title == crimeNew.title
                && crimeOld.date == crimeNew.date
                && crimeOld.isSolved == crimeNew.isSolved
    }
}