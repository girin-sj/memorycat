package com.example.memorycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memorycat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, QuizStartActivity::class.java)
        binding.testButton.setOnClickListener{startActivity(intent)}
    }

}
