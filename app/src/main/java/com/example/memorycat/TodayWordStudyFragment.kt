package com.example.memorycat

import MemoryCatTextToSpeech
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.memorycat.ViewModel.BookmarkViewModel
import com.example.memorycat.ViewModel.TodayWordViewModel
import com.example.memorycat.databinding.FragmentTodaywordStudyBinding

class TodayWordStudyFragment : Fragment() {
    private var _binding: FragmentTodaywordStudyBinding? = null
    private val binding get() = _binding!!
    private var counter: Int = 0
    private val observer = Observer<String> { newWord -> //화면 내용 변경 될 때마다 observer 호출됨.
        if (newWord != binding.TodayWord.text) {
            binding.TodayWord.text = newWord
            updateMeanings(newWord)
        }
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

        todayWordViewModel.todayWord.observe(viewLifecycleOwner, observer)

        tts = MemoryCatTextToSpeech(requireContext())
        binding.todaywordvoiceButton.setOnClickListener { startTTS() }

        // 버튼 눌러서 다음, 이전 단어로 바뀔 때마다 북마크 정보도 해당 단어에 맞게 가야함.

        // 배열 만들기, 다음 단어로
        binding.studyNextButton.setOnClickListener {
            if(counter == 0){
                counter++
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyNextButton.text = "다음 단어로"

                todayWordViewModel.makeTodayWordList() //리스트 만들기
                todayWordViewModel.todayWordNames.observe(viewLifecycleOwner) { todayWordNames ->
                    todayWordNames?.let {
                        todayWordViewModel.getTodayWord(counter - 1)
                    }
                }

                binding.studyBeforeButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
            }
            else if(counter <= 10) {
                counter++
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyBeforeButton.text = "이전 단어로"
                todayWordViewModel.getTodayWord(counter - 1)

                binding.studyNextButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                binding.studyBeforeButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                // 북마크 가져오기 추가 -> db데이터 변경, 색 변화

                if (counter == 10) { // 종료
                    binding.studyNextButton.text = "학습 끝내기"
                    todayWordViewModel.getTodayWord(counter - 1)
                    binding.studyNextButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.peowpink)

                    binding.studyNextButton.setOnClickListener {
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        transaction?.replace(R.id.main_content, TodayWordEndFragment())
                        transaction?.addToBackStack(null)
                        transaction?.commit()
                    }

                }
            }
        }

        // 이전 단어로
        binding.studyBeforeButton.setOnClickListener {
            if (counter <= 1) {
                binding.studyBeforeButton.text = "이전단어 없음"
                binding.TodayWordNumber.text = "$counter/10"

                binding.studyBeforeButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.graylight)

            } else {
                counter--
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyNextButton.text = "다음 단어로"
                todayWordViewModel.getTodayWord(counter - 1)

                binding.studyNextButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                binding.studyBeforeButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                // 북마크 내용 가져오기 추가 -> db데이터 변경, 색 변화
            }
        }
    }

    //뷰 모델에서 getMeanings()하고 여기로 옴
    private val meaningsObserver = Observer<List<String>> { meanings -> //getMeanings(word)에서 반환한 뜻들이 여기로 들어옴
        //앞에서 3개의 뜻만 가져오기
        binding.TodayWordMean1.text = meanings[1]
        binding.TodayWordMean2.text = meanings[2]
        binding.TodayWordMean3.text = meanings[3]
    }

    private fun updateMeanings(word: String) {
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