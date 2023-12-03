package com.example.memorycat

import QuizNoteAdapter
import com.example.memorycat.ViewModel.QuizViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorycat.databinding.FragmentQuizNoteBinding

class QuizNoteFragment : Fragment() {
    private var _binding: FragmentQuizNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: QuizNoteAdapter
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizNoteBinding.inflate(inflater, container, false)

        adapter = QuizNoteAdapter(this)
        binding.noterecycler.layoutManager = LinearLayoutManager(context)
        binding.noterecycler.adapter = adapter
        binding.noterecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizViewModel.loadNoteResult().observe(viewLifecycleOwner, Observer { noteResults ->
            adapter.updateNote(noteResults.toMutableList())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
