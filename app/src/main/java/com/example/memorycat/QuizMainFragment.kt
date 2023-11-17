package com.example.memorycat

import MemoryCatTextToSpeech
import MyViewModel
import android.os.Bundle
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
    private val myViewModel: MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tts = MemoryCatTextToSpeech(requireContext())
        myViewModel.words.observe(viewLifecycleOwner, Observer { newWord ->
            binding.quizWord.text = newWord
        })
        binding.voiceButton.setOnClickListener { startTTS() }

        binding.quizNextButton.setOnClickListener {
            if (counter < 10) {
                counter++
                binding.quizNumber.text = "$counter/10"
                myViewModel.getWords()
            }

            if (counter == 10) {
                binding.quizPassButton.text = "결과 확인하기"
                binding.quizPassButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                binding.quizPassButton.setOnClickListener {
                    val transaction =
                        activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, QuizResultFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
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
