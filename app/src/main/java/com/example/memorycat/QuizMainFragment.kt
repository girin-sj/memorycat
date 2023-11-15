package com.example.memorycat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.memorycat.databinding.FragmentQuizMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class QuizMainFragment : Fragment() {
    private var _binding: FragmentQuizMainBinding? = null
    private val binding get() = _binding!!
    private var counter: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchRandomQuizWord()

        binding.quizNextButton.setOnClickListener {
            if (counter < 10) {
                counter++
                binding.quizNumber.text = "$counter/10"
                fetchRandomQuizWord()
            }

            if (counter == 10) {
                binding.quizPassButton.text = "결과 확인하기"
                binding.quizPassButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                binding.quizPassButton.setOnClickListener {
                    val transaction =
                        activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, QuizResultFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
        }
    }

    private fun fetchRandomQuizWord() {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
        val userDB = firestore.collection("userDB").document(uid!!)
        val usedFieldNames = mutableListOf<String>() // 중복 방지, 오답노트 리사이클러뷰를 위한 리스트

        userDB.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val level = document.getString("level")
                    val englishDictionaryCollection =
                        firestore.collection("englishDictionary").document(level!!)
                    englishDictionaryCollection.get()
                        .addOnSuccessListener { dictionaryDocument ->
                            if (dictionaryDocument != null) {
                                val fieldMap = dictionaryDocument.data
                                if (fieldMap != null) {
                                    val fieldNames = fieldMap.keys.toList() // Map 형식의 단어 data field의 key
                                    val availableFieldNames = fieldNames - usedFieldNames // 중복 방지

                                    if (availableFieldNames.isNotEmpty()) {
                                        val randomFieldName = availableFieldNames.random() // 단어 랜덤 추출
                                        usedFieldNames.add(randomFieldName)
                                        binding.quizWord.text = randomFieldName
                                    }
                                }
                            }
                        }
                } else {
                    Log.d("QuizMainFragment", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizMainFragment", "Error getting document: $exception")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
