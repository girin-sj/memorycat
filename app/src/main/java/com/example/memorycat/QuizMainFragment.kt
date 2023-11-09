package com.example.memorycat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memorycat.databinding.FragmentQuizMainBinding

class QuizMainFragment : Fragment() {
    private var _binding: FragmentQuizMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var counter: Int = 1

        binding.quizPassButton.setOnClickListener {
            if (counter == 9) {
                binding.quizPassButton.text = "결과 확인하기"
                counter++
                binding.quizNumber.text = "$counter/10"
            } else if (counter > 10) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.main_content, QuizResultFragment())
                transaction?.addToBackStack(null)
                transaction?.commit()
            } else {
                counter++
                binding.quizNumber.text = "$counter/10"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
