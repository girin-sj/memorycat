package com.example.memorycat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorycat.databinding.ActivityBookmarkStartBinding

class BookmarkStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_bookmark_start)

        val binding = ActivityBookmarkStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, BookmarkMainActivity::class.java)
        binding.WordMeanButton.setOnClickListener{startActivity(intent)}
    }
}