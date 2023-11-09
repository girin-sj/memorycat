package com.example.memorycat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorycat.databinding.FragmentTodaywordEndBinding

class TodayWordEndFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentTodaywordEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //북마크 화면으로 이동
        //오늘의 영단어 화면으로 이동
    }


}