package com.example.memorycat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.memorycat.ViewModel.MypageViewModel
import com.example.memorycat.databinding.FragmentEditMypageBinding

class EditMypageFragment : Fragment() {
    private var _binding: FragmentEditMypageBinding? = null
    private val binding get() = _binding!!

    private val mypageViewModel: MypageViewModel by viewModels() //뷰모델
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditMypageBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonChangeName.setOnClickListener {
            val name = binding.textUserName.text.toString() // EditText의 텍스트 값을 가져옴
            mypageViewModel.updateName(name)
        }
        binding.buttonChangeGoal.setOnClickListener {
            val goal = binding.textUserGoal.text.toString()
            mypageViewModel.updateGoal(goal)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
