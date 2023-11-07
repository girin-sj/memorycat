package com.example.memorycat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorycat.databinding.ActivityQuizStartBinding

class QuizStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityQuizStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, QuizMainActivity::class.java)
        binding.quizStartButton.setOnClickListener{startActivity(intent)}
    }
}