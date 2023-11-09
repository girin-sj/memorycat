package com.example.memorycat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorycat.databinding.ActivityQuizMainBinding

class TodayWordStudyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_todayword_study)
        val binding = ActivityQuizMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var counter: Int = 1
        val intent = Intent(this, QuizResultActivity::class.java)

        binding.quizPassButton.setOnClickListener {
            counter++
            binding.quizNumber.text = "$counter/10"
            if (counter == 10) {
                binding.quizPassButton.text = "결과 확인하기"
            } else if (counter > 10) {
                startActivity(intent)

            }
        }
    }
}