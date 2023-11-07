package com.example.memorycat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorycat.databinding.ActivityTodaywordStartBinding

class TodayWordStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_todayword_start)
        val binding = ActivityTodaywordStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent_1 = Intent(this, TodayWordStudyActivity::class.java)
        binding.studyStartButton.setOnClickListener{startActivity(intent_1)}

        val intent_2 = Intent(this, WrongWordStudyActivity::class.java)
        binding.studyStartButton.setOnClickListener{startActivity(intent_2)}
    }
}
