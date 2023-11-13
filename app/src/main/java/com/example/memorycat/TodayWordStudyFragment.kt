package com.example.memorycat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memorycat.databinding.FragmentTodaywordStudyBinding

class TodayWordStudyFragment : Fragment() {
    private var _binding: FragmentTodaywordStudyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaywordStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var counter: Int = 1

        //다음 단어
        binding.studyNextButton.setOnClickListener {
            if (counter == 9) {
                binding.studyNextButton.text = "학습 끝내기"
                counter++
                binding.TodayWordNumber.text = "$counter/10"
            } else if (counter > 10) {
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.main_content, TodayWordEndFragment())
                transaction?.addToBackStack(null)
                transaction?.commit()
            } else {
                counter++
                binding.TodayWordNumber.text = "$counter/10"
            }
        }

        //이전 단어
        binding.studyBeforeButton.setOnClickListener {
            if (counter <= 1) {
                binding.studyBeforeButton.text = "이전단어 없음"
                binding.TodayWordNumber.text = "$counter/10"
            } else {
                counter--
                binding.studyBeforeButton.text = "$counter/10"
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}