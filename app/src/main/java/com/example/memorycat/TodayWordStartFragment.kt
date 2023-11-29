package com.example.memorycat

import TodayWordViewModel
import android.os.Bundle
import android.util.Log
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

        todayWordViewModel.level.observe(viewLifecycleOwner, { level ->
            binding.levelText.text = "${level?.toUpperCase()} 학습하기"
        }) //성공

        //오늘의 영단어 학습 시작
        binding.studyStartButton.setOnClickListener {
            val wordlist = todayWordViewModel.makeTodayWordList() //배열 생성
            Log.d("TodayWordViewModel", "list 확인: ${wordlist}") //이거 왜 안떠
            //첫 단어 가져오기 추가하자

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