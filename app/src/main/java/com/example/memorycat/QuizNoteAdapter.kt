import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.QuizResult
import com.example.memorycat.R
import com.example.memorycat.databinding.ItemNoteBinding

class QuizNoteHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

class QuizNoteAdapter(private var data: List<QuizResult>) : RecyclerView.Adapter<QuizNoteAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newData: List<QuizResult>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quizResult = data[position]
        holder.binding.wordData.text = quizResult.word
        holder.binding.answerMean.text = quizResult.answer
        holder.binding.selectMean.text = quizResult.select
        if (quizResult.isCorrect) {
            holder.binding.selectMean.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.rightgreen))
        } else {
            holder.binding.selectMean.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.wrongred))
        }
    }
}