package com.example.memorycat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memorycat.databinding.ItemBookmarkBinding

class BookmarkViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root)

class BookmarkAdapter(val bookmarkDatas: MutableList<String>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //어답터 클래스는 RecyclerView.Adapter<RecyclerView.ViewHolder>를 상속받고,
    //onCreateViewHolder, onBindViewHolder, getItemCount를 각각 오버라이딩
    //뷰 사용 강제.어떤 어답터 사용할지 말해줘야 함.

    //단어 누르면 뜨는거
    //binding.root.setOnClickListener {
    //    Toast.makeText(binding.root.context, "단어: ${} 뜻: ${}",
    //        Toast.LENGTH_SHORT)
    //        .show()
    //}

    override fun getItemCount(): Int = bookmarkDatas.size
    //전체 리스트의 사이즈, 데이터 크기

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        BookmarkViewHolder(ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    //뷰가 만들어질때(create) 호출되는 메소드
    //ItemBookmarkBinding을 사용하여 각 아이템의 바인딩을 생성
    //onBindViewHolder에서는 해당 바인딩을 통해 데이터를 설정

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as BookmarkViewHolder).binding
        // Set word to the corresponding view
        //여기서 MVVM으로 북마크된 단어들을 표현하려나..?
        //binding.wordData1.text = bookmarkedWords[position]
    }
    //뷰가 바인드(Bind)될때 호출되는 메소드
    //onCreateViewHolder에서 뷰가 만들어졌다면, onBindViewHolder는 뷰에 내용이 씌위짐
    //리사이클러뷰가 한번 만들어지면, 각 뷰는 한번만 create되지만, 삭제되지 않고 계속 재활용(recycle)됨
    //-> onCreateViewHolder는 리사이클러뷰가 만들어질때만 호출되지만,
    //onBindViewHolder는 스크롤을 내리거나 올릴때마다 호출됨
}
