package com.jungbae.schoolfood.network

import android.content.Context
import android.util.Log
import java.util.ArrayList
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor

/*
open class NetworkService {

    @Volatile private var instance: NetworkService? = null

    fun getInstance(): NetworkService {
        val imInstance = instance
        if(imInstance != null) {
            return imInstance
        }

        instance = NetworkService()
        return synchronized(this) {
            val i = instance
            return i!!
        }
    }
}
*/

class NetworkService {

    companion object {
        private var instance: NetworkService? = null

        fun getInstance(): NetworkService {
            if(instance == null) {
                instance = NetworkService()

            }
            return instance!!
        }
    }

    private val service: SchoolFoodService

    init {
        service = SchoolFoodService.create()
    }

    /*
    fun getSchoolData(@Query("Type") type: String,
                   @Query("pIndex") index: Int,
                   @Query("pSize") size: Int,
                   @Query("SCHUL_NM") name: String): Single<SchoolData>

     */
    fun getSchoolData(type: String, index: Int, size: Int, schoolName: String): Observable<SchoolData> {
        return service.getSchoolData(type, index, size, schoolName)
    }

    fun getSchoolMealData(type: String,
                          index: Int,
                          size: Int,
                          officeCode: String,
                          schoolCode: String,
                          authKey: String,
                          fromDate: String,
                          toDate: String): Observable<SchoolMealData> {

        Log.e("@@@", "officeCode ${officeCode}, schoolCode ${schoolCode}")
        return service.getSchoolMealData(type, index, size, officeCode, schoolCode, authKey, fromDate, toDate)
    }
}

