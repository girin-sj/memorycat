package com.example.memorycat

import MemoryCatTextToSpeech
import MyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.memorycat.databinding.FragmentTodaywordStudyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


//오늘의 영단어. 7개 기본 + 3개 랜덤(이전꺼에서) - 망각곡선
class TodayWordStudyFragment : Fragment() {
    private var _binding: FragmentTodaywordStudyBinding? = null
    private val binding get() = _binding!!
    var counter: Int = 1

    private var tts: MemoryCatTextToSpeech? = null
    private val myViewModel: MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaywordStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    //버튼 누르기 -> 화면 전환
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.getTodayWord() //이렇게 MyViewModel에서 가져오기

        tts = MemoryCatTextToSpeech(requireContext())
        binding.todaywordvoiceButton.setOnClickListener { startTTS() }

        myViewModel.words.observe(viewLifecycleOwner, Observer { newWord ->
            binding.TodayWord.text = newWord //이게 바로 들어가네
        })

        myViewModel.means1.observe(viewLifecycleOwner, Observer { newMean1 ->
            binding.TodayWordMean1.text = newMean1
        })

        myViewModel.means2.observe(viewLifecycleOwner, Observer { newMean2 ->
            binding.TodayWordMean2.text = newMean2
        })

        myViewModel.means3.observe(viewLifecycleOwner, Observer { newMean3 ->
            binding.TodayWordMean3.text = newMean3
        })


        //버튼 눌러서 다음, 이전 단어로 바뀔 때마다 북마크 정보도 해당 단어에 맞게 가야함.

        //다음 단어로
        binding.studyNextButton.setOnClickListener {
            if (counter <= 9) {
                counter++
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyBeforeButton.text = "이전 단어로"
                myViewModel.getTodayWord() //단어 가져오기
                //뜻, 북마크 따로 가져와야하나
            }
            if (counter == 10) { //마지막 단어
                binding.studyNextButton.text = "학습 끝내기"
                //binding.studyNextButton.backgroundTintList =
                //    ContextCompat.getColorStateList(requireContext(), R.color.yellow) //배경색 바꾸기(필요 없긴 함)

                //학습 끝내기
                binding.studyNextButton.setOnClickListener {
                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, TodayWordEndFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
        }
        var previousWord: String? = null // 이전에 표시한 단어를 저장하는 변수
        //이전 단어로
        binding.studyBeforeButton.setOnClickListener {
            //버튼 누를 때마다 해당 단어의 북마크 정보 가져와서 색 반영해야 함.
            if (counter <= 1) {
                binding.studyBeforeButton.text = "이전단어 없음"
                binding.TodayWordNumber.text = "$counter/10"
            } else {
                counter--
                binding.TodayWordNumber.text = "$counter/10"
                binding.studyNextButton.text = "다음 단어로"

                // 이전 단어 정보 가져오기
                if (previousWord != null) { //이전 정보들 가져와야함.
                    binding.TodayWord.text = previousWord
                    // 이전 단어의 뜻 가져오기
                    myViewModel.getPreviousTodayWord(previousWord!!)

                }
                //else {
                    // 이전 단어 정보가 없으면 새로운 단어 가져오기
                    // 이러는 거 아니다..
                //    fetchRandomTodayWord()
                //}
            }
        }
    }//onViewCreated()

    //------------------------------------------------------------------------------
    //여기부터 MyViewModel로

    // 이전 단어의 뜻 가져오기
    private fun getPreviousTodayWord(word: String) {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
        val userDB = firestore.collection("userDB").document(uid!!)
        val englishDictionaryCollection =
            firestore.collection("englishDictionary").document(word) // 단어를 문서 ID로 사용

        englishDictionaryCollection.get()
            .addOnSuccessListener { dictionaryDocument ->
                if (dictionaryDocument != null) {
                    val meaningsMap = dictionaryDocument.data as? Map<String, String>
                    if (meaningsMap != null && meaningsMap.isNotEmpty()) {
                        binding.TodayWordMean1.text = meaningsMap["mean1"] ?: ""
                        binding.TodayWordMean2.text = meaningsMap["mean2"] ?: ""
                        binding.TodayWordMean3.text = meaningsMap["mean3"] ?: ""
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TodayWordStudyFragment", "Error getting document: $exception")
            }
    }

    // 오늘의 영단어에서 발바닥 누르면 북마크 색 바뀌기
    private fun bookmarkChangeColor(){
        binding.todaywordBookmarkButton.setOnClickListener {
            // 현재 색상 가져오기. 그냥 db에 모든 단어 false로 지정해놓고 시작해야 할 것 같은데
            val currentColor = binding.todaywordBookmarkButton.imageTintList?.defaultColor

            // 현재 색상이 @color/graydark인 경우에만 변경
            if (currentColor == ContextCompat.getColor(binding.root.context, R.color.graydark)) {
                binding.todaywordBookmarkButton.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.peowpink)
                )
            } else {
                binding.todaywordBookmarkButton.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.graydark)
                )
            }
        }
    }



    //----------------------------------------------------------------------------------------

    private fun startTTS() {
        tts!!.speakWord(binding.TodayWord.text.toString())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/*
    //DB에서 단어, 뜻, 북마크 가져오기
    private fun getTodayWord() {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
        val userDB = firestore.collection("userDB").document(uid!!)
        val usedFieldNames = mutableListOf<String>() // 중복 방지를 위한 리스트. 3개 랜덤 돌릴때만 필요할듯

        userDB.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val level = document.getString("level")
                    val englishDictionaryCollection =
                        firestore.collection("englishDictionary").document(level!!)
                    englishDictionaryCollection.get()
                        .addOnSuccessListener { dictionaryDocument ->
                            if (dictionaryDocument != null) {
                                val fieldMap = dictionaryDocument.data //문서의 데이터. map 형태
                                if (fieldMap != null) {
                                    // 단어 가져오기
                                    val fieldNames = fieldMap.keys.toList()  //필드의 key
                                    val availableFieldNames = fieldNames - usedFieldNames // 중복 방지

                                    if (fieldNames.isNotEmpty()) {
                                        val randomFieldName: String

                                        if (usedFieldNames.size < 10) {
                                            // 처음 학습할 때는 1번째부터 10번째까지의 단어 중 하나를 랜덤으로 선택
                                            randomFieldName = availableFieldNames.random()
                                        } else {
                                            // 그 이후 학습에서는 이전에 학습했던 3개의 단어와 그 다음 단어부터 7개의 단어 중 하나를 랜덤으로 선택
                                            val previouslyLearnedWords = usedFieldNames.takeLast(3)
                                            val remainingWords =
                                                availableFieldNames - previouslyLearnedWords
                                            randomFieldName =
                                                (remainingWords + previouslyLearnedWords).random()
                                        }
                                        //available의 인덱스랑 usedFieldNames의 개수 못 정하나?

                                        usedFieldNames.add(randomFieldName)
                                        binding.TodayWord.text = randomFieldName

                                        //해당 단어의 뜻 가져오기 -> DB에 내용 전부 Map, String으로 넣으면 됨.
                                        val meaningsMap = fieldMap[randomFieldName] as? Map<String, String>
                                        if (meaningsMap != null && meaningsMap.isNotEmpty()) {
                                            binding.TodayWordMean1.text = meaningsMap?.get("mean1") ?: ""
                                            binding.TodayWordMean2.text = meaningsMap?.get("mean2") ?: ""
                                            binding.TodayWordMean3.text = meaningsMap?.get("mean3") ?: ""
                                        }

                                        // 단어를 북마크DB에 추가 또는 토글
                                        toggleBookmarkStatus(randomFieldName)

                                        // 현재 단어를 이전 단어로 저장
                                        // 지금은 이전 단어를 하나만 저장을 했는데, list로 전부 전부 저장해야 할듯.
                                        // 처음나왔던 것부터 stack형태로.
                                        previousWord = randomFieldName
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
        bookmarkChangeColor() //버튼 누를때 색 바뀌는 기능 -> 해당 정보 db에 넣어야 함. 해당 함수에서 db불러오자

    } //fetchRandomTodayWord() 단어, 뜻, 북마크 가져오기

    //firestore에서 해당 user의 bookmarkDB에 접근. bookmark db가 바뀌었으니 해당 코드는 수정해야 함.
    //북마크 버튼 눌릴때 false는 true로, true는 false로 바뀜
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
 */