import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.databinding.ItemNoteBinding

class MyViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val datas: MutableList<String>) :
    RecyclerView.Adapter<MyViewHolder>() {

    // var onItemClick: ((Int) -> Unit)? = null

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(
            ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding
        binding.wordData.text = datas[position]

        /*
        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position)
        }
         */
    }
}