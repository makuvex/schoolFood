package com.jungbae.schoolfood.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jungbae.schoolfood.R
import com.jungbae.schoolfood.network.SimpleSchoolMealData
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.home_card_row.view.*

//HomeCardEmptyHolder
//class HomeCardHolder(inflater: LayoutInflater, parent: ViewGroup):
//    RecyclerView.ViewHolder(inflater.inflate(R.layout.home_card_row, parent, false)) {

class HomeCardHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.home_card_row, parent, false)) {

    var data: SimpleSchoolMealData? = null

    fun bind(data: SimpleSchoolMealData, subject: PublishSubject<SimpleSchoolMealData>) {
        this.data = data

        itemView.school_name.text = data.name
        itemView.date.text = data.date

        if(data.meal.isNotEmpty()) {
            itemView.meal_info.text = data.meal.replace("<br/>", "\n")
            itemView.more.visibility = View.VISIBLE
        } else {
            itemView.meal_info.text = "급식 정보 없음"
            itemView.more.visibility = View.GONE
        }

        itemView.meal_time.text = data.mealKind
        itemView.extra_info.text = data.cal

        itemView.setOnClickListener {
            subject?.let {
                it.onNext(data)
            }
        }
    }
}