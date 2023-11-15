package com.example.memorycat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorycat.databinding.FragmentBookmarkMainBinding

class BookmarkMainFragment : Fragment() {
    private var _binding: FragmentBookmarkMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val datas = mutableListOf<String>()
        for (i in 1..10){
            datas.add("Item $i")
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = MyAdapter(datas)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// 리사이클러뷰에 어답터 장착
//recyclerView.adapter = RecyclerViewAdapter(wordList, LayoutInflater.from(this))
// 리사이클러뷰에 레이아웃 매니저 설정
//recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
