package com.jungbae.schoolfood.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jungbae.schoolfood.R
import com.jungbae.schoolfood.activity.MainActivity
import com.jungbae.schoolfood.network.SimpleSchoolData
import com.jungbae.schoolfood.view.showToast
import okhttp3.internal.Version

//data class VersionInfo(var name: String?, var code: String?)

class IntroActivity : AppCompatActivity() {


    class School(data: SimpleSchoolData) {
        val check = data.also {
            requireNotNull(it.schoolCode)
            print(it.name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        val (name, code) = VersionInfo("버전", "코드")
//        Log.e("@@@","@@@ name ${name}, code ${code}")
        Handler().postDelayed({
            moveMainActivity()
            finish()
        }, 500)
    }

    fun moveMainActivity() {
        startActivity(Intent(this@IntroActivity, MainActivity::class.java)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

}
