package com.jungbae.schoolfood

import android.provider.Settings
import android.util.Log
import com.google.firebase.database.*
import com.jungbae.schoolfood.network.SimpleSchoolData


class FirebaseService {

    companion object {
        private var instance: FirebaseService? = null
        private lateinit var dbRef: DatabaseReference
        val androidId: String
            get() = Settings.Secure.getString(SchoolFoodApplication.context.contentResolver, Settings.Secure.ANDROID_ID)

        fun getInstance(): FirebaseService {
            if(instance == null) {
                instance = FirebaseService()
                dbRef = FirebaseDatabase.getInstance().getReference("user")
            }
            return instance?.let{ it } ?: FirebaseService()
        }
    }

    fun createSchoolData(data: SimpleSchoolData) {
        Log.e("","@@@ androidId ${androidId}")
        var list = mutableListOf<SimpleSchoolData>()
        list.add(data)

        val query = dbRef.child(androidId)
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.e("","@@@ onDataChange")
                p0.children.forEach {
                    Log.e("","@@@ it ${it}")
                    val s = it.getValue(SimpleSchoolData::class.java)
                    s?.let { data ->
                        list.add(data)
                    }
                }

                var set = HashMap<String, List<SimpleSchoolData>>()
                set[androidId] = list
                dbRef.setValue(set)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("","@@@ onCancelled")
            }
        })

    }




}


