package com.example.memorycat

import MemoryCatTextToSpeech
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.memorycat.ViewModel.QuizViewModel
import com.example.memorycat.databinding.FragmentQuizMainBinding

class QuizMainFragment : Fragment() {
    private var _binding: FragmentQuizMainBinding? = null
    private val binding get() = _binding!!
    private var counter: Int = 1
    private var correctCounter: Int = 0
    private var tts: MemoryCatTextToSpeech? = null
    private val quizViewModel: QuizViewModel by viewModels()
    private var correctAnswer: String? = null
    //observer로 단어가 바뀌는지 관찰하고 UI의 단어, 선택지를 변경
    private val observer = Observer<String> { newWord ->
        binding.quizWord.text = newWord
        updateChoices(newWord)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = MemoryCatTextToSpeech(requireContext())
        binding.voiceButton.setOnClickListener { startTTS() }

        quizViewModel.randomWord.observe(viewLifecycleOwner, observer)

        binding.quizNextButton.setOnClickListener {
            if (++counter < 10) {
                binding.quizNumber.text = "$counter/10"
                handleAnswer(binding.quizWord.text.toString())

            } else {
                binding.quizNumber.text = "$counter/10"
                binding.quizPassButton.text = "결과 확인하기"
                binding.quizPassButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                binding.quizPassButton.setOnClickListener {
                    handleAnswer(binding.quizWord.text.toString())
                    if(correctCounter==10){
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        transaction?.replace(R.id.main_content, QuizResultFragment())
                        transaction?.addToBackStack(null)
                        transaction?.commit()
                    }
                    else{
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        transaction?.replace(R.id.main_content, QuizFailFragment())
                        transaction?.addToBackStack(null)
                        transaction?.commit()
                    }
                }
            }
        }
    }

    private val meaningsObserver = Observer<List<String>> { meanings ->
        val randomMeanings = quizViewModel.randomMeanings.value
        Log.d("QuizMainFragment", "$randomMeanings")
        // 정답 뜻 추가
        correctAnswer = meanings.random()
        // 앞에서 3개의 뜻만 가져오기
        val selectedMeanings = randomMeanings!!.take(3)
        // 정답 뜻 추가
        val finalMeanings = selectedMeanings + correctAnswer
        // 리스트 섞기
        val finalShuffledMeanings = finalMeanings.shuffled()
        Log.d("QuizMainFragment", "$finalShuffledMeanings")

        // 버튼에 뜻 할당
        binding.quizAnswer1.text = finalShuffledMeanings[0]
        binding.quizAnswer2.text = finalShuffledMeanings[1]
        binding.quizAnswer3.text = finalShuffledMeanings[2]
        binding.quizAnswer4.text = finalShuffledMeanings[3]
    }

    private fun updateChoices(word: String) {
        quizViewModel.getRandomMeanings(word)
        // List가 누적되는 걸 막기 위해 초기화 후 재호출
        quizViewModel.getMeanings(word).removeObserver(meaningsObserver)
        quizViewModel.getMeanings(word).observe(viewLifecycleOwner, meaningsObserver)
    }

    private fun handleAnswer(word: String) {
        // 라디오 버튼의 id로 선택지 설정
        val selectedId = binding.answerGroup.checkedRadioButtonId
        var answerId = ""
        when(selectedId) {
            R.id.quizAnswer1 -> {
                Log.d("QuizMainFragment", "answer1")
                answerId = binding.quizAnswer1.text.toString()
            }
            R.id.quizAnswer2 -> {
                Log.d("QuizMainFragment", "answer2")
                answerId = binding.quizAnswer2.text.toString()
            }
            R.id.quizAnswer3 -> {
                Log.d("QuizMainFragment", "answer3")
                answerId = binding.quizAnswer3.text.toString()
            }
            R.id.quizAnswer4 -> {
                Log.d("QuizMainFragment", "answer4")
                answerId = binding.quizAnswer4.text.toString()
            }
        }

        if (quizViewModel.checkAnswer(answerId, correctAnswer!!)) {
            // 정답 처리 데이터 전달
            Log.d("QuizMainFragment", "Correct Answer!")
            Toast.makeText(context, "정답입니다!", Toast.LENGTH_SHORT).show()

            quizViewModel.updateQuizResult(word, "O")
            quizViewModel.updateNoteResult(word, answerId, correctAnswer!!, "O")
            correctCounter++
        } else {
            // 오답 처리 데이터 전달
            Log.d("QuizMainFragment", "Incorrect Answer!")
            Toast.makeText(context, "오답입니다!", Toast.LENGTH_SHORT).show()

            quizViewModel.updateQuizResult(word, "X")
            quizViewModel.updateNoteResult(word, answerId, correctAnswer!!, "X")
        }
        // 새로운 단어
        quizViewModel.getCurrentRandomWord()
    }

    //text to speech
    private fun startTTS() {
        tts!!.speakWord(binding.quizWord.text.toString())
    }

    override fun onDestroyView() {
        if (tts != null) {
            tts!!.stopWord()
        }
        super.onDestroyView()
        _binding = null
    }
}