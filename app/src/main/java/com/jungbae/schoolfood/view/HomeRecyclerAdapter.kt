package com.jungbae.schoolfood.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jungbae.schoolfood.network.SimpleSchoolMealData
import io.reactivex.subjects.PublishSubject


class HomeRecyclerAdapter(private val list: List<SimpleSchoolMealData>, private var subject: PublishSubject<SimpleSchoolMealData>): RecyclerView.Adapter<HomeCardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardHolder {
        return HomeCardHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: HomeCardHolder, position: Int) {
//        val name = list.get(position).name
//        val address = list.get(position).address
//
        holder.bind(list.get(position), subject)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}