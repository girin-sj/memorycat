import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.RankingFragment
import com.example.memorycat.User
import com.example.memorycat.databinding.ListRankingBinding

class RankingAdapter(private val context: RankingFragment) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    class ViewHolder(val binding: ListRankingBinding) : RecyclerView.ViewHolder(binding.root)
    private var userList = mutableListOf<User>()

    fun updateUser(newData: MutableList<User>) {
        userList = newData
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.txtLevel.text = user.level
        holder.binding.txtName.text = user.name
        holder.binding.txtScore.text = user.score
        holder.binding.textRankingGrade.text = user.grade
    }
}