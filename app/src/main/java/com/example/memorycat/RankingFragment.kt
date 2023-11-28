package com.example.memorycat

import RankingAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorycat.databinding.FragmentRankingBinding

class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recyclerView add Item
        val users = mutableListOf<User>()
        for (i in 1..10) {
            users.add(users[i])
        }

        binding.recRanking.layoutManager = LinearLayoutManager(context)
        binding.recRanking.adapter = RankingAdapter(users)
    }
}
