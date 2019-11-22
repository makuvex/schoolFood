package com.jungbae.schoolfood.network

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SchoolFoodService {

    companion object {
          fun create(): SchoolFoodService {

            val retrofit = Retrofit.Builder()
                    .baseUrl("https://open.neis.go.kr/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(SchoolFoodService::class.java)
        }
   }

}