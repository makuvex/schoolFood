package com.jungbae.schoolfood.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SchoolFoodApiInterface {

    // https://open.neis.go.kr/hub/schoolInfo?Type=json&pIndex=1&pSize=100&SCHUL_NM=신중
    @GET(ApiSetting.Service.GET_SCHOOL_INFO)
    fun getNotices(@Query("Type") type: String,
                   @Query("pIndex") index: Int,
                   @Query("pSize") size: Int,
                   @Query("SCHUL_NM") name: String): Single<SchoolData>

    @GET(ApiSetting.Service.GET_MEAL_SERVIE_INFO)
    fun getMealServiceInfo(@Query("Type") type: String,
                           @Query("pIndex") index: Int,
                           @Query("pSize") size: Int,
                           @Query("ATPT_OFCDC_SC_CODE") officeEduCode: String,
                           @Query("SD_SCHUL_CODE") schoolCode: String,
                           @Query("KEY") authKey: String,
                           @Query("MLSV_FROM_YMD") fromDate: String,
                           @Query("MLSV_TO_YMD") toDate: String): Single<SchoolMealData>

}