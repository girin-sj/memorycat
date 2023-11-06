package com.example.memorycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memorycat.databinding.ActivityQuizResultBinding
import com.example.memorycat.databinding.ActivityQuizStartBinding

class QuizResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, QuizNoteActivity::class.java)
        binding.quizNextButton.setOnClickListener{startActivity(intent)}
    }
}