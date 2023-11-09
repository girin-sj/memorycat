package com.example.memorycat

//activity_recycler_view.xml에서
//tools:listitem="@layout/item_bookmark"
//윗줄로 해당 아이템으로 리사이클러뷰 볼 수 있음.
//tools 속성은 xml에서만 보일 뿐, 실제 앱 동작 상에서 영향 끼치지 않음.

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookmarkMainFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_main)

        //북마크한 내용들 가져와서 여기서 보여줘야 함
        val wordList = mutableListOf<Word>()
        for (i in 0..100) {
            wordList.add(Word("" + i + "번째 단어", "" + i + "번째 뜻"))
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        //val boardAdapter = BoardAdapter(itemList)
        //boardAdapter.notifyDataSetChanged() - 어댑터와 리사이클러뷰 갱신하는 건데 필요 없나

        // 리사이클러뷰에 어답터 장착
        recyclerView.adapter = RecyclerViewAdapter(wordList, LayoutInflater.from(this))
        // 리사이클러뷰에 레이아웃 매니저 설정
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        recyclerView.layoutManager = GridLayoutManager(this, 3)
    }
}

class RecyclerViewAdapter(
    //어답터 클래스는 RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>를 상속받고,
    //onCreateViewHolder, onBindViewHolder, getItemCount를 각각 오버라이딩
    //뷰 사용 강제.어떤 어답터 사용할지 말해줘야 함.
    // outer class
    var wordList: MutableList<Word>,
    var inflater: LayoutInflater
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // inner class
        //어댑터 클래서 외부 혹은 내부에 RecyclerView.ViewHolder클래스를 상속하여 만듦
        // 아이템 뷰의 상세 뷰 컴포넌트를 홀드한다(정해놓음)

        //val wordImage: ImageView - 이미지 사용할꺼면 추가하자
        val nthWord: TextView
        val nthMean: TextView

        init { //데이터 채워줄 뷰 컴포넌트(item_bookmark.xml)
            //각 뷰들을 itemView.findViewById를 사용하여 해당 뷰를 연결
            //wordImage = itemView.findViewById(R.id.wordImage) - 이미지 사용하면 추가하자
            nthWord = itemView.findViewById<TextView>(R.id.nthWord)
            nthMean = itemView.findViewById<TextView>(R.id.nthMean)
            //아래 이건 뭐지
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                val car = wordList.get(position)
                Log.d("test", car.nthWord)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //뷰가 만들어질때(create) 호출되는 메소드
        //위에서 만든 각 행 레이아웃(R.layout.item_recycler_view / xml)를 인플레이트하여 뷰 홀더를 생성
        val view = inflater.inflate(R.layout.item_bookmark, parent, false)
        return ViewHolder(view)
        //뷰 홀더 리턴. itemView 파라미터로 받음.
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //뷰가 바인드(Bind)될때 호출되는 메소드
        //onCreateViewHolder에서 뷰가 만들어졌다면, onBindViewHolder는 뷰에 내용이 씌위짐
        //리사이클러뷰가 한번 만들어지면, 각 뷰는 한번만 create되지만, 삭제되지 않고 계속 재활용(recycle)됨
        //-> onCreateViewHolder는 리사이클러뷰가 만들어질때만 호출되지만,
        //onBindViewHolder는 스크롤을 내리거나 올릴때마다 호출됨

        //= 데이터를 아이템뷰의 뷰컴포넌트와 묶음(뷰를 채워줌)
        holder.nthWord.text = wordList.get(position).nthWord
        holder.nthMean.text = wordList.get(position).nthMean
    }

    override fun getItemCount(): Int { //listView의 getCount()와 유사
        return wordList.size
        //전체 리스트의 사이즈, 데이터 크기 리턴
    }
}

class Word(val nthWord: String, val nthMean: String)