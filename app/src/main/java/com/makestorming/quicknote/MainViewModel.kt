package com.makestorming.quicknote

import android.app.Application
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var userKey : ObservableField<String> = ObservableField()
    var list : ObservableArrayList<MemoListData> = ObservableArrayList()
    var position : MutableLiveData<Int> = MutableLiveData(0)
    var email : ObservableField<String> = ObservableField()
    var uid : ObservableField<String> = ObservableField()
    var verified : ObservableBoolean = ObservableBoolean() //로그인 성공여부를 알려주는 변수
    var deleteMode : ObservableBoolean = ObservableBoolean(false) //로그인 성공여부를 알려주는 변수
    var listNum : ObservableInt = ObservableInt(0)
    private var selected: MutableLiveData<MemoListData> = MutableLiveData()
    private var mAdapter: MemoListAdapter

    init {
        mAdapter = MemoListAdapter(list, this)
    }

    fun getAdapter() : MemoListAdapter{
        return mAdapter
    }

    fun getSelected(): MutableLiveData<MemoListData>{
        return selected
    }

    fun addItem(memoListData: MemoListData) {
        list.add(listNum.get(), memoListData)
        listNum.set(listNum.get() + 1)
        mAdapter.notifyDataSetChanged()
    }

    fun changeItem(memoListData: MemoListData) { //position 정의 해야함.
        list[position.value!!].let {
            it.key = memoListData.key
            it.title = memoListData.title
            it.text = memoListData.text
            it.date = memoListData.date
        }
        mAdapter.notifyDataSetChanged()
    }

    fun removeItem(memoListData: MemoListData) {
        list.remove(MemoListData(memoListData.key, memoListData.date, memoListData.title, memoListData.text))
        mAdapter.notifyDataSetChanged()
    }

    fun sortItem(isReverse: Boolean){
        list.sortWith(Comparator { t1, t2 ->
            val date: Long = t1.date
            val date1: Long = t2.date
            if (isReverse) {
                date.compareTo(date1)
            } else {
                date1.compareTo(date)
            }
        })
        mAdapter.notifyDataSetChanged()
    }

    fun onItemClick(memoListData: MemoListData){
        list.add(memoListData)
    }

    fun auth(){

    }

    fun getAction(item: MemoListData?, position: Int) {
        //리스트의 아이템을 클릭했을때만 이 메소드가 실행되게 하면됨.
        //삭제 모드라면 다른 메소드를 클릭하도록 유도함.
        this.position.value = position
        selected.value = item
    }


}
