package com.example.memorycat

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.memorycat.ViewModel.QuizViewModel
import com.example.memorycat.ViewModel.TodayWordViewModel
import com.example.memorycat.databinding.FragmentQuizNoteDialogBinding

class QuizNoteDialogFragment(private val quizResult: QuizResult) : DialogFragment() {
    private var _binding: FragmentQuizNoteDialogBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizNoteDialogBinding.inflate(inflater, container, false)
        binding.wordData.text = quizResult.word
        binding.selectMean.text = quizResult.select
        binding.answerMean.text = quizResult.answer
        quizViewModel.getNoteMeanings(quizResult.word).observe(viewLifecycleOwner, Observer { meanings ->
            binding.TodayWordMean1.text = meanings[1]
            binding.TodayWordMean2.text = meanings[2]
            binding.TodayWordMean3.text = meanings[3]
        })

        if (quizResult.isCorrect == "O") {
            binding.selectMean.setTextColor(ContextCompat.getColor(binding.selectMean.context, R.color.rightgreen))
        } else if (quizResult.isCorrect == "X") {
            binding.selectMean.setTextColor(ContextCompat.getColor(binding.selectMean.context, R.color.wrongred))
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val view = View.inflate(requireContext(), R.layout.fragment_quiz_note_dialog, null)
        dialog.setContentView(view)

        return dialog
    }

}