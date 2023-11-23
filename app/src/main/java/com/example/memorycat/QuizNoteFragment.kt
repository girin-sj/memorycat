package com.example.memorycat

import MyAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorycat.databinding.FragmentQuizNoteBinding

class QuizNoteFragment : Fragment() {
    private var _binding: FragmentQuizNoteBinding? = null
    private val binding get() = _binding!!

    private val popupWindowManager = Popup(requireContext()) // Initialize your popup manager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datas = mutableListOf<String>()
        for (i in 1..10) {
            datas.add("Item $i")
        }

        val adapter = MyAdapter(datas)
        /*adapter.onItemClick = { position ->
            // Handle the item click
            val itemView = binding.noterecycler.findViewHolderForAdapterPosition(position)?.itemView
            itemView?.let {
                popupWindowManager.showPopup(it)
            }
        }

         */

        binding.noterecycler.layoutManager = LinearLayoutManager(context)
        binding.noterecycler.adapter = adapter
        binding.noterecycler.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}