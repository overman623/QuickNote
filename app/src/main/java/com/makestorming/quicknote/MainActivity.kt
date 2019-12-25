package com.makestorming.quicknote

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.makestorming.quicknote.config.FileManager
import com.makestorming.quicknote.config.PermissionsChecker
import com.makestorming.quicknote.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.util.*


/*


- 매번 모든 파일들을 다시 불러오는 동작이 비효율 적이기 때문에 livedata를 차용하기로 함.


1. 처음 실행될때만 파일의 목록을 불러와서 그 목록을 livedata에 저장함.
2. 새로 파일이 추가된다면, 추가된 파일 이름을 livedata에 추가.
3. 파일이 삭제된다면, livedata에서 해당하는 파일을 삭제한다.
4. 파일이 변경된다면, livedata에서 해당하는 파일을 변경한다.


*/

class MainActivity : AppCompatActivity() {
    private val tag : String = MainActivity::class.java.simpleName
    private val permissions : Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1
    private val PICK_CONTACT_REQUEST = 1
    private val textItems : MutableList<TextListData> = mutableListOf()
    private val model : MainViewModel2 = MainViewModel2()

//    private val mAdapter = TextListAdapter(textItems, object : TextListAdapter.Callback{
    private val mAdapter = TextListAdapter(model.list, object : TextListAdapter.Callback{
        override fun getAction(item : TextListData?) {
            openWriteActivity(item)
        }
    })

/*
    private val mAdapter = TextListAdapter(textItems, object : TextListAdapter.Callback{
        override fun getAction(item : TextListData?) {
            openWriteActivity(item)
        }
    })
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = model
        binding.lifecycleOwner = this

        setSupportActionBar(toolbar)

        PermissionsChecker(this).apply {
            if(lacksPermissions(permissions)){
                ActivityCompat.requestPermissions(this@MainActivity, permissions, MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            }
        }

        fab.setOnClickListener {
            openWriteActivity(null)
        }

        loadFiles()

//        if(model.textData.size == 0) textCenter.text = getString(R.string.text_center)
//        if(textItems.size == 0) textCenter.text = getString(R.string.text_center)
//        if(textItems.size == 0) textCenter.text = getString(R.string.text_center)

        textViewList.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

    }

    override fun onBackPressed() {
        if(mAdapter.deleteMode){
            mAdapter.deleteMode = false
            fab.setImageResource(android.R.drawable.ic_menu_edit)
            fab.setOnClickListener {
                openWriteActivity(null)
            }
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete_memo -> {
                deleteMemo()
                true
            }
            R.id.action_sort_by_date -> {
                sortMemoDate(false)
                true
            }
            R.id.action_sort_by_date_reverse -> {
                sortMemoDate(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteMemo() {
        fab.setImageResource(android.R.drawable.ic_menu_delete)
        fab.setOnClickListener {
//            mAdapter.setData.forEach {
            mAdapter.setData2.forEach {
                FileManager(it.title).deleteFile()
                model.list.remove(it)
            }
            mAdapter.notifyDataSetChanged()
//            loadFiles() //->삭제된 데이터만 제거하면 저절로 UI에 반영됨.
//            mAdapter.setData.clear()
            mAdapter.setData2.clear()
            Toast.makeText(this@MainActivity, R.string.text_delete, Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
//        mAdapter.setData.clear()
        mAdapter.setData2.clear()
        mAdapter.deleteMode = true
    }

    private fun sortMemoDate(isReverse : Boolean) {
        model.list.sortWith(Comparator { t1, t2 ->
//        textItems.sortWith(Comparator { t1, t2 ->
//        model.textData.value?.sortWith(Comparator { t1, t2 ->
            val date: Long = t1.date
            val date1: Long = t2.date
            if(isReverse){
                date.compareTo(date1)
            }else{
                date1.compareTo(date)
            }
        })
        mAdapter.notifyDataSetChanged()
    }

    fun openWriteActivity(item : TextListData?) {
        val intent = Intent(this, MemoActivity::class.java)
        startActivityForResult(intent.apply {
            item?.let {
                putExtra("TITLE", it.title)
                putExtra("DATE", it.date)
                putExtra("ORDER", it.order)
            }
        }, PICK_CONTACT_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                            Manifest.permission.READ_CONTACTS)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this@MainActivity,
                            permissions,
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS)

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                    Toast.makeText(this, R.string.text_not_permission, Toast.LENGTH_SHORT).show()
                    finish()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun loadFiles(){

//        model.textData.clear()
//        textItems.clear()



        File(Environment.getDataDirectory().absolutePath +
                "/data/" + packageName + "/memo")
            .apply {
                if(exists()){
                    listFiles()?.forEach {
//                        model.addData(
//                        textItems.add(
                        model.list.add(
                            TextListData(0,
                            it.lastModified(),
                            it.name.replace(".txt", ""),
                            FileManager().readLine(it)))
                    }
                }else
                    mkdir()
            }

        mAdapter.notifyDataSetChanged()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
//            loadFiles()

            data?.let{
                model.list.add(
                    TextListData(0,
                        it.getLongExtra("FILE_MAKE_DATE", 0),
                        it.getStringExtra("FILE_NAME")!!,
                        it.getStringExtra("FILE_READ_LINE")!!))
            }
            mAdapter.notifyDataSetChanged()
//            if (resultCode == Activity.RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Do something with the contact here (bigger example below)
//            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



}
