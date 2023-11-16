package com.example.memorycat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.memorycat.databinding.FragmentTodaywordStudyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

//오늘의 영단어. 7개 기본 + 3개 랜덤(이전꺼에서) - 망각곡선
class TodayWordStudyFragment : Fragment() {
    private var _binding: FragmentTodaywordStudyBinding? = null
    private val binding get() = _binding!!
    var counter: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaywordStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchRandomTodayWord()

        //다음 단어
        binding.studyNextButton.setOnClickListener {
            if (counter <= 9) {
                counter++
                binding.TodayWordNumber.text = "$counter/10"
                fetchRandomTodayWord()
            }
            if (counter == 10) {
                binding.studyNextButton.text = "학습 끝내기"
                binding.studyNextButton.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.yellow)

                binding.studyNextButton.setOnClickListener {
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, TodayWordEndFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
        }

        //이전 단어
        binding.studyBeforeButton.setOnClickListener {
            //버튼 누를 때마다 해당 단어의 북마크 정보 가져와서 색 반영해야 함.
            if (counter <= 1) {
                binding.studyBeforeButton.text = "이전단어 없음"
                binding.TodayWordNumber.text = "$counter/10"
            } else {
                counter--
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyBeforeButton.text = "이전 단어로"
            }
        }

        // 오늘의 영단어에서 발바닥 누르면 북마크 색 바뀌기
        binding.todaywordBookmark.setOnClickListener {
            // 현재 색상 가져오기
            val currentColor = binding.todaywordBookmark.imageTintList?.defaultColor

            // 현재 색상이 @color/graydark인 경우에만 변경
            if (currentColor == ContextCompat.getColor(binding.root.context, R.color.graydark)) {
                binding.todaywordBookmark.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.peowpink)
                )
            } else {
                binding.todaywordBookmark.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.graydark)
                )
            }
        }

    }

    //DB에서 단어 가져오기.
    private fun fetchRandomTodayWord() {
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
                                    // 단어 가져오기
                                    val fieldNames = fieldMap.keys.toList() // Map 형식의 단어 data field의 key
                                    val availableFieldNames = fieldNames - usedFieldNames // 중복 방지

                                    if (availableFieldNames.isNotEmpty()) {
                                        val randomFieldName = availableFieldNames.random() // 단어 랜덤 추출
                                        usedFieldNames.add(randomFieldName)
                                        binding.TodayWord.text = randomFieldName

                                        // 뜻 가져오기 -> 이게 아직 안됨.
                                        val meaningsArray = fieldMap[randomFieldName] as? List<String>

                                        if (meaningsArray != null && meaningsArray.isNotEmpty()) {
                                            // 단어를 북마크DB에 추가 또는 토글
                                            toggleBookmarkStatus(randomFieldName)

                                            // 뜻을 각각의 변수에 바인딩
                                            binding.TodayWordMean1.text = meaningsArray.getOrNull(0) ?: ""
                                            binding.TodayWordMean2.text = meaningsArray.getOrNull(1) ?: ""
                                            binding.TodayWordMean3.text = meaningsArray.getOrNull(2) ?: ""

                                        }
                                    }
                                }

                            }
                        }
                } else {
                    Log.d("TodayWordStudyFragment", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordStudyFragment", "Error getting document: $exception")
            }
    }

    //firestore에서 해당 user의 bookmarkDB에 접근
    //버튼 눌릴때 false는 true로, true는 false로 바뀜
    //firestore에서 해당 user의 bookmarkDB에 접근
    //버튼 눌릴때 false는 true로, true는 false로 바뀜
    private fun toggleBookmarkStatus(word: String) {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val uid: String? = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            val wordForDocumentId = convertToDocumentId(word)
            val userBookmarkDB = firestore.collection("userDB").document(uid)
                .collection("bookmarkDB").document(wordForDocumentId)

            firestore.runTransaction { transaction ->
                val bookmarkSnapshot = transaction.get(userBookmarkDB)

                if (bookmarkSnapshot.exists()) {
                    // 단어가 이미 존재할 경우 토글
                    val isBookmarked = bookmarkSnapshot.getBoolean("isBookmarked") ?: false
                    transaction.update(userBookmarkDB, "isBookmarked", !isBookmarked)
                } else {
                    // 단어가 존재하지 않을 경우 새로 추가
                    transaction.set(userBookmarkDB, mapOf("word" to word, "isBookmarked" to true))
                }
            }
                .addOnSuccessListener {
                    Log.d("TodayWordStudyFragment", "Bookmark status toggled for word: $word")
                }
                .addOnFailureListener { exception ->
                    Log.e("TodayWordStudyFragment", "Error toggling bookmark status", exception)
                }
        }
    }

    // 단어를 Firebase에서 사용 가능한 문서 ID로 변환하는 함수
    private fun convertToDocumentId(word: String): String {
        return word.lowercase(Locale.ROOT).replace(" ", "_")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}