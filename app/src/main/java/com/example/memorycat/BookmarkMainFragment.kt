import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
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
            bookmarkClickListener = { word ->
                todayWordViewModel.removeBookmark(word)
                todayWordViewModel.loadSelectedBookmarks { bookmarkResults ->
                    adapter.updateBookmark(bookmarkResults.toMutableList())
                }
            },
            itemClickListener = { word ->
                Toast.makeText(context, "${word}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.bookmarkrecycler.layoutManager = LinearLayoutManager(context)
        binding.bookmarkrecycler.adapter = adapter
        binding.bookmarkrecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayWordViewModel.loadSelectedBookmarks { bookmarkResults ->
            adapter.updateBookmark(bookmarkResults.toMutableList())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}