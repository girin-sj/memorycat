
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.BookmarkResult
import com.example.memorycat.databinding.ItemBookmarkBinding

class BookmarkAdapter(
    private val fragment: BookmarkMainFragment,
    private val bookmarkClickListener: (String) -> Unit,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
    private var tts: MemoryCatTextToSpeech? = null
    init {
        tts = MemoryCatTextToSpeech(fragment.requireContext())
    }

    class ViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root) {
        val peow: ImageButton = binding.bookmarkPeow
        val voice: ImageButton = binding.bookmarkVoice
        val word: TextView = binding.bookmarkWordData
        val mean1: TextView = binding.boomkarkMean1
        val mean2: TextView = binding.boomkarkMean2
        val mean3: TextView = binding.boomkarkMean3
    }

    private var bookmarkList = mutableListOf<BookmarkResult>()

    fun updateBookmark(newData: MutableList<BookmarkResult>) {
        bookmarkList = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookmarkList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmarkResult = bookmarkList[position]
        holder.word.text = bookmarkResult.word
        holder.mean1.text = bookmarkResult.mean1
        holder.mean2.text = bookmarkResult.mean2
        holder.mean3.text = bookmarkResult.mean3

        holder.voice.setOnClickListener {
            tts?.speakWord(holder.word.text.toString())
        }

        holder.peow.setOnClickListener {
            bookmarkClickListener(bookmarkResult.word)
        }

        holder.itemView.setOnClickListener {
            itemClickListener(bookmarkResult.word)
        }
    }
}
