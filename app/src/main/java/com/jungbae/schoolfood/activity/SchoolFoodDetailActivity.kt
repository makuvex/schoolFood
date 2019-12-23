package com.jungbae.schoolfood.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onShow
import com.jungbae.schoolfood.R
import com.jungbae.schoolfood.SchoolFoodApplication
import com.jungbae.schoolfood.network.*
import com.jungbae.schoolfood.network.preference.PreferenceManager
import com.jungbae.schoolfood.network.preference.PreferencesConstant
import com.jungbae.schoolfood.view.MealDetailRecyclerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_meal_detail.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class SchoolFoodDetailActivity : AppCompatActivity() {
    private val disposeBag = CompositeDisposable()

    private lateinit var mealList: ArrayList<SimpleSchoolMealData>
    private lateinit var mealAdapter: MealDetailRecyclerAdapter

    var schoolCode: String? = null
    var officeCode: String? = null

    private lateinit var selectedBehaviorSubject: PublishSubject<SimpleSchoolMealData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_detail)

        intent?.let {
            officeCode = it.getStringExtra(PreferencesConstant.OFFICE_SC_CODE)
            schoolCode = it.getStringExtra(PreferencesConstant.SCHOOL_CODE)
        }

        initializeUI()
        bindRxUI()

        requestMealInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.clear()
    }

    init {
        selectedBehaviorSubject = PublishSubject.create()
        mealList = ArrayList()
        mealAdapter = MealDetailRecyclerAdapter(mealList, selectedBehaviorSubject)
    }

    fun initializeUI() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = mealAdapter
        }
    }

    fun bindRxUI() {
//        val searchDisposable = search.clicks()
//            .throttleFirst(1, TimeUnit.SECONDS)
//            .filter{ edit_text.length() > 0 }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe{
//                NetworkService.getInstance().getSchoolData("json", 1, 100, edit_text.text.toString())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(ObservableResponse<SchoolData>(
//                        onSuccess = {
//                            val list = it.schoolInfo.get(1).row.map{ data ->
//                                SimpleSchoolData(data.schoolName, data.roadNameAddress, data.schoolCode, data.eduOfficecode)
//                            }
//
//                            list?.let {
//                                updateUI(it)
//                            }
//
//                            Log.d("@@@@@", "onSuccess ${it.reflectionToString()}")
//                            Log.d("@@@@@", "onSuccess list ${list}")
//                        }, onError = {
//                            Log.d("@@@", "@@@@@ error $it")
//                        }
//                ))
//            }
//
//        val subjectDisposable = selectedBehaviorSubject.filter{ it != null }.subscribe {
//            //Toast.makeText(SchoolFoodApplication.context, it.name, Toast.LENGTH_SHORT).show()
//            showMaterialDialog(it)
//        }
//
//        disposeBag.addAll(searchDisposable, subjectDisposable)
    }

    fun showMaterialDialog(data: SimpleSchoolData) {
        MaterialDialog(this).show {
            positiveButton(text = "확인") {
                Toast.makeText(SchoolFoodApplication.context, "positiveButton", Toast.LENGTH_SHORT).show()

                PreferenceManager.addSchoolData(data)
                //SchoolFoodPreference.addSchoolData(data)

//                PreferenceManager.schoolCode = data.schoolCode
//                PreferenceManager.officeCode = data.officeCode

                setResult(RESULT_OK, Intent())
                (windowContext as SearchSchoolActivity).finish()
            }
            negativeButton(text = "취소") {
                Toast.makeText(SchoolFoodApplication.context, "negativeButton", Toast.LENGTH_SHORT).show()
            }
            onShow {
                title(text = "알림")
                message(text = "${data.name}를 홈카드에 추가 할까요?")
            }

        }
    }

    fun requestMealInfo() {
        val scCode = schoolCode?.let {it} ?: return
        val ofCode = officeCode?.let {it} ?: return


        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val today = current.format(formatter)
        val todayDesc = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

        Log.d("", "@@@ requestMealInfo")
        mealList.clear()

        val cal = Calendar.getInstance()
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
        val lastDayOfMonth = cal.getTime()

        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE))
        val firstDayOfMonth = cal.getTime()

        val sif = SimpleDateFormat("yyyyMMdd")

        Log.e("@@@","start ${sif.format(firstDayOfMonth)}, last ${sif.format(lastDayOfMonth)}")

        val dispose =
            NetworkService.getInstance().getSchoolMealData("json", 1, 100, ofCode, scCode, "05b9d532ceeb48dd89238746bd9b0e16", sif.format(firstDayOfMonth), sif.format(lastDayOfMonth))
            .subscribeWith(ObservableResponse<SchoolMealData>(
                onSuccess = {
                    val data = it.mealServiceDietInfo.get(1).row.map { data ->
                        SimpleSchoolMealData(data.schoolName, data.mealTime, data.dishName, data.mealName, data.calInfo, data.schoolCode, data.eduOfficecode)
                    }

                    data?.let {
                        detail_title.text = it.first().name
                        addCard(it)
                        //updateUI(it)
                        //updateCard(it)
                    }

                    Log.e("@@@@@", "onSuccess ${it.reflectionToString()}")
                    //Log.d("@@@@@", "onSuccess list ${list}")
                }, onError = {
                    Log.e("@@@@@", "@@@@@ error $it")
                }
            ))

        disposeBag.add(dispose)


    }

    fun addCard(list: List<SimpleSchoolMealData>) {
        AndroidSchedulers.mainThread().scheduleDirect {
            mealList.addAll(list)

            mealAdapter.notifyDataSetChanged()
        }
    }

    fun updateCard(data: SimpleSchoolMealData) {
        AndroidSchedulers.mainThread().scheduleDirect {
            mealList.add(data)
            mealAdapter.notifyDataSetChanged()
//            val index = mealList.indexOfFirst{ it.name == data.name }
//            schoolMealList.set(index, data)
//            cardAdapter.notifyDataSetChanged()
        }
    }

}
