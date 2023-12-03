package com.example.memorycat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.memorycat.ViewModel.QuizViewModel
import com.example.memorycat.databinding.FragmentQuizStartBinding

class QuizStartFragment : Fragment() {
    private var _binding: FragmentQuizStartBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quizViewModel.level?.observe(viewLifecycleOwner, { level ->
            binding.levelText.text = "${level?.toUpperCase()} 테스트를\n 시작할게요"
        })

        binding.quizStartButton.setOnClickListener {
            //quizViewModel.resetNoteResult()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, QuizMainFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        binding.goTodaywordButton.setOnClickListener {
            //quizViewModel.resetNoteResult()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, TodayWordStartFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        binding.quizNoteButton.setOnClickListener {
            //quizViewModel.resetNoteResult()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, QuizNoteFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
