package com.example.memorycat

import android.os.Bundle
import com.example.memorycat.ViewModel.QuizViewModel
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.memorycat.databinding.FragmentQuizResultBinding

class QuizResultFragment : Fragment() {
    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizViewModel.level?.observe(viewLifecycleOwner, { level ->
            binding.levelText.text = "${level?.toUpperCase()} 레벨 통과!"
        })

        binding.quizNextButton.setOnClickListener {
            quizViewModel.updateLevel()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, TodayWordStartFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        binding.quizAgainButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, QuizStartFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        binding.quizRankingButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, RankingFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
