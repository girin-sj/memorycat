import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.QuizNoteFragment
import com.example.memorycat.R
import com.example.memorycat.QuizResult
import com.example.memorycat.databinding.ItemNoteBinding

class QuizNoteAdapter(private val context: QuizNoteFragment) : RecyclerView.Adapter<QuizNoteAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root){
        val word: TextView = binding.wordData
        val select: TextView = binding.selectMean
        val answer: TextView = binding.answerMean
    }
    private var noteList = mutableListOf<QuizResult>()

    fun updateNote(newData: MutableList<QuizResult>) {
        noteList = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quizResult = noteList[position]
        holder.word.text = quizResult.word
        holder.select.text = quizResult.select
        holder.answer.text = quizResult.answer

        if (quizResult.isCorrect == "O") {
            holder.binding.selectMean.setTextColor(ContextCompat.getColor(holder.binding.selectMean.context, R.color.rightgreen))
        } else if (quizResult.isCorrect == "X") {
            holder.binding.selectMean.setTextColor(ContextCompat.getColor(holder.binding.selectMean.context, R.color.wrongred))
        }
    }
}
