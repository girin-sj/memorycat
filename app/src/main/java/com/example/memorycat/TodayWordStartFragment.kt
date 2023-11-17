package com.example.memorycat

//import android.os.Bundle
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memorycat.databinding.FragmentTodaywordStartBinding

class TodayWordStartFragment : Fragment() {
    private var _binding: FragmentTodaywordStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodaywordStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //오늘의 영단어 학습 시작
        binding.studyStartButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, TodayWordStudyFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        //틀렸던 단어들 재학습하기는 우선 pass
    }
}