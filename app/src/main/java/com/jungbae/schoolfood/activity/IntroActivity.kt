package com.jungbae.schoolfood.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jungbae.schoolfood.R
import com.jungbae.schoolfood.activity.MainActivity

//data class VersionInfo(var name: String?, var code: String?)

class IntroActivity : AppCompatActivity() {

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




