package com.example.memorycat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memorycat.databinding.FragmentBookmarkStartBinding

class BookmarkStartFragment : Fragment() {
    private var _binding: FragmentBookmarkStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarkStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //프레그먼트끼리의 화면이동으로 변경해야 함. replace 사용하자
        //이미 bookmarkMainActivity는 삭제해서, bookmark start -> main 화면 전환부터 시작하자.
        /*binding.WordMeanButton.setOnClickListener {
            val intent = Intent(activity, BookmarkMainFragment::class.java)
        binding.WordMeanButton.setOnClickListener {
            val intent = Intent(activity, BookmarkMainActivity::class.java)
            startActivity(intent)
        }*/
    }

}