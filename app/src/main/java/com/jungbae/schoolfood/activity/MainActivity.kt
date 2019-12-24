package com.jungbae.schoolfood.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.jakewharton.rxbinding3.view.clicks
import com.jungbae.schoolfood.R
import com.jungbae.schoolfood.network.*
import com.jungbae.schoolfood.network.preference.PreferenceManager
import com.jungbae.schoolfood.network.preference.PreferencesConstant
import com.jungbae.schoolfood.view.HomeRecyclerAdapter
import com.jungbae.schoolfood.view.increaseTouchArea
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.search
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.recycler_view
import kotlinx.android.synthetic.main.content_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


enum class SchoolDataIndex(val index: Int) {
    HEAD(0),
    ROW(1),
    RESULT_CODE(1)
}


enum class ActivityResultIndex(val index: Int) {
    SEARCH(0),
    OPTION(1)
}

class MainActivity : AppCompatActivity() {
    private val disposeBag = CompositeDisposable()

    private lateinit var schoolMealList: ArrayList<SimpleSchoolMealData>
    private lateinit var cardAdapter: HomeRecyclerAdapter
    private lateinit var selectedBehaviorSubject: PublishSubject<SimpleSchoolMealData>
    private lateinit var deleteBehaviorSubject: PublishSubject<SimpleSchoolMealData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setDisplayShowTitleEnabled(false)
            //it.setDisplayHomeAsUpEnabled(false)
            //it.setHomeAsUpIndicator(R.drawable.btn_back)
        }

        initializeUI()
        bindUI()

        requestMealInfo()

//        fab.setOnClickListener { view ->
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    init {
        selectedBehaviorSubject = PublishSubject.create()
        deleteBehaviorSubject = PublishSubject.create()
        schoolMealList = ArrayList()
        cardAdapter = HomeRecyclerAdapter(schoolMealList, selectedBehaviorSubject, deleteBehaviorSubject)

    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.clear()
    }

    fun initializeUI() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = cardAdapter
        }
        increaseTouchArea(search, 50)
        increaseTouchArea(option, 50)
        option.isSelected = false

        MobileAds.initialize(this, getString(com.jungbae.schoolfood.R.string.admob_app_id))
        adView.loadAd(AdRequest.Builder().build())
        adView.adListener = object: AdListener() {
            override fun onAdLoaded() { Log.e("@@@","onAdLoaded") }
            override fun onAdFailedToLoad(errorCode : Int) { Log.e("@@@","onAdFailedToLoad") }
            override fun onAdOpened() { Log.e("@@@","onAdOpened") }
            override fun onAdLeftApplication() { Log.e("@@@","onAdLeftApplication") }
            override fun onAdClosed() { Log.e("@@@","onAdClosed") }
        }
    }

    fun bindUI() {
        val searchDisposable = search.clicks()
            .throttleFirst(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startActivity(ActivityResultIndex.SEARCH.index)
                Log.d("@@@", "searchDisposable")
            }

        val optionDisposable = option.clicks()
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                option.isSelected = !option.isSelected
                val resId: Int = when(option.isSelected) {
                    true -> R.drawable.cancel
                    false -> R.drawable.trash
                }

                option.setBackgroundResource(resId)
                cardAdapter.notifyDataSetChangedWith(option.isSelected)
                Log.d("@@@", "optionDisposable")
            }

        val itemClicksDisposable = selectedBehaviorSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("@@@", "item clicks ${it}")

//                if(it.meal.isEmpty()) {
//                    Toast.makeText(this, "급식 정보가 없습니다.", Toast.LENGTH_SHORT).show()
//                    return@subscribe
//                }
                val intent = Intent(this, SchoolFoodDetailActivity::class.java)
                intent.putExtra(PreferencesConstant.SCHOOL_CODE, it.schoolCode)
                intent.putExtra(PreferencesConstant.OFFICE_SC_CODE, it.officeCode)
                intent.putExtra(PreferencesConstant.SCHOOL_NAME, it.name)
                startActivity(intent)
            }

        val itemDeleteDisposable = deleteBehaviorSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("@@@", "item delete ${it}")

            }

        disposeBag.addAll(searchDisposable, optionDisposable, itemClicksDisposable, itemDeleteDisposable)
    }

    fun startActivity(index: Int) {
        when(index) {
            ActivityResultIndex.SEARCH.index ->
                startActivityForResult(Intent(this, SearchSchoolActivity::class.java), ActivityResultIndex.SEARCH.index)
            ActivityResultIndex.OPTION.index ->
                startActivity(Intent(this, SearchSchoolActivity::class.java))
        }
    }

    fun reloadData() {
        Log.d("@@@","@@@ reloadData @@@")

        requestMealInfo()

        schoolMealList.clear()
        cardAdapter.notifyDataSetChangedWith(option.isSelected)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("@@@","@@@ onActivityResult ${resultCode} @@@")
        if (requestCode == ActivityResultIndex.SEARCH.index) {
            when (resultCode) {
                Activity.RESULT_OK -> reloadData()
            }
        }
    }

    fun requestMealInfo() {
        val data = PreferenceManager.schoolData
        //val data = SchoolFoodPreference.schoolData


//        if(officeCode ==  || schoolCode == "") {
//            updateUI(null)
//            return
//        }

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val today = current.format(formatter)
        val todayDesc = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
/*
        val list = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        list.toObservable() // extension function for Iterables
            .flatMap { Observable.just(String.format("%s_", it)) }
            .subscribeBy(  // named arguments for lambda Subscribers
                onNext = {
                    Log.e("@@@","@@@ onNext ${it}")
                },
                onError =  { it.printStackTrace() },
                onComplete = { println("Done!") }
            )
*/
        Log.d("", "@@@ requestMealInfo")
        schoolMealList.clear()

        data?.let{
            if(data.isNotEmpty()) {
                home_no_data_view.visibility = View.GONE
                recycler_view.visibility = View.VISIBLE
            }
            data.toList().toObservable().observeOn(AndroidSchedulers.mainThread()).flatMap {
                //NetworkService.getInstance().getSchoolMealData("json", 1, 100, it.officeCode, it.schoolCode, "05b9d532ceeb48dd89238746bd9b0e16", today, today)
                Log.e("@@@@@@@@@@@@@@@@@@@", "flatMap ${it.reflectionToString()}")

                addCard(SimpleSchoolMealData(it.name, todayDesc, "", "", "", it.schoolCode, it.officeCode))
                NetworkService.getInstance().getSchoolMealData("json", 1, 100, it.officeCode, it.schoolCode, "05b9d532ceeb48dd89238746bd9b0e16", today, today)
            }
            .subscribeWith(ObservableResponse<SchoolMealData>(
                onSuccess = {
                    val data = it.mealServiceDietInfo.get(1).row.map { data ->
                        SimpleSchoolMealData(data.schoolName, todayDesc, data.dishName, data.mealName, data.calInfo, data.schoolCode, data.eduOfficecode)
                    }.first()

                    data?.let {
                        //updateUI(it)
                        updateCard(it)
                    }

                    Log.e("@@@@@", "onSuccess ${it.reflectionToString()}")
                    //Log.d("@@@@@", "onSuccess list ${list}")
                }, onError = {
                    Log.e("@@@@@", "@@@@@ error $it")
                }
            ))

                    /*
                .subscribeBy(onNext = {
                    Log.e("@@@","@@@ onNext ${it}")
                })
                */

            /*
            .subscribeWith(ObservableResponse<SchoolMealData>(
                onSuccess = {
                    val list = it.mealServiceDietInfo.get(1).row.map { data ->
                        SimpleSchoolMealData(data.schoolName, today, data.dishName, data.mealName, data.calInfo)
                    }
                    list?.let {
                        updateUI(it)
                    }

                    Log.d("@@@@@", "onSuccess ${it.reflectionToString()}")
                    //Log.d("@@@@@", "onSuccess list ${list}")
                }, onError = {
                    Log.d("@@@", "@@@@@ error $it")
                }
            ))
*/


//            data.forEach {
//                NetworkService.getInstance().getSchoolMealData("json", 1, 100, it.officeCode, it.schoolCode, "05b9d532ceeb48dd89238746bd9b0e16", today, today)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(ObservableResponse<SchoolMealData>(
//                        onSuccess = {
//                            val list = it.mealServiceDietInfo.get(1).row.map { data ->
//                                SimpleSchoolMealData(data.schoolName, today, data.dishName, data.mealName, data.calInfo)
//                            }
//                            list?.let {
//                                updateUI(it)
//                            }
//
//                            Log.d("@@@@@", "onSuccess ${it.reflectionToString()}")
//                            //Log.d("@@@@@", "onSuccess list ${list}")
//                        }, onError = {
//                            Log.d("@@@", "@@@@@ error $it")
//                        }
//                    ))
//
//            }


        }
    }

    fun addCard(data: SimpleSchoolMealData) {
        AndroidSchedulers.mainThread().scheduleDirect {
            schoolMealList.add(data)
            cardAdapter.notifyDataSetChangedWith(option.isSelected)
        }
    }

    fun updateCard(data: SimpleSchoolMealData) {
        AndroidSchedulers.mainThread().scheduleDirect {
            val index = schoolMealList.indexOfFirst{ it.name == data.name }
            schoolMealList.set(index, data)
            cardAdapter.notifyDataSetChangedWith(option.isSelected)
        }
    }


    fun updateUI(list: List<SimpleSchoolMealData>?) {
        AndroidSchedulers.mainThread().scheduleDirect {
            list?.let {
                if (it.isNotEmpty()) {
                    home_no_data_view.visibility = View.GONE
                    //schoolMealList.clear()
                    schoolMealList.addAll(it)
                    cardAdapter.notifyDataSetChangedWith(option.isSelected)
                } else {
                    home_no_data_view.visibility = View.VISIBLE
                    recycler_view.visibility = View.GONE
                }
                return@scheduleDirect
            }
            home_no_data_view.visibility = View.VISIBLE
            recycler_view.visibility = View.GONE
        }
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
