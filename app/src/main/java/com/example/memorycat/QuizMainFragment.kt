package com.example.memorycat

import MemoryCatTextToSpeech
import QuizViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizViewModel.resetCurrentQuizDB()
        tts = MemoryCatTextToSpeech(requireContext())
        binding.voiceButton.setOnClickListener { startTTS() }
        quizViewModel.words.observe(viewLifecycleOwner, Observer { newWord ->
            binding.quizWord.text = newWord
        })
        quizViewModel.getWords()

        /* binding.quizNextButton.setOnClickListener {
            if (counter < 10) {
                counter++
                binding.quizNumber.text = "$counter/10"
                val userAnswer = binding.quizAnswer.text.toString()
                if (quizViewModel.checkAnswer(userAnswer)) {
                    // Handle correct answer (e.g., update UI, show a message)
                    Log.d("QuizMainFragment", "Correct Answer!")
                } else {
                    // Handle incorrect answer (e.g., update UI, show a message)
                    Log.d("QuizMainFragment", "Incorrect Answer!")
                }

                // Update quiz result in the database
                quizViewModel.updateQuizResult(userAnswer)
                quizViewModel.getWords()
            }
            if (counter == 10) {
                binding.quizPassButton.text = "결과 확인하기"
                binding.quizPassButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                binding.quizPassButton.setOnClickListener {
                    val transaction =
                        activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, QuizNoteFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
            binding.quizAnswer.text.clear()
        }

         */
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
