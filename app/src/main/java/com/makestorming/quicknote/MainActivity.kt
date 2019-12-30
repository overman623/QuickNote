package com.makestorming.quicknote

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.makestorming.quicknote.config.FileManager
import com.makestorming.quicknote.config.PermissionsChecker
import com.makestorming.quicknote.database.User
import com.makestorming.quicknote.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

/*

//구글 인증이 끝났으면, 구글 인증정보를 데이터 베이스에 저장한다.

*/

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val tag : String = MainActivity::class.java.simpleName
    private val permissions : Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val model : MainViewModel = MainViewModel()

    private val mAdapter = TextListAdapter(model.list, object : TextListAdapter.Callback{
        override fun getAction(item : TextListData?, index : Int) {
            model.index.set(index)
            openWriteActivity(item)
        }
    })

    private lateinit var database: DatabaseReference// ...
    private lateinit var auth: FirebaseAuth// ...
    lateinit var googleSignInClient : GoogleSignInClient //구글 로그인을 관리하는 클래스
    private lateinit var listener : ValueEventListener
    private var memoListener : ChildEventListener = object : ChildEventListener{

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        var num = 0

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            p0.let{
                val memoKey = it.key.toString()
                val title = it.child("title").value.toString()
                val text = it.child("text").value.toString()
                val date = it.child("date").value as Long
                model.list.add(
                    num++, TextListData(memoKey, date, title, text)
                )
                mAdapter.notifyDataSetChanged()
            }

        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }

    }

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
        }//파일 쓰기 권한은 없앨 예정임.

        textViewList.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        auth = FirebaseAuth.getInstance()
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build().let {
                googleSignInClient = GoogleSignIn.getClient(this, it)
            }
        buttonGoogle.setOnClickListener(this)
        fab.setOnClickListener(this)

        database = FirebaseDatabase.getInstance().reference

    }

    public override fun onStart() { //유저의 정보가 없다면 다이얼로그 창을 띄워서 로그인을 유도한다.
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        setUserInfo()
    }

    private fun signOut(){
        auth.signOut()
        googleSignInClient.signOut()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show()
            model.email.set(null)
            model.uid.set(null)
            model.verified.set(false)
        }
    }

    override fun onBackPressed() {
        if(mAdapter.deleteMode){
            mAdapter.deleteMode = false
            fab.setImageResource(android.R.drawable.ic_menu_edit)
            fab.setOnClickListener(this)
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(model.verified.get()){
            menuInflater.inflate(R.menu.menu_main, menu)
        }
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
            R.id.action_sign_out -> {
                signOut()
                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteMemo() {
        fab.setImageResource(android.R.drawable.ic_menu_delete)
        fab.setOnClickListener {
            mAdapter.setData.forEach {
                FileManager(it.title).deleteFile()
                model.list.remove(it)
            }
            mAdapter.notifyDataSetChanged()
            mAdapter.setData.clear()
            Toast.makeText(this@MainActivity, R.string.text_delete, Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
        mAdapter.setData.clear()
        mAdapter.deleteMode = true
    }

    private fun sortMemoDate(isReverse : Boolean) {
        model.list.sortWith(Comparator { t1, t2 ->
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
                putExtra("KEY", it.key)
                putExtra("TITLE", it.title)
                putExtra("DATE", it.date)
                putExtra("TEXT", it.text)
            }
        }, MEMO)
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

    private fun loadMemos(dataSnapshot : DataSnapshot){
        var num = 0
        model.list.clear()
        dataSnapshot.child("memo").let{
            it.children.forEach { child ->
                val memoKey = child.key.toString()
                val title = child.child("title").value.toString()
                val text = child.child("text").value.toString()
                val date = child.child("date").value as Long
                model.list.add(
                    num++, TextListData(memoKey, date, title, text)
                )
            }
            mAdapter.notifyDataSetChanged()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == MEMO) {

            val query = database.orderByChild("user/${model.userKey.get().toString()}/memo")

//                .child(model.userKey.get().toString())
//                .orderByChild("memo")

            if (resultCode == MEMO_RENAME) {//rename 작성 보류
//                model.list.removeAt(model.index.get())

                data?.let{
                    it.getStringExtra("TITLE_NEW")
                    it.getStringExtra("TITLE_BEFORE")
                    model.list[model.index.get()].title
                }

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })


                //데이터베이스 데이터 갱신
                val memo = TextListData(
                    data?.getStringExtra("KEY")!!,
                    data.getLongExtra("FILE_MAKE_DATE", 0),
                    data.getStringExtra("FILE_NAME")!!,
                    data.getStringExtra("FILE_READ_LINE")!!
                    ) //메모 갱신
//                database.child("user").child(model.order.toString()).child("memo").child().setValue(memo)//초기화
//                database.child("user").child(model.order.toString()).child("memo").orderByChild()
            }else{

                //무조건 키값이 없음.
                //데이터베이스 데이터 추가
                //새로운 키를 추가

                val userKey = model.userKey.get()
                val memoKey = database.child("user/${model.userKey.get().toString()}/memo").push().key
                Log.d(tag, "userKey : " + userKey)
                Log.d(tag, "memoKey : " + memoKey)
                val date = System.currentTimeMillis()
                val title = data?.getStringExtra("TITLE_NEW")
                val text = data?.getStringExtra("TEXT")
                if (memoKey == null) {
                    Log.w(tag, "Couldn't get push key for posts")
                    return
                }
                val memo = TextListData(memoKey, date, title!!, text!!) //신규 유저 추가
                val memoValues = memo.toMap()
                val childUpdates = HashMap<String, Any>()
                childUpdates["/user/$userKey/memo/$memoKey"] = memoValues
                database.updateChildren(childUpdates)

            }
            mAdapter.notifyDataSetChanged()
        }else if(requestCode == 100){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try { // 구글 로그인 성공
                val account = task.getResult(ApiException::class.java)
                fireBaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun fireBaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) { // 로그인 성공
                    Toast.makeText(
                        this@MainActivity,
                        "로그인 성공",
                        Toast.LENGTH_SHORT
                    ).show()
                    setUserInfo()

                } else { // 로그인 실패
                    Toast.makeText(this@MainActivity, "로그인 실패", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun setUserInfo(){
        val currentUser = auth.currentUser
        if (currentUser != null) {
            model.email.set(currentUser.email)
            model.uid.set(currentUser.uid)
            model.verified.set(currentUser.isEmailVerified)
            listener = object : ValueEventListener { //메모쪽만 실행해서 반복하면 된다.
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.child("user").let {

                        if(!model.userKey.get().isNullOrEmpty()) return

                        it.children.forEach {now ->
                            if(currentUser.uid == now.child("uid").value){
                                model.userKey.set(now.key)
                                database.child("user").child(now.key!!).child("memo").addChildEventListener(memoListener)
//                                loadMemos(now, now.key)
                                return
                            }
                        }

                        val key = database.child("user").push().key
                        if (key == null) {
                            Log.w(tag, "Couldn't get push key for posts")
                            return
                        }
                        model.userKey.set(key)
                        database.child("user").child(key).child("memo").addChildEventListener(memoListener)
                        val user = User(currentUser.email, currentUser.uid) //신규 유저 추가
                        val userValues = user.toMap()
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/user/$key"] = userValues
                        database.updateChildren(childUpdates)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(tag, "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.orderByChild("user").addListenerForSingleValueEvent(listener)
//            database.orderByChild("user").addChildEventListener(listener2)
        }
        invalidateOptionsMenu()
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.buttonGoogle -> {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent,100)
            }
            R.id.fab -> {
                openWriteActivity(null)
//                writeNewUser("test", "123456")
            }
        }
    }


}
