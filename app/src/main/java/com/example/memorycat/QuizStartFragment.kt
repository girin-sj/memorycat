package com.example.memorycat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.memorycat.databinding.FragmentQuizStartBinding

class QuizStartFragment : Fragment() {
    private var _binding: FragmentQuizStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.quizStartButton.setOnClickListener {
            val intent = Intent(activity, QuizMainActivity::class.java)
            startActivity(intent)
        }
    }
}
