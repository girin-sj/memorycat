package com.example.memorycat

import TodayWordViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.memorycat.databinding.FragmentTodaywordStartBinding

class TodayWordStartFragment : Fragment() {
    private var _binding: FragmentTodaywordStartBinding? = null
    private val binding get() = _binding!!
    private val todayWordViewModel: TodayWordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodaywordStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        todayWordViewModel.level.observe(viewLifecycleOwner, { level ->
            binding.levelText.text = "Rank: ${level.uppercase(Locale.getDefault())}"
        }) //여기부터 막힌다는 거는 뷰모델 자체가 이상한건가?

         */

        //오늘의 영단어 학습 시작
        binding.studyStartButton.setOnClickListener {
            //TodayWordViewModel.makeTodayWordList() //viewModel에서 level이랑 연결해놓음
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, TodayWordStudyFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        //틀렸던 단어들 재학습하기는 우선 pass
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}