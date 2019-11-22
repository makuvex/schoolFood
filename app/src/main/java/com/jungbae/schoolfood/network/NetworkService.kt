package com.jungbae.schoolfood.network

import android.content.Context
import java.util.ArrayList
import io.reactivex.Observable
import okhttp3.Interceptor

open class NetworkService {

    companion object {

    }
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

    fun
}