package com.jungbae.schoolfood.network

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Query

class SchoolFoodService {

    companion object {
        private var t: SchoolFoodApiInterface? = null

        fun create(): SchoolFoodService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://open.neis.go.kr/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            if(t == null) {
                t = retrofit.create(SchoolFoodApiInterface::class.java)
            }

            return SchoolFoodService()
        }
    }

    fun getSchoolData(type: String, index: Int, size: Int, name: String): Observable<SchoolData> {
        t?.let {
            return it.getSchoolData(type, index, size, name).toObservable().compose(ioMain())
        }
        return Observable.empty()
    }


    fun <T> ioMain(): ObservableTransformer<T, T> {
        return ObservableTransformer {
                upstream -> upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(Schedulers.computation())
        }
    }
}