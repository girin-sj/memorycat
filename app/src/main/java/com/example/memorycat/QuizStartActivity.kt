package com.example.memorycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memorycat.databinding.ActivityQuizMainBinding
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