package com.example.memorycat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.memorycat.ViewModel.TodayWordViewModel
import com.example.memorycat.databinding.FragmentTodaywordStartBinding

lateinit var getFirstTenWords: MutableList<String>
//lateinit var getThreeMeanings: MutableList<String>

class TodayWordStartFragment : Fragment() {
    private var _binding: FragmentTodaywordStartBinding? = null
    private val binding get() = _binding!!
    private val todayWordViewModel: TodayWordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaywordStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayWordViewModel.level.observe(viewLifecycleOwner, { level ->
            binding.levelText.text = "${level?.uppercase()} 학습하기"
        })

        // 오늘의 영단어 학습 시작
        binding.studyStartButton.setOnClickListener {
            todayWordViewModel.makeTodayWordList() //리스트 만들기
            Log.d("com.example.memorycat.ViewModel.TodayWordViewModel", "list생성")
            todayWordViewModel.todayWordNames.observe(viewLifecycleOwner) { todayWordNames ->
                todayWordNames?.let {
                    getFirstTenWords = todayWordNames.take(10).toMutableList() //이건 study에서도 사용함.
                    Log.d("com.example.memorycat.ViewModel.TodayWordViewModel", "todayWordNames: $todayWordNames")
                    //첫 단어
                    val word = getFirstTenWords[0]
                    Log.d("com.example.memorycat.ViewModel.TodayWordViewModel", "idx: ${0}, 단어: ${word}")

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, TodayWordStudyFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
                // 틀렸던 단어들 재학습하기는 우선 pass
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}