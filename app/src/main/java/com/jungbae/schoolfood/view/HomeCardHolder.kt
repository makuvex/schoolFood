package com.jungbae.schoolfood.view

import android.view.LayoutInflater
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

        var dish = data.meal
        itemView.meal_info.text = dish.replace("<br/>", "\n")

        itemView.meal_time.text = data.mealKind
        itemView.extra_info.text = data.cal

        itemView.setOnClickListener {
            subject?.let {
                it.onNext(data)
            }
        }
    }
}