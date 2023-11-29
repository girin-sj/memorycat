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

//fetchBookmarkedWords 함수는 Firestore에서 북마크된 단어 목록을 가져오는 역할
// 가져온 데이터를 bookmarkedWords 리스트에 저장하고,
// 이 리스트를 BookmarkAdapter에 전달하여 RecyclerView에 표시

//"word"라는 필드로 예시를 들었으니, 실제 Firestore에 저장된 데이터의 구조에 따라 필드 이름을 수정하자

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

        //db에서 북마크 개수 가져와서 이용. user안에 만들어야 하나
        /*
        val bookmarkDatas = mutableListOf<String>()
        for (i in 1..10){
            bookmarkDatas.add("Item $i")
        }

         */

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

