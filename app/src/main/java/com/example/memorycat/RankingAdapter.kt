import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.User
import com.example.memorycat.databinding.ListRankingBinding

class RankingAdapter(private var data: MutableList<User>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    class ViewHolder(val binding: ListRankingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = data[position]
        holder.binding.txtLevel.text = user.level
        holder.binding.txtName.text = user.name
        holder.binding.txtScore.text = user.score.toString()
    }
}