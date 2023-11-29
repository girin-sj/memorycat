package com.example.memorycat

import BookmarkViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorycat.databinding.FragmentBookmarkMainBinding

class BookmarkMainFragment : Fragment() {
    private var _binding: FragmentBookmarkMainBinding? = null
    private val binding get() = _binding!!
    //둘다 사용
    private val BookmarkViewModel: BookmarkViewModel by viewModels()
    //private val TodayWordViewModel: TodayWordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BookmarkAdapter(emptyList())

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        BookmarkViewModel.bookmarkResult.observe(viewLifecycleOwner, Observer { bookmarkResult ->
            adapter.bookmarkUpdateData(bookmarkResult)
        })
    }

    //북마크 자체에도 발바닥 누르면 db에게 false 정보 주고, 리스트에서 사라지게.

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

