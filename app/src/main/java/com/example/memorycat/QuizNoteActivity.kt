package com.example.memorycat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorycat.databinding.ActivityMainBinding
import com.example.memorycat.databinding.ActivityQuizNoteBinding

class QuizNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityQuizNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val datas = mutableListOf<String>()

        binding.noterecycler.layoutManager = LinearLayoutManager(this)
        binding.noterecycler.adapter = MyAdapter(datas)
        binding.noterecycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }
}