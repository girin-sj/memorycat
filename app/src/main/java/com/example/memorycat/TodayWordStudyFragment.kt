package com.example.memorycat

import MemoryCatTextToSpeech
import TodayWordViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.memorycat.databinding.FragmentTodaywordStudyBinding


//오늘의 영단어.
//오늘 공부할 10개의 단어 전부 list에 저장되어 있는 상태고, index로 단어 하나하나 불러다 쓰면 됨. 이전 단어도 인덱스 사용! 의미도 함수 이용해서 가져오면 됨.
class TodayWordStudyFragment : Fragment() {
    private var _binding: FragmentTodaywordStudyBinding? = null
    private val binding get() = _binding!!
    private var counter: Int = 1

    private var tts: MemoryCatTextToSpeech? = null
    private val todayWordViewModel: TodayWordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaywordStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todayWordViewModel.getTodayWord(counter-1) //이렇게 iewModel에서 가져오기

        tts = MemoryCatTextToSpeech(requireContext())
        binding.todaywordvoiceButton.setOnClickListener { startTTS() }

        todayWordViewModel.todayWord.observe(viewLifecycleOwner, Observer { newWord ->
            binding.TodayWord.text = newWord //이게 바로 들어가네
        })

        todayWordViewModel.means1.observe(viewLifecycleOwner, Observer { newMean1 ->
            binding.TodayWordMean1.text = newMean1
        })

        todayWordViewModel.means2.observe(viewLifecycleOwner, Observer { newMean2 ->
            binding.TodayWordMean2.text = newMean2
        })

        todayWordViewModel.means3.observe(viewLifecycleOwner, Observer { newMean3 ->
            binding.TodayWordMean3.text = newMean3
        })


        var word : String

        //버튼 눌러서 다음, 이전 단어로 바뀔 때마다 북마크 정보도 해당 단어에 맞게 가야함.

        //다음 단어로
        binding.studyNextButton.setOnClickListener {
            if (counter <= 9) {
                counter++
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyBeforeButton.text = "이전 단어로"

                word = todayWordViewModel.getTodayWord(counter-1) //단어 가져오기
                todayWordViewModel.getMeanings(word)
                //북마크 가져오기 추가
            }
            if (counter == 10) { //마지막 단어
                binding.studyNextButton.text = "학습 끝내기"
                //binding.studyNextButton.backgroundTintList =
                //    ContextCompat.getColorStateList(requireContext(), R.color.yellow) //배경색 바꾸기(필요 없긴 함)

                //학습 끝내기
                binding.studyNextButton.setOnClickListener {
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, TodayWordEndFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
        }
        //var previousWord: String? = null // 이전에 표시한 단어를 저장하는 변수
        //이전 단어로
        binding.studyBeforeButton.setOnClickListener {
            //버튼 누를 때마다 해당 단어의 북마크 정보 가져와서 색 반영해야 함.
            if (counter <= 1) {
                binding.studyBeforeButton.text = "이전단어 없음"
                binding.TodayWordNumber.text = "$counter/10"
            } else {
                counter--
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyNextButton.text = "다음 단어로"

                // 이전 단어 정보 가져오기
                word = todayWordViewModel.getTodayWord(counter-1) //단어 가져오기
                todayWordViewModel.getMeanings(word)
                //북마크 내용 가져오기 추가
            }
        }
    }//onViewCreated()



    private fun startTTS() {
        tts!!.speakWord(binding.TodayWord.text.toString())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
