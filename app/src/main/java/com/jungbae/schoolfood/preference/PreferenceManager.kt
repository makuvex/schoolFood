package com.jungbae.schoolfood.network.preference

import androidx.appcompat.app.AppCompatActivity
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.rxkPrefs
import com.jungbae.schoolfood.SchoolFoodApplication

object PreferencesConstant {

    val CHILD_NAME = "CHILD_NAME"
    val CHILD_HASH_ID = "CHILD_HASH_ID"

    val OFFICE_SC_CODE = "OFFICE_SC_CODE"
    val OFFICE_SC_NAME = "OFFICE_SC_NAME"

    val CHILD_SCHOOL_CODE = "CHILD_SCHOOL_CODE"
    val CHILD_SCHOOL_NAME = "CHILD_SCHOOL_NAME"
}

class PreferenceManager {

    companion object {
        private var instance: RxkPrefs? = null
        private val self: PreferenceManager =
            PreferenceManager()

        init {
            instance = instance
                ?: rxkPrefs(SchoolFoodApplication.context, SchoolFoodApplication .context.packageName, AppCompatActivity.MODE_PRIVATE)
        }

        @JvmStatic
        var childHashId: String?
            get() = instance?.let { it.string(PreferencesConstant.CHILD_HASH_ID).get() }
            set(id) {
                instance?.let {
                    it.string(PreferencesConstant.CHILD_HASH_ID).set(id ?: "")
                }
            }

        @JvmStatic
        var childName: String?
            get() = instance?.let { it.string(PreferencesConstant.CHILD_NAME).get() }
            set(id) {
                instance?.let {
                    it.string(PreferencesConstant.CHILD_NAME).set(id ?: "")
                }
            }

        @JvmStatic
        var childSchoolName: Long?
            get() = instance?.let { it.long(PreferencesConstant.CHILD_SCHOOL_NAME).get() }
            set(time) {
                instance?.let {
                    it.long(PreferencesConstant.CHILD_SCHOOL_NAME).set(time ?: 0)
                }
            }
    }

}



