package com.jungbae.schoolfood.network.preference

import androidx.appcompat.app.AppCompatActivity
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.adapters.StringSet
import com.afollestad.rxkprefs.rxkPrefs
import com.google.gson.GsonBuilder
import com.jungbae.schoolfood.SchoolFoodApplication
import com.jungbae.schoolfood.network.SimpleSchoolData

object PreferencesConstant {
    val CHILD_NAME = "CHILD_NAME"
    val CHILD_HASH_ID = "CHILD_HASH_ID"

    val OFFICE_SC_CODE = "OFFICE_SC_CODE"
    val OFFICE_SC_NAME = "OFFICE_SC_NAME"

    val SCHOOL_CODE = "SCHOOL_CODE"
    val SCHOOL_NAME = "SCHOOL_NAME"
}

class PreferenceManager {

    companion object {
        private var instance: RxkPrefs? = null
        private val self: PreferenceManager =
            PreferenceManager()
        //private lateinit var schoolCodeList: MutableList<SimpleSchoolData>

        init {
            instance = instance ?: rxkPrefs(SchoolFoodApplication.context, SchoolFoodApplication.context.packageName, AppCompatActivity.MODE_PRIVATE)
            instance?.let {
                //schoolCodeList = listOf<SimpleSchoolData>().toMutableList()
            }
        }

//        @JvmStatic
//        var officeCode: String?
//            get() = instance?.let { it.string(PreferencesConstant.OFFICE_SC_CODE).get() }
//            set(id) {
//                instance?.let {
//                    it.string(PreferencesConstant.OFFICE_SC_CODE).set(id ?: "")
//                }
//            }

//        @JvmStatic
//        var schoolCodeList: MutableSet<String?
//            get() = instance?.let { it.string(PreferencesConstant.SCHOOL_CODE).get() }
//            set(id) {
//                instance?.let {
//                    it.string(PreferencesConstant.SCHOOL_CODE).set(id ?: "")
//                }
//            }

        @JvmStatic
        var schoolData: MutableSet<SimpleSchoolData>?
            get() {
                instance?.run {
                    val gson = GsonBuilder().create()
                    val data = instance?.let { it.stringSet(PreferencesConstant.SCHOOL_CODE).get() }
                    return data?.map { gson.fromJson(it, SimpleSchoolData::class.java) }?.toMutableSet()
                }
                return null
            }
            set(data) {
                instance?.let {
                    data?.run {
                        val gson = GsonBuilder().create()
                        val set = data.map { gson.toJson(it) }.toMutableSet()
                        it.stringSet(PreferencesConstant.SCHOOL_CODE, set)
                    }
                }
            }

        fun addSchoolData(schoolData: SimpleSchoolData) {
            instance?.run {
                val preSet = stringSet(PreferencesConstant.SCHOOL_CODE).get()
                if(preSet == null) {
                    val set = mutableSetOf<SimpleSchoolData>()
                    set.add(schoolData)
                    stringSet(PreferencesConstant.SCHOOL_CODE, set.map { GsonBuilder().create().toJson(it) }.toMutableSet())
                } else {
                    preSet?.add(GsonBuilder().create().toJson(schoolData))
                    stringSet(PreferencesConstant.SCHOOL_CODE).set(preSet)
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



