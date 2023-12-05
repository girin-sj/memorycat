package com.example.memorycat

import com.example.memorycat.ViewModel.QuizViewModel
import RankingAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorycat.ViewModel.MypageViewModel
import com.example.memorycat.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {
    private lateinit var adapter: RankingAdapter
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private val rankingViewModel: RankingViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        adapter = RankingAdapter(this)
        binding.recRanking.layoutManager = LinearLayoutManager(context)
        binding.recRanking.adapter = adapter
        binding.recRanking.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rankingViewModel.getUidDocument().observe(viewLifecycleOwner, Observer { userResults ->
            adapter.updateUser(userResults.toMutableList())
        })
    }
}