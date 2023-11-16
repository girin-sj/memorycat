package com.example.memorycat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
        val bookmarkDatas = mutableListOf<String>()
        for (i in 1..10){
            bookmarkDatas.add("Item $i")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = BookmarkAdapter(bookmarkDatas)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

        //if (uid != null) {
        //    fetchBookmarkedWords(uid)
        //}


    /*
    private fun fetchBookmarkedWords(uid: String) {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userBookmarkDB = firestore.collection("userDB").document(uid)
            .collection("bookmarkDB")

        userBookmarkDB
            .whereEqualTo("isBookmarked", true)
            .get()
            .addOnSuccessListener { documents ->
                val bookmarkedWords = mutableListOf<String>()
                for (document in documents) {
                    val word = document.getString("word")
                    word?.let {
                        bookmarkedWords.add(it)
                    }
                }
                setupRecyclerView(bookmarkedWords)
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    private fun setupRecyclerView(bookmarkedWords: MutableList<String>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = BookmarkAdapter(bookmarkedWords)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
    }

     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}