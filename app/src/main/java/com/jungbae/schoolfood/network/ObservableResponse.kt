package com.jungbae.schoolfood.network


import android.util.Log
import io.reactivex.observers.DisposableObserver

open class ObservableResponse<T>(val onSuccess: ((T) -> Unit)? = {}, val onError: ((T?) -> Unit)? = {}): DisposableObserver<T>() {

    override fun onNext(t: T) {
        when(checkValidResponseCode(t)) {
            true -> {
                // 정상 응답이나 response 값이 없는 경우
                onSuccess?.let {
                    it(t)
                }
            }
            false -> {
                onError?.let {
                    it(t)
                }
            }
        }
    }

    override fun onError(e: Throwable) {
        Log.d("@@@", "onError 1")
        onError?.let {
            it(null)
        }
    }

    override fun onComplete() {
        Log.d("@@@", "onComplete 1")
    }

    private fun checkValidResponseCode(t: T): Boolean {
        val school: SchoolData = t as SchoolData
        val code = school.schoolInfo.get(SchoolDataIndex.HEAD.index).head.get(SchoolDataIndex.RESULT_CODE.index).result.code

        if (code == "INFO-000") {
            return true
        }
        return false
    }
}