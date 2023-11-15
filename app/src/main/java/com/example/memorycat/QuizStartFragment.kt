package com.example.memorycat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.memorycat.databinding.FragmentQuizStartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val uid : String? = FirebaseAuth.getInstance().currentUser?.uid
        val userDB = firestore.collection("userDB").document(uid!!)
        userDB.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val level = document.getString("level")
                    binding.levelText.text = "${level?.toUpperCase()} 단어 테스트를\n시작할게요"
                }
                else {
                    Log.d("QuizStartFragment", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizStartFragment", "Error getting document: $exception")
            }

        binding.quizStartButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_content, QuizMainFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
