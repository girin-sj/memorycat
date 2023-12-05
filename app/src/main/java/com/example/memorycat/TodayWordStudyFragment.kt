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
import com.example.memorycat.ViewModel.TodayWordViewModel
import com.example.memorycat.databinding.FragmentTodaywordStudyBinding

class TodayWordStudyFragment : Fragment() {
    private var _binding: FragmentTodaywordStudyBinding? = null
    private val binding get() = _binding!!
    var word: String = ""
    private var counter: Int = 0
    private var tts: MemoryCatTextToSpeech? = null
    private val todayWordViewModel: TodayWordViewModel by viewModels()

    //단어, 뜻, 북마크 색 옵저버
    private val observer = Observer<String> { newWord -> //화면 내용 변경 될 때마다 observer 호출됨.
        if (newWord != binding.TodayWord.text) {
            binding.TodayWord.text = newWord
            updateMeanings(newWord)
            getBookmarkColor(newWord)
        }
    }
    private var isBookmarkClickable = true
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
            if (isSelect) {
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
            Log.d("TodayWordStudyFragment", "end changeBookmarkColor")
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

        // 배열 만들기, 다음 단어로
        binding.studyNextButton.setOnClickListener {
            if (isBookmarkClickable) {
                isBookmarkClickable = false // 클릭 중복 방지
                if(counter == 0){
                    counter++
                    binding.TodayWordNumber.text = "$counter/10"
                    binding.studyNextButton.text = "다음 단어로"
                    //리스트 만들기
                    todayWordViewModel.makeTodayWordList()
                    todayWordViewModel.todayWordNames.observe(viewLifecycleOwner) { todayWordNames ->
                        todayWordNames?.let {
                            word = todayWordViewModel.getTodayWord(counter - 1).toString()
                        }
                    }
                    //북마크 db, 색 변경
                    binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)
                    //버튼 색
                    binding.studyBeforeButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                }
                else if(counter < 10) {
                    counter++
                    binding.TodayWordNumber.text = "$counter/10"
                    binding.studyBeforeButton.text = "이전 단어로"
                    word = todayWordViewModel.getTodayWord(counter - 1).toString()

                    binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)

                    binding.studyNextButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                    binding.studyBeforeButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)

                    if (counter == 10) {
                        binding.studyNextButton.text = "학습 끝내기"
                        word = todayWordViewModel.getTodayWord(counter - 1).toString()
                        binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)
                        binding.studyNextButton.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.peowpink)
                    }
                } else if (counter == 10) { //추가 동작
                    binding.studyNextButton.text = "학습 끝내기"
                    word = todayWordViewModel.getTodayWord(counter - 1).toString()
                    binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)
                    binding.studyNextButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.peowpink)
                    //학습 끝내기
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

                    binding.todaywordBookmarkButton.setOnClickListener(bookmarkClickListener)

                    binding.studyNextButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                    binding.studyBeforeButton.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.yellow)
                }
                isBookmarkClickable = true
            }
        }
    }

    fun getBookmarkColor(word: String) { //옵저버에 사용. 새로운 단어 나올때마다 북마크 색 가져오기
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
    fun changeBookmarkColor(isSelect: Boolean) { //북마크 버튼 누를때 북마크 색 변경
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
    //여기까지 북마크

    private val meaningsObserver = Observer<List<String>> { meanings -> //getMeanings(word)에서 반환한 뜻들 바인딩
        binding.TodayWordMean1.text = meanings[1]
        binding.TodayWordMean2.text = meanings[2]
        binding.TodayWordMean3.text = meanings[3]
    }
    private fun updateMeanings(word: String) { //화면에 새로운 뜻 나오도록. 옵저버에 사용
        todayWordViewModel.getMeanings(word).removeObserver(meaningsObserver)
        todayWordViewModel.getMeanings(word).observe(viewLifecycleOwner, meaningsObserver)
    }
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