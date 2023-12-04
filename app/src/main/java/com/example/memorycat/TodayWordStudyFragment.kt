package com.example.memorycat

import MemoryCatTextToSpeech
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.memorycat.Repository.Repository_yjw
import com.example.memorycat.ViewModel.TodayWordViewModel
import com.example.memorycat.databinding.FragmentTodaywordStudyBinding

class TodayWordStudyFragment : Fragment() {
    private var _binding: FragmentTodaywordStudyBinding? = null
    private val binding get() = _binding!!
    var word: String = ""
    private var counter: Int = 0
    private var selectCounter: Int = 1 //필요없을지도
    private var tts: MemoryCatTextToSpeech? = null
    private val todayWordViewModel: TodayWordViewModel by viewModels()
    //private val bookmarkViewModel: BookmarkViewModel by viewModels()
    val repo: Repository_yjw = Repository_yjw()

    //단어와 뜻 옵저버
    private val observer = Observer<String> { newWord -> //화면 내용 변경 될 때마다 observer 호출됨.
        if (newWord != binding.TodayWord.text) {
            binding.TodayWord.text = newWord
            updateMeanings(newWord)
            getBookmarkColor(newWord)
        }
    }
    private var isBookmarkClickable = true //추가
    // 북마크 버튼 클릭 리스너
    private val bookmarkClickListener = View.OnClickListener {
        Log.d("TodayWordStudyFragment", "bookmarkClickListener")
        isBookmarkClickable = false // 클릭 중복 방지
        val word = binding.TodayWord.text.toString()
        val mean1 = binding.TodayWordMean1.text.toString()
        val mean2 = binding.TodayWordMean2.text.toString()
        val mean3 = binding.TodayWordMean3.text.toString()

        // db update
        todayWordViewModel.checkSelect(word) { isSelect ->
            Log.d("TodayWordStudyFragment", "isSelect: $isSelect")
            if (isSelect) { //변경된 내용이 checkSelect여기에 적용이 안되나..?
                Toast.makeText(context, "북마크 제거!", Toast.LENGTH_SHORT).show()
                todayWordViewModel.updateBookmarkResult(word, mean1, mean2, mean3, "X")
                Log.d("TodayWordStudyFragment", "updateBookmarkResult: $word: X}")
            } else {
                Toast.makeText(context, "북마크 추가!", Toast.LENGTH_SHORT).show()
                todayWordViewModel.updateBookmarkResult(word, mean1, mean2, mean3, "O")
                Log.d("TodayWordStudyFragment", "updateBookmarkResult: $word: O}")
            }
            // 색상 변경
            changeBookmarkColor(isSelect)
            Log.d("TodayWordStudyFragment", "end changeBookmarkColor") //왜 처음에는 log만 나오고 색은 안바뀌는가
            isBookmarkClickable = true // 클릭 가능하도록 변경
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodaywordStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayWordViewModel.todayWord.observe(viewLifecycleOwner, observer)

        tts = MemoryCatTextToSpeech(requireContext())
        binding.todaywordvoiceButton.setOnClickListener { startTTS() }

        // 버튼 눌러서 다음, 이전 단어로 바뀔 때마다 북마크 정보도 해당 단어에 맞게 가야함.

        // 배열 만들기, 다음 단어로
        binding.studyNextButton.setOnClickListener {
            if (isBookmarkClickable) {
                isBookmarkClickable = false // 클릭 중복 방지
                if(counter == 0){
                    counter++
                    binding.TodayWordNumber.text = "$counter/10"
                    binding.studyNextButton.text = "다음 단어로"

                    todayWordViewModel.makeTodayWordList() //리스트 만들기
                    todayWordViewModel.todayWordNames.observe(viewLifecycleOwner) { todayWordNames ->
                        todayWordNames?.let {
                            word = todayWordViewModel.getTodayWord(counter - 1).toString()
                        }
                    }
                    //getBookmarkColor(word) //색 반영
                    binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)

                    binding.studyBeforeButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                }
                else if(counter < 10) {
                    counter++
                    binding.TodayWordNumber.text = "$counter/10"
                    binding.studyBeforeButton.text = "이전 단어로"
                    word = todayWordViewModel.getTodayWord(counter - 1).toString()

                    //getBookmarkColor(word) //색 반영
                    binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)

                    binding.studyNextButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                    binding.studyBeforeButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                    // 북마크 가져오기 추가 -> db데이터 변경, 색 변화

                    if (counter == 10) {
                        binding.studyNextButton.text = "학습 끝내기"
                        word = todayWordViewModel.getTodayWord(counter - 1).toString()

                        //getBookmarkColor(word) //색 반영
                        binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)

                        binding.studyNextButton.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.peowpink)
                    }
                } else if (counter == 10) { // counter가 10일 때 추가 동작
                    binding.studyNextButton.text = "학습 끝내기"
                    word = todayWordViewModel.getTodayWord(counter - 1).toString()

                    //getBookmarkColor(word) //색 반영
                    binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)

                    binding.studyNextButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.peowpink)

                    val transaction = activity?.supportFragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_content, TodayWordEndFragment())
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
                isBookmarkClickable = true
            }
        }
        // 이전 단어로
        binding.studyBeforeButton.setOnClickListener {
            if (isBookmarkClickable) {
                isBookmarkClickable = false // 클릭 중복 방지
                if (counter <= 1) {
                    binding.studyBeforeButton.text = "이전단어 없음"
                    binding.TodayWordNumber.text = "$counter/10"

                    binding.studyBeforeButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.graylight)

                } else {
                    counter--
                    binding.TodayWordNumber.text = "$counter/10"
                    binding.studyNextButton.text = "다음 단어로"
                    word = todayWordViewModel.getTodayWord(counter - 1).toString()

                    //getBookmarkColor(word) //색 반영
                    binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)

                    binding.studyNextButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                    binding.studyBeforeButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                    // 북마크 내용 가져오기 추가 -> db데이터 변경, 색 변화
                }
                isBookmarkClickable = true
            }
        }
    }

    fun getBookmarkColor(word: String) {
        todayWordViewModel.checkSelect(word) { isSelect -> //null 받아오나
            Log.d("TodayWordStudyFragment", "getBookmarkColor_isSelect: ${isSelect}")

            // 콜백으로 결과를 받아 색상 변경
            if (isSelect) {
                Log.d("TodayWordStudyFragment", "getBookmarkColor: pink")
                binding.todaywordBookmarkButton.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.peowpink)
                )
            } else {
                Log.d("TodayWordStudyFragment", "getBookmarkColor: gray")
                binding.todaywordBookmarkButton.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.graydark)
                )
            }
        }
    }

    fun changeBookmarkColor(isSelect: Boolean) {
        // 현재 색상이 @color/graydark인 경우
        if (isSelect) {
            binding.todaywordBookmarkButton.setColorFilter(
                ContextCompat.getColor(binding.root.context, R.color.graydark)
            )
            Log.d("TodayWordStudyFragment", "changeBookmarkColor: pink to gray")
        } else { // 현재 색상이 @color/peowpink인 경우
            binding.todaywordBookmarkButton.setColorFilter(
                ContextCompat.getColor(binding.root.context, R.color.peowpink)
            )
            Log.d("TodayWordStudyFragment", "changeBookmarkColor: gray to pink")
        }
    }

    private val meaningsObserver = Observer<List<String>> { meanings -> //getMeanings(word)에서 반환한 뜻들이 여기로 들어옴
        //앞에서 3개의 뜻만 가져오기
        binding.TodayWordMean1.text = meanings[1]
        binding.TodayWordMean2.text = meanings[2]
        binding.TodayWordMean3.text = meanings[3]
    }

    private fun updateMeanings(word: String) {
        todayWordViewModel.getMeanings(word).removeObserver(meaningsObserver)
        todayWordViewModel.getMeanings(word).observe(viewLifecycleOwner, meaningsObserver)
    }

    //현제 북마크 상태 파악 -> 버튼 눌리면 db 바꾸기 & 색 바꾸기

    private fun startTTS() {
        tts!!.speakWord(binding.TodayWord.text.toString())
    }
    override fun onDestroyView() {
        if (tts != null) {
            tts!!.stopWord()
        }
        super.onDestroyView()
        _binding = null
    }
}

/*
    private fun handleBookmark(word: String) {
        Log.d("TodayWordStudyFragment", " handleBookmark")
        val mean1 = binding.TodayWordMean1.text.toString()
        val mean2 = binding.TodayWordMean2.text.toString()
        val mean3 = binding.TodayWordMean3.text.toString()

        // checkSelect 호출과 결과 처리
        todayWordViewModel.checkSelect(word) { isSelect ->
            if (isSelect) {
                Toast.makeText(context, "북마크 제거!", Toast.LENGTH_SHORT).show()
                todayWordViewModel.updateBookmarkResult(word, mean1, mean2, mean3, "X")
                Log.d("TodayWordStudyFragment", "updateBookmarkResult: $word: X}")
                selectCounter--
                Log.d("TodayWordStudyFragment", "selectCounter: $selectCounter")
            } else {
                Toast.makeText(context, "북마크 추가!", Toast.LENGTH_SHORT).show()
                todayWordViewModel.updateBookmarkResult(word, mean1, mean2, mean3, "O")
                Log.d("TodayWordStudyFragment", "updateBookmarkResult: $word: O}")
                selectCounter++
                Log.d("TodayWordStudyFragment", "selectCounter: $selectCounter")
            }

            // 북마크 정보 가져오기
            changeBookmarkColor(word, isSelect)
        }
    }
     */