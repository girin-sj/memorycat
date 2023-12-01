package com.example.memorycat

import BookmarkViewModel
import MemoryCatTextToSpeech
import TodayWordViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.memorycat.databinding.FragmentTodaywordStudyBinding

//lateinit var getThreeMeanings: MutableList<String>



//오늘의 영단어.
//오늘 공부할 10개의 단어 전부 list에 저장되어 있는 상태고, index로 단어 하나하나 불러다 쓰면 됨. 이전 단어도 인덱스 사용! 의미도 함수 이용해서 가져오면 됨.
class TodayWordStudyFragment : Fragment() {
    private var _binding: FragmentTodaywordStudyBinding? = null
    private val binding get() = _binding!!
    private var counter: Int = 1
    //-
    private val observer = Observer<String> { newWord ->
        binding.TodayWord.text = newWord
        updateMeanings(newWord)
    }

    private var tts: MemoryCatTextToSpeech? = null
    private val todayWordViewModel: TodayWordViewModel by viewModels()
    private val bookmarkViewModel: BookmarkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaywordStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        todayWordViewModel.todayWord.observe(viewLifecycleOwner, observer) //-

        tts = MemoryCatTextToSpeech(requireContext())
        binding.todaywordvoiceButton.setOnClickListener { startTTS() }

        // 버튼 눌러서 다음, 이전 단어로 바뀔 때마다 북마크 정보도 해당 단어에 맞게 가야함.

        // 다음 단어로
        binding.studyNextButton.setOnClickListener {
            if (counter < 10) {
                counter++
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyBeforeButton.text = "이전 단어로"

                val word = getFirstTenWords[counter - 1]
                Log.d("TodayWordViewModel", "idx: ${counter - 1}, 단어: ${word}")
                todayWordViewModel.getMeanings(word) //제대로 뜻 못 가져오고 있음
                Log.d("TodayWordViewModel", "getMeanings(word)")

                // 북마크 가져오기 추가 -> db데이터 변경, 색 변화
            }

            if (counter == 10) { // 마지막 단어
                binding.studyNextButton.text = "학습 끝내기"
                binding.studyNextButton.setOnClickListener {
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, TodayWordEndFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
        }

        // 이전 단어로
        binding.studyBeforeButton.setOnClickListener {
            if (counter <= 1) {
                binding.studyBeforeButton.text = "이전단어 없음"
                binding.TodayWordNumber.text = "$counter/10"
            } else {
                counter--
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyNextButton.text = "다음 단어로"

                // getTodayWord() 사용
                val word = getFirstTenWords[counter - 1]
                Log.d("TodayWordViewModel", "idx: ${counter - 1}, 단어: ${word}")

                // LiveData를 observe 하는 코드 블록 내에 넣어줌
                if (word != null) {
                    todayWordViewModel.getMeanings(word)
                }

                // 북마크 내용 가져오기 추가
            }
        }
    }

    private val meaningsObserver = Observer<List<String>> { meanings ->
        val meanings = todayWordViewModel.meanings.value
        Log.d("TodayWordViewModel", "means list: $meanings")
        // 앞에서 3개의 뜻만 가져오기
        val threeMeanings = meanings
        Log.d("TodayWordViewModel", "$threeMeanings")

        // 버튼에 뜻 할당 //데이터 없으면 아무것도 안들어가야함
        binding.TodayWordMean1.text = threeMeanings?.get(1) ?: ""
        binding.TodayWordMean2.text = threeMeanings?.get(2) ?: ""
        binding.TodayWordMean3.text = threeMeanings?.get(3) ?: ""
        Log.d("TodayWordViewModel", "means1: ${threeMeanings?.get(1)}")

    }

    private fun updateMeanings(word: String) {
        //todayWordViewModel.getMeanings(word)
        todayWordViewModel.getMeanings(word).removeObserver(meaningsObserver)
        todayWordViewModel.getMeanings(word).observe(viewLifecycleOwner, meaningsObserver)
    }


    //현제 북마크 상태 파악 -> 버튼 눌리면 db 바꾸기 & 색 바꾸기

    private fun startTTS() {
        tts!!.speakWord(binding.TodayWord.text.toString())
    }
    override fun onDestroyView() {
        if (tts != null) {
            tts!!.stopWord()
        }
        super.onDestroyView()
        _binding = null
    }
}