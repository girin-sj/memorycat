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
    private var correctCounter: Int = 1
    private var tts: MemoryCatTextToSpeech? = null
    private val quizViewModel: QuizViewModel by viewModels()
    private var correctAnswer: String? = null
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
            if (++counter < 11) {
                binding.quizNumber.text = "$counter/10"
                handleAnswer(binding.quizWord.text.toString()) //정답 데이터 DB에 넣기

            } else {
                binding.quizPassButton.text = "결과 확인하기"
                binding.quizPassButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)

                binding.quizPassButton.setOnClickListener {
                    handleAnswer(binding.quizWord.text.toString()) //정답 데이터 DB에 넣기
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
        Log.d("QuizMainFragment", "meanings: $meanings") //확인용1
        correctAnswer = meanings.random()
        // 앞에서 3개의 뜻만 가져오기
        val selectedMeanings = randomMeanings!!.take(3)
        // 정답 뜻 추가
        val finalMeanings = selectedMeanings + correctAnswer
        // 리스트 섞기
        val finalShuffledMeanings = finalMeanings.shuffled()
        Log.d("QuizMainFragment", "$finalShuffledMeanings") //2

        // 버튼에 뜻 할당
        binding.quizAnswer1.text = finalShuffledMeanings[0]
        Log.d("QuizMainFragment", "means1: ${finalShuffledMeanings[0]}")
        binding.quizAnswer2.text = finalShuffledMeanings[1]
        binding.quizAnswer3.text = finalShuffledMeanings[2]
        binding.quizAnswer4.text = finalShuffledMeanings[3]
    }

    private fun updateChoices(word: String) { //여기가 문제네
        quizViewModel.getRandomMeanings() //나는 이거 필요 없음
        quizViewModel.getMeanings(word).removeObserver(meaningsObserver)
        quizViewModel.getMeanings(word).observe(viewLifecycleOwner, meaningsObserver)
    }

    //이건 북마크 때문에 사용해야 함
    private fun handleAnswer(word: String) {
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

        quizViewModel.getRandomWord()
    }

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