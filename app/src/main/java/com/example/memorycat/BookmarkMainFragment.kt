
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorycat.ViewModel.TodayWordViewModel
import com.example.memorycat.databinding.FragmentBookmarkMainBinding

class BookmarkMainFragment : Fragment() {
    private var _binding: FragmentBookmarkMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookmarkAdapter
    private val todayWordViewModel: TodayWordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkMainBinding.inflate(inflater, container, false)

        adapter = BookmarkAdapter(
            fragment = this,
            bookmarkClickListener = { word -> //북마크 버튼
                todayWordViewModel.removeBookmark(word) //recycler view에서 삭제
                todayWordViewModel.loadSelectedBookmarks { bookmarkResults -> //삭제 후의 북마크 목록을 다시 불러와서 RecyclerView를 업데이트
                //콜백 함수의 제어권을 넘겨받음 -> 콜백 함수 호출 시점에 대한 제어권 가짐
                    adapter.updateBookmark(bookmarkResults.toMutableList()) //북마크 db 업데이트
                }
            },
            itemClickListener = { word -> //아이템 자체
                Toast.makeText(context, "${word}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.bookmarkrecycler.layoutManager = LinearLayoutManager(context)
        binding.bookmarkrecycler.adapter = adapter
        //binding.bookmarkrecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        return binding.root
    }
    //뷰의 생성이 완료되면 호출됨
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //초기 북마크 목록을 가져와 RecyclerView 업데이트
        todayWordViewModel.loadSelectedBookmarks { bookmarkResults ->
            adapter.updateBookmark(bookmarkResults.toMutableList())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}