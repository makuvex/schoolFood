package com.jungbae.schoolfood

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.jungbae.schoolfood.network.NetworkService
import com.jungbae.schoolfood.network.ObservableResponse
import com.jungbae.schoolfood.network.SchoolData
import com.jungbae.schoolfood.network.reflectionToString
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val disposeBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        bindUI()
        fab.setOnClickListener { view ->
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//

            NetworkService.getInstance().getSchoolData("json", 1, 100, "신중").subscribeWith(ObservableResponse<SchoolData>(
                onSuccess = {

                    val school = it
                    Log.d("@@@@@", "onSuccess ${it.reflectionToString()}")
                }, onError = {
                    Log.d("@@@", "@@@@@ error $it")
                }
            ))
        }
    }

    fun bindUI() {

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
