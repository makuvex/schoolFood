package com.jungbae.schoolfood

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.jungbae.schoolfood.network.SimpleSchoolData

/*
* 1. MainActivity
* 2. SearchSchoolActivity
* 3. SchoolFoodDetailActivity
* 4. click banner ad
* 5. pv Full ad
* */
enum class SchoolFoodPageView(val view: String) {
    MAIN("MainActivity"),
    SEARCH("SearchSchoolActivity"),
    DETAIL("SchoolFoodDetailActivity"),
    BANNER("ClickBanner"),
    FULL_AD("FullAd")
}

class FirebaseService {

    companion object {
        private var instance: FirebaseService? = null
        private lateinit var dbRef: DatabaseReference
        private lateinit var fbAnalytics: FirebaseAnalytics

        val androidId: String
            get() = Settings.Secure.getString(SchoolFoodApplication.context.contentResolver, Settings.Secure.ANDROID_ID)

        fun getInstance(): FirebaseService {
            if(instance == null) {
                instance = FirebaseService()
                dbRef = FirebaseDatabase.getInstance().getReference("user")
                fbAnalytics = FirebaseAnalytics.getInstance(SchoolFoodApplication.context)
            }
            return instance?.let{ it } ?: FirebaseService()
        }
    }

    fun createSchoolData(data: SimpleSchoolData) {
        var list = mutableListOf<SimpleSchoolData>()
        list.add(data)

        val query = dbRef.child(androidId)
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val s = it.getValue(SimpleSchoolData::class.java)
                    s?.let { data ->
                        list.add(data)
                    }
                }

                var set = HashMap<String, List<SimpleSchoolData>>()
                set[androidId] = list
                dbRef.child(androidId).setValue(list)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("","@@@ onCancelled")
            }
        })
    }

    fun logEvent(event: SchoolFoodPageView) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event.view)
        fbAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

}


