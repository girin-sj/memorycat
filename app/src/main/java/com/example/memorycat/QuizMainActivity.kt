package com.example.memorycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memorycat.databinding.ActivityQuizMainBinding

class QuizMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityQuizMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var counter:Int = 1
        val intent = Intent(this, QuizResultActivity::class.java)

        binding.quizPassButton.setOnClickListener{
            counter++
            binding.quizNumber.text = "$counter/10"
            if (counter==10){
                binding.quizPassButton.text = "결과 확인하기"
            }
            else if(counter>10){
                startActivity(intent)
            }
        }
    }
}