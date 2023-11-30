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

lateinit var getFirstTenWords: MutableList<String>
lateinit var getThreeMeanings: MutableList<String>

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
            todayWordViewModel.makeTodayWordList() // LiveData 내용 업데이트 요청
            Log.d("TodayWordViewModel", "list생성") //여기선 잘 나오는데 study에서는 왜 안되지
            todayWordViewModel.todayWordNames.observe(viewLifecycleOwner) { todayWordNames ->
                todayWordNames?.let {
                    // LiveData 내용이 업데이트되면 실행될 코드
                    val firstTenWords = todayWordNames.take(10)
                    getFirstTenWords = todayWordNames.take(10).toMutableList()
                    val word = firstTenWords.getOrElse(0) { "" }
                    todayWordViewModel.getMeanings(word) //null나옴
                    Log.d(
                        "TodayWordViewModel",
                        "list: $getFirstTenWords"
                    )
                    Log.d("TodayWordViewModel", "원소1: ${getFirstTenWords.getOrElse(0) { "" }}")
                    Log.d("TodayWordViewModel", "원소2: ${getFirstTenWords.getOrElse(9) { "" }}")

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, TodayWordStudyFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                    Log.d("TodayWordViewModel", "확인1: $firstTenWords") //여기선 잘 나옴
                }
            }
        }
        // 틀렸던 단어들 재학습하기는 우선 pass
    }

    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayWordViewModel.level.observe(viewLifecycleOwner, { level ->
            binding.levelText.text = "${level?.uppercase()} 학습하기"
        })

        // 오늘의 영단어 학습 시작
        binding.studyStartButton.setOnClickListener {
            todayWordViewModel.makeTodayWordList() // LiveData 내용 업데이트 요청
            Log.d("TodayWordViewModel", "list생성") //여기선 잘 나오는데 study에서는 왜 안되지
            todayWordViewModel.todayWordNames.observe(viewLifecycleOwner, { todayWordNames ->
                todayWordNames?.let {
                    // LiveData 내용이 업데이트되면 실행될 코드
                    val firstTenWords = todayWordNames.take(10)
                    val word = firstTenWords.getOrElse(0) { "" }
                    todayWordViewModel.getMeanings(word) //null나옴
                    Log.d("TodayWordViewModel", "list: $firstTenWords") //여기선 잘 나오는데 study에서는 왜 안되지
                    Log.d("TodayWordViewModel", "원소1: ${firstTenWords.getOrElse(0) { "" }}")
                    Log.d("TodayWordViewModel", "means1: ${todayWordViewModel.means1.value}")
                    Log.d("TodayWordViewModel", "원소2: ${firstTenWords.getOrElse(9) { "" }}")

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, TodayWordStudyFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                    Log.d("TodayWordViewModel", "확인1: $firstTenWords") //여기선 잘 나옴
                }
            })
        }
        // 틀렸던 단어들 재학습하기는 우선 pass
    }
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
