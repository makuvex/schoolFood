package com.jungbae.schoolfood.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onShow
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.jakewharton.rxbinding3.view.clicks
import com.jungbae.schoolfood.R
import com.jungbae.schoolfood.SchoolFoodApplication
import com.jungbae.schoolfood.network.*
import com.jungbae.schoolfood.network.preference.PreferenceManager
import com.jungbae.schoolfood.network.preference.PreferencesConstant
import com.jungbae.schoolfood.view.HomeRecyclerAdapter
import com.jungbae.schoolfood.view.increaseTouchArea
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.search
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.recycler_view
import kotlinx.android.synthetic.main.content_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

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
    private lateinit var selectedSubject: PublishSubject<SimpleSchoolMealData>
    private lateinit var deleteSubject: PublishSubject<SimpleSchoolMealData>
    private lateinit var backPressedSubject: BehaviorSubject<Long>

    private lateinit var emitBlockSubject: PublishSubject<Unit>

    private lateinit var interstitialAd: InterstitialAd
    private var adBlock: () -> Unit = {
        if(interstitialAd.isLoaded) {
            interstitialAd.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("@@@","@@@ onCreate")
        setContentView(R.layout.activity_main)
        initAd()

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
        Log.e("@@@","@@@ init")

        selectedSubject = PublishSubject.create()
        deleteSubject = PublishSubject.create()
        backPressedSubject = BehaviorSubject.createDefault(0L)
        emitBlockSubject = PublishSubject.create()

        schoolMealList = ArrayList()
        cardAdapter = HomeRecyclerAdapter(schoolMealList, selectedSubject, deleteSubject)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.clear()
    }

    override fun onBackPressed() {
        backPressedSubject.onNext(System.currentTimeMillis())
    }

    fun initAd() {
        MobileAds.initialize(this, getString(com.jungbae.schoolfood.R.string.admob_app_id))
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = resources.getString(R.string.full_ad_unit_id_for_test)
        interstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() { Log.e("@@@","interstitialAd onAdLoaded") }
            override fun onAdFailedToLoad(errorCode : Int) { Log.e("@@@","interstitialAd onAdFailedToLoad code ${errorCode}") }
            override fun onAdClosed() {
                Log.e("@@@","onAdClosed")
                interstitialAd.loadAd(AdRequest.Builder().build())
                emitBlockSubject.onNext(Unit)
            }
        }
        interstitialAd.loadAd(AdRequest.Builder().build())

        adView.loadAd(AdRequest.Builder().build())
        adView.adListener = object: AdListener() {
            override fun onAdLoaded() { Log.e("@@@","banner onAdLoaded") }
            override fun onAdFailedToLoad(errorCode : Int) { Log.e("@@@","banner onAdFailedToLoad code ${errorCode}") }
            override fun onAdOpened() { Log.e("@@@","onAdOpened") }
            override fun onAdLeftApplication() { Log.e("@@@","onAdLeftApplication") }
            override fun onAdClosed() { Log.e("@@@","onAdClosed") }
        }
    }

    fun initializeUI() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = cardAdapter
        }
        increaseTouchArea(search, 50)
        increaseTouchArea(option, 50)
        option.isSelected = false
    }

    fun bindUI() {

        val backDisposable =
            backPressedSubject
                .buffer(2, 1)
                .map{ Pair(it[0], it[1]) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(it.second - it.first < TimeUnit.SECONDS.toMillis(2)) {
                        finish()
                    } else {
                        Toast.makeText(this, "뒤로 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                    }
                }

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
            .doAfterNext {
                if(option.isSelected) search.visibility = View.INVISIBLE else search.visibility = View.VISIBLE
            }
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


        val itemClicksDisposable = selectedSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .flatMap { combineLatest(Observable.just(adBlock), emitBlockSubject) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                Log.e("@@@", "item clicks ${result}")

//                showInterstitialAd {
//                    Log.e("","")
//                    val intent = Intent(this, SchoolFoodDetailActivity::class.java)
//                    intent.putExtra(PreferencesConstant.SCHOOL_CODE, meal.schoolCode)
//                    intent.putExtra(PreferencesConstant.OFFICE_SC_CODE, meal.officeCode)
//                    intent.putExtra(PreferencesConstant.SCHOOL_NAME, meal.name)
//                    startActivity(intent)
//                }

//                if(interstitialAd.isLoaded) {
//                    interstitialAd.show()
//
//                }
//                val intent = Intent(this, SchoolFoodDetailActivity::class.java)
//                intent.putExtra(PreferencesConstant.SCHOOL_CODE, it.schoolCode)
//                intent.putExtra(PreferencesConstant.OFFICE_SC_CODE, it.officeCode)
//                intent.putExtra(PreferencesConstant.SCHOOL_NAME, it.name)
//                startActivity(intent)
            }

        val itemDeleteDisposable = deleteSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("@@@", "item delete ${it}")
                showMaterialDialog(it)
            }

        disposeBag.addAll(backDisposable, searchDisposable, optionDisposable, itemClicksDisposable, itemDeleteDisposable)
    }

    private fun combineLatest(ob: Observable<() -> Unit>, ob2: Observable<Unit>): Observable<Pair<Unit, Unit>> {
        return Observables.combineLatest(ob, ob2){ execution, adClosed  ->
            Pair(Unit, Unit)
        }
    }

    private fun showInterstitialAd(block: (() -> Unit)?): Unit {
        if(interstitialAd.isLoaded) {
            interstitialAd.show()
        }
//        block?.let{
//            commonBlock = it
//        }
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
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val today = current.format(formatter)
        val todayDesc = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

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

    fun reloadRecylerView() {
        cardAdapter.notifyDataSetChangedWith(option.isSelected)
    }

    fun showMaterialDialog(data: SimpleSchoolMealData) {
        MaterialDialog(this).show {
            positiveButton(text = "확인") {
                Toast.makeText(SchoolFoodApplication.context, "${data.name} 홈카드가 삭제 되었습니다.", Toast.LENGTH_SHORT).show()

                if(schoolMealList.removeIf { school ->  school.schoolCode == data.schoolCode && school.officeCode == data.officeCode }) {
                    PreferenceManager.removeSchoolData(data.officeCode, data.schoolCode)
                    reloadRecylerView()
                }
                if(schoolMealList.size == 0) {
                    //option.callOnClick()
                }
                //PreferenceManager.addSchoolData(data)
            }
            negativeButton(text = "취소") {
                Toast.makeText(SchoolFoodApplication.context, "취소 되었습니다.", Toast.LENGTH_SHORT).show()
            }
            onShow {
                title(text = "알림")
                message(text = "${data.name}을(를) 홈카드에서 삭제 할까요?")
            }

        }
    }
}
