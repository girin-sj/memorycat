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
            if (counter==10){
                startActivity(intent)
            }
            else {
                counter++
                binding.quizNumber.text = "$counter/10"
            }
        }
    }
}