package com.example.memorycat

import android.content.Intent
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

        //이동할 프레그먼트 화면 만들고, replace 해당 화면으로 이동시키자. 그러면 manifest에서 activity 자워도 화면 이동 가능함.
        //프레그먼트끼리 이동 확인됐을떄 마지막으로 activity 화면 제거하자.
        binding.studyStartButton.setOnClickListener {
            val intent = Intent(activity, TodayWordStudyActivity::class.java)
            startActivity(intent)
        }
    }
}