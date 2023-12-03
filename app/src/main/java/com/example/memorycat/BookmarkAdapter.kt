
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.BookmarkMainFragment
import com.example.memorycat.BookmarkResult
import com.example.memorycat.R
import com.example.memorycat.databinding.ItemBookmarkBinding

//MainActivity -> List -> Adapter -> listView_item.xml -> activity_main.xml
// 데이터와 View를 연결짓는 다리 역할
class BookmarkAdapter(private val context: BookmarkMainFragment) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
    private var tts: MemoryCatTextToSpeech? = null
    private val parentContext = context
    init {
        tts = MemoryCatTextToSpeech(context.requireContext())
    }
    class ViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root){
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
        //peow?

        //텍스트 색바꾸기

        if (bookmarkResult.isSelect == "O") { //북마크에 들어간 단어들
            holder.binding.boomkarkMean1.setTextColor(ContextCompat.getColor(holder.binding.boomkarkMean1.context, R.color.rightgreen))
            holder.binding.boomkarkMean2.setTextColor(ContextCompat.getColor(holder.binding.boomkarkMean2.context, R.color.rightgreen))
            holder.binding.boomkarkMean3.setTextColor(ContextCompat.getColor(holder.binding.boomkarkMean3.context, R.color.rightgreen))
        } else if (bookmarkResult.isSelect == "X") {
            holder.binding.boomkarkMean1.setTextColor(ContextCompat.getColor(holder.binding.boomkarkMean1.context, R.color.wrongred))
            holder.binding.boomkarkMean2.setTextColor(ContextCompat.getColor(holder.binding.boomkarkMean2.context, R.color.wrongred))
            holder.binding.boomkarkMean3.setTextColor(ContextCompat.getColor(holder.binding.boomkarkMean3.context, R.color.wrongred))
        }



        holder.voice.setOnClickListener {
            tts?.speakWord(holder.word.text.toString())
        }
    }
}

//어답터 클래스는 RecyclerView.Adapter<RecyclerView.ViewHolder>를 상속받고,
//onCreateViewHolder, onBindViewHolder, getItemCount를 각각 오버라이딩
//뷰 사용 강제.어떤 어답터 사용할지 말해줘야 함.
//class ViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root)

//onBindViewHolder: 뷰가 바인드(Bind)될때 호출되는 메소드
//onCreateViewHolder에서 뷰가 만들어졌다면, onBindViewHolder는 뷰에 내용이 씌위짐
//리사이클러뷰가 한번 만들어지면, 각 뷰는 한번만 create되지만, 삭제되지 않고 계속 재활용(recycle)됨
//-> onCreateViewHolder는 리사이클러뷰가 만들어질때만 호출되지만,
//onBindViewHolder는 스크롤을 내리거나 올릴때마다 호출됨


//단어 누르면 뜨는거
//binding.root.setOnClickListener {
//    Toast.makeText(binding.root.context, "단어: ${} 뜻: ${}",
//        Toast.LENGTH_SHORT)
//        .show()
//}