package com.example.memorycat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.databinding.ItemNoteBinding

class MyViewHolder(val binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas: MutableList<String>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = datas.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
    }
}