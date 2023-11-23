package com.example.memorycat

import MemoryCatTextToSpeech
import QuizViewModel
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
import com.example.memorycat.databinding.FragmentQuizMainBinding

class QuizMainFragment : Fragment() {
    private var _binding: FragmentQuizMainBinding? = null
    private val binding get() = _binding!!
    private var counter: Int = 1
    private var tts: MemoryCatTextToSpeech? = null
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = MemoryCatTextToSpeech(requireContext())
        binding.voiceButton.setOnClickListener { startTTS() }

        quizViewModel.randomWord.observe(viewLifecycleOwner, Observer { newWord ->
            binding.quizWord.text = newWord
            updateChoices(newWord)
        })

        binding.quizAnswer1.setOnClickListener {
            handleAnswer(binding.quizAnswer1.text.toString())
        }
        binding.quizAnswer2.setOnClickListener {
            handleAnswer(binding.quizAnswer2.text.toString())
        }
        binding.quizAnswer3.setOnClickListener {
            handleAnswer(binding.quizAnswer3.text.toString())
        }
        binding.quizAnswer4.setOnClickListener {
            handleAnswer(binding.quizAnswer4.text.toString())
        }

        quizViewModel.getRandomWord()
    }

    private fun updateChoices(word: String) {
        quizViewModel.getMeanings(word).observe(viewLifecycleOwner, Observer { meanings ->
            val randomMeanings = quizViewModel.getRandomMeanings()
            // 정답 뜻 추가
            randomMeanings.add(meanings.random())
            // 리스트 섞기
            randomMeanings.shuffle()
            // 버튼에 뜻 할당
            binding.quizAnswer1.text = randomMeanings[0]
            binding.quizAnswer2.text = randomMeanings[1]
            binding.quizAnswer3.text = randomMeanings[2]
            binding.quizAnswer4.text = randomMeanings[3]
        })
    }

    private fun handleAnswer(answer: String) {
        if (quizViewModel.checkAnswer(answer)) {
            // 정답 처리 데이터 전달
            Log.d("QuizMainFragment", "Correct Answer!")
            Toast.makeText(context, "정답입니다!", Toast.LENGTH_SHORT).show()
        } else {
            // 오답 처리 데이터 전달
            Log.d("QuizMainFragment", "Incorrect Answer!")

            // toast 메시지 표시
            Toast.makeText(context, "오답입니다!", Toast.LENGTH_SHORT).show()
        }

        quizViewModel.updateQuizResult(answer)

        if (++counter < 10) {
            quizViewModel.getRandomWord()
        } else {
            binding.quizPassButton.text = "결과 확인하기"
            binding.quizPassButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.yellow)
            binding.quizPassButton.setOnClickListener {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.main_content, QuizNoteFragment())
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }
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