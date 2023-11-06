package com.example.memorycat
//package com.example.fastcampus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecyclerViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        //북마크한 내용들 가져와서 여기서 보여줘야 함
        val wordList = mutableListOf<Word>()
        for (i in 0..100) {
            wordList.add(Word("" + i + "번째 단어", "" + i + "번째 뜻"))
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // 리사이클러뷰에 어답터 장착
        recyclerView.adapter = RecyclerViewAdapter(wordList, LayoutInflater.from(this))
        // 리사이클러뷰에 레이아웃 매니저 장착
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
//        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        recyclerView.layoutManager = GridLayoutManager(this, 3)
    }
}

class RecyclerViewAdapter(
    //뷰 사용 강제.어떤 어답터 사용할지 말해줘야 함.
    // outer class
    var wordList: MutableList<Word>,
    var inflater: LayoutInflater
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // inner class
        // 아이템 뷰의 상세 뷰 컴포넌트를 홀드한다(정해놓음)
        val wordImage: ImageView
        val nthWord: TextView
        val nthMean: TextView

        init { //데이터 채워줄 뷰 컴포넌트(car_item.xml)
            wordImage = itemView.findViewById(R.id.carImage)
            nthWord = itemView.findViewById(R.id.nthCar)
            nthMean = itemView.findViewById(R.id.nthEngine)
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                val car = wordList.get(position)
                Log.d("testt", car.nthCar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 아이템뷰를 리턴
        val view = inflater.inflate(R.layout.word_item, parent, false)
        return ViewHolder(view)
        //뷰 홀더 리턴. itemView 파라미터로 받음. xml inflate하고 뷰 홀더에게 넣어줌.
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 데이터를 아이템뷰의 뷰컴포넌트와 묶는다(뷰를 채워준다)
        holder.nthWord.text = wordList.get(position).nthCar
        holder.nthMean.text = wordList.get(position).nthMean
    }

    override fun getItemCount(): Int { //listView의 getCount()와 유사
        return wordList.size
        //전체 리스트의 사이즈, 데이터 크기
    }
}

class Word(val nthCar: String, val nthMean: String)