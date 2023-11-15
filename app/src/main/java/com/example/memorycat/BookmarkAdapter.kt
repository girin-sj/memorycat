package com.example.memorycat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.databinding.ItemBookmarkBinding

class BookmarkViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root)

class BookmarkAdapter(val bookmarkDatas: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //어답터 클래스는 RecyclerView.Adapter<RecyclerView.ViewHolder>를 상속받고,
    //onCreateViewHolder, onBindViewHolder, getItemCount를 각각 오버라이딩
    //뷰 사용 강제.어떤 어답터 사용할지 말해줘야 함.

    //datas안에 들어있는거 -> var wordList: MutableList<Word>, var inflater: LayoutInflater

    //단어 누르면 뜨는거
    //binding.root.setOnClickListener {
    //    Toast.makeText(binding.root.context, "단어: ${nthWord} 뜻: ${nthMean}",
    //        Toast.LENGTH_SHORT)
    //        .show()
    //}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        BookmarkViewHolder(
            ItemBookmarkBinding.inflate(
                LayoutInflater.from(parent.context), parent,false)
        )
    //뷰가 만들어질때(create) 호출되는 메소드
    //위에서 만든 각 행 레이아웃(R.layout.item_recycler_view / xml)를 인플레이트하여 뷰 홀더를 생성
    //뷰 홀더 리턴. itemView 파라미터로 받음.

    override fun getItemCount(): Int = bookmarkDatas.size
    //전체 리스트의 사이즈, 데이터 크기

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //뷰가 바인드(Bind)될때 호출되는 메소드
        //onCreateViewHolder에서 뷰가 만들어졌다면, onBindViewHolder는 뷰에 내용이 씌위짐
        //리사이클러뷰가 한번 만들어지면, 각 뷰는 한번만 create되지만, 삭제되지 않고 계속 재활용(recycle)됨
        //-> onCreateViewHolder는 리사이클러뷰가 만들어질때만 호출되지만,
        //onBindViewHolder는 스크롤을 내리거나 올릴때마다 호출됨
        val binding = (holder as MyViewHolder).binding

    }
}