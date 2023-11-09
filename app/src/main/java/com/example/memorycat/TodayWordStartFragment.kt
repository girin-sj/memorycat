package com.example.memorycat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memorycat.databinding.ActivityTodaywordStartBinding

class TodayWordStartFragment : Fragment() {
    private var _binding: ActivityTodaywordStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityTodaywordStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.studyStartButton.setOnClickListener {
            val intent = Intent(activity, TodayWordStudyActivity::class.java)
            startActivity(intent)
        }
    }
}