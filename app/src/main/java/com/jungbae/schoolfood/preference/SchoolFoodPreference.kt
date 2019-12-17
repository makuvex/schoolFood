package com.jungbae.schoolfood.network.preference

import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.jungbae.schoolfood.SchoolFoodApplication
import com.jungbae.schoolfood.network.SimpleSchoolData

object SchoolFoodPreferencesConstant {
    val CHILD_NAME = "CHILD_NAME"
    val CHILD_HASH_ID = "CHILD_HASH_ID"

    val OFFICE_SC_CODE = "OFFICE_SC_CODE"
    val OFFICE_SC_NAME = "OFFICE_SC_NAME"

    val SCHOOL_CODE = "SCHOOL_CODE"
    val SCHOOL_NAME = "SCHOOL_NAME"
}

class SchoolFoodPreference {

    companion object {
        private var instance: SharedPreferences? = null

        init {
            instance = SchoolFoodApplication.context.getSharedPreferences("SchoolFood", 0)
        }


        @JvmStatic
        var schoolData: MutableSet<SimpleSchoolData>?
            get() {
                instance?.run {
                    val gson = GsonBuilder().create()
                    val data = instance?.let { it.getStringSet(PreferencesConstant.SCHOOL_CODE, null) }
                    Log.e("@@@","schoolData get data ${data}")
                    return data?.map { gson.fromJson(it, SimpleSchoolData::class.java) }?.toMutableSet()
                }
                return null
            }
            set(data) {
                instance?.let {
                    data?.run {
                        Log.e("@@@","schoolData set data@@@")
                        val gson = GsonBuilder().create()
                        val set = data.map { gson.toJson(it) }.toMutableSet()
                        it.edit().putStringSet(PreferencesConstant.SCHOOL_CODE, set)
                        //it.stringSet(PreferencesConstant.SCHOOL_CODE, set)
                    }
                }
            }

        fun addSchoolData(schoolData: SimpleSchoolData) {
            instance?.run {
                val preSet = getStringSet(PreferencesConstant.SCHOOL_CODE, null)
                if(preSet == null) {
                    Log.e("@@@","preSet null set will be changed")
                    val set = mutableSetOf<SimpleSchoolData>()
                    set.add(schoolData)
                    edit().putStringSet(PreferencesConstant.SCHOOL_CODE, set.map { GsonBuilder().create().toJson(it) }.toMutableSet())
                } else {
                    preSet.forEach { Log.e("@@@","@@@ preSet.forEach ${it}") }
                    preSet?.add(GsonBuilder().create().toJson(schoolData))
                    edit().putStringSet(PreferencesConstant.SCHOOL_CODE, preSet)
                }
            }
        }

//
//        @JvmStatic
//        var officeCodeList: MutableSet<String>?
//            get() = instance?.let { it.stringSet(PreferencesConstant.OFFICE_SC_CODE).get() }
//            private set(id) {
//                instance?.let {
//                    it.stringSet(PreferencesConstant.OFFICE_SC_CODE).set(id!!)
//                }
//            }
//
//        fun addOfficeCode(code: String) {
//            instance?.let {
//                var list = this.officeCodeList
//                list?.let {
//                    it.add(code)
//                    officeCodeList = list
//                }
//            }
//        }

//        @JvmStatic
//        var officeCode: Long?
//            get() = instance?.let { it.long(PreferencesConstant.OFFICE_SC_CODE).get() }
//            set(time) {
//                instance?.let {
//                    it.long(PreferencesConstant.OFFICE_SC_CODE).set(time ?: 0)
//                }
//            }
    }

}



