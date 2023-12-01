package com.example.memorycat

import QuizViewModel
import RankingAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.memorycat.databinding.FragmentRankingBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date

class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    private val rankingViewModel: RankingViewModel by viewModels() //뷰모델
    private val mypageViewModel: MypageViewModel by viewModels() //뷰모델
    private val quizViewModel: QuizViewModel by viewModels() //뷰모델
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recyclerView add Item
        val users = mutableListOf<User>()
        for (i in 1..10) {
            val user = User("${mypageViewModel.name.value}", "Lv. ${quizViewModel.level.value}", "Score ${rankingViewModel.correctCount.value}/10", "${i}등")
            users.add(user)
        }

        binding.recRanking.layoutManager = LinearLayoutManager(context)
        binding.recRanking.adapter = RankingAdapter(users)
    }
}