package com.example.memorycat

import QuizViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.memorycat.databinding.FragmentQuizStartBinding

class QuizStartFragment : Fragment() {
    private var _binding: FragmentQuizStartBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels() //뷰모델

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quizViewModel.level.observe(viewLifecycleOwner, { level ->
            binding.levelText.text = "${level?.toUpperCase()} 단어 테스트를\n시작할게요"
        })

        binding.quizStartButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, QuizMainFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
