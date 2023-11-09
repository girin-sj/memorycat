package com.example.memorycat

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TodayWordStudyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_todayword_study)
        val binding = ActivityQuizMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //발바닥 회식으로 색 변형 동적할당
        //image_view.setColorFilter(Color.parseColor("#D2D2D20"))
        //image_view.imageTintList.valueOf(Color.parseColor("#D2D2D2"))

        //studyNextButton -> 다음 단어로 이동

    }
}