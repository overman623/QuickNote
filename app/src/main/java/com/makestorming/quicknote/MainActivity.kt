package com.makestorming.quicknote

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.makestorming.quicknote.config.DoubleClickPreventListener
import com.makestorming.quicknote.database.User
import com.makestorming.quicknote.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val tag: String = MainActivity::class.java.simpleName
    //    private val model: MainViewModel = MainViewModel()
    private lateinit var model: MainViewModel
//    private lateinit var mAdapter: MemoListAdapter

    private lateinit var database: DatabaseReference // ...
    private lateinit var auth: FirebaseAuth // ...
    lateinit var googleSignInClient: GoogleSignInClient //구글 로그인을 관리하는 클래스
    private lateinit var listener: ValueEventListener
//    var num :Int = 0

    private val memoListener: ChildEventListener = object : ChildEventListener {

        override fun onCancelled(p0: DatabaseError) {
            Log.d(tag, "onCancelled")
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            Log.d(tag, "onChildMoved")
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) { //갱신된 데이터가 온다.

            model.changeItem(
                MemoListData(
                    p0.key.toString(),
                    p0.child("date").value as Long,
                    p0.child("title").value.toString(),
                    p0.child("text").value.toString()
                )
            )

        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {

            Log.d(tag, p0.toString())

            p0.let {
                val memoKey = it.key.toString()
                val title = it.child("title").value.toString()
                val text = it.child("text").value.toString()
                val date = it.child("date").value as Long

//                if (num > model.list.size) num = 0

                model.addItem(MemoListData(memoKey, date, title, text))

//                model.list.add(
//                    num++, MemoListData(memoKey, date, title, text)
//                )
//                model.listNum.set(num)


/*                if (model.list.size == 0) {
                    textCenter.text = getString(R.string.text_new_memo)
                } else {
                    textCenter.text = ""
                }*///databind으로 처리함.
            }

        }

        override fun onChildRemoved(p0: DataSnapshot) {
            model.removeItem(
                MemoListData(
                    p0.key.toString(),
                    p0.child("date").value as Long,
                    p0.child("title").value.toString(),
                    p0.child("text").value.toString()
                )
            )

/*            if (model.list.size == 0) {
                textCenter.text = getString(R.string.text_new_memo)
            } else {
                textCenter.text = ""
            }*///databinding으로 처리함.
        }
    }

    private val clickListener = object : DoubleClickPreventListener() {
        override fun onSingleClick(v: View?) {
            when (v!!.id) {
                R.id.buttonGoogle -> {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, 100)
                }
                R.id.fab -> {
                    openWriteActivity(null)
                }
            }
        }
    }//개별적으로 바인딩해서 처리함.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        model.getSelected().observe(this, Observer {
            openWriteActivity(it)
        })

/*        mAdapter = MemoListAdapter(model.list, object : MemoListAdapter.Callback {
            override fun getAction(item: MemoListData?, index: Int) {
                model.position.set(index)
                openWriteActivity(item)
            }
        })*/

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.vm = model
        binding.lifecycleOwner = this

        setSupportActionBar(toolbar)
//        textViewList.apply {
            //            adapter = mAdapter
//        }
//        num = model.listNum.get()

        auth = FirebaseAuth.getInstance()
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build().let {
                googleSignInClient = GoogleSignIn.getClient(this, it)
            }

        buttonGoogle.setOnClickListener(clickListener) //databinding으로 처리
        fab.setOnClickListener(clickListener) //databinding으로 처리

        database = FirebaseDatabase.getInstance().reference

    }

    fun onClick2(v: View?) {
        var mLastClickTime: Long = 0

        fun inner(){
            val currentClickTime = SystemClock.uptimeMillis()
            val elapsedTime = currentClickTime - mLastClickTime
            mLastClickTime = currentClickTime
            if (elapsedTime <= 1000) {
                return
            }
            Log.d("Test","Clicked")
        }
        inner()
    }



    public override fun onStart() { //유저의 정보가 없다면 다이얼로그 창을 띄워서 로그인을 유도한다.
        super.onStart()
        setUserInfo()
    }

    private fun signOut() {
        textCenter.text = "" //databinding으로 처리 예정.
        database.orderByChild("user").removeEventListener(listener)
        model.userKey.get()?.let {
            database.child("user").child(it).child("memo").removeEventListener(memoListener)
        }
        auth.signOut()
        googleSignInClient.signOut()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, R.string.action_sign_out, Toast.LENGTH_SHORT).show()
            model.email.set(null)
            model.userKey.set(null)
            model.uid.set(null)
//            model.position.set(0)
            model.position.value = 0
            model.listNum.set(0)
            model.list.clear()
            model.verified.set(false)
        }
    }

    override fun onBackPressed() {
        if (model.deleteMode.get()) {
            model.deleteMode.set(false)
//            mAdapter.notifyDataSetChanged()
            fab.setImageResource(android.R.drawable.ic_menu_edit) //verfied 변수를 databinding하여 처리
            fab.setOnClickListener(clickListener)//verfied 변수를 databinding하여 처리
            invalidateOptionsMenu()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (model.verified.get()) {
            if (model.deleteMode.get()) {
                menuInflater.inflate(R.menu.menu_main_delete, menu)
            } else {
                menuInflater.inflate(R.menu.menu_main, menu)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_new_memo -> {
                openWriteActivity(null)
                true
            }
            R.id.action_delete_cancel -> {
                onBackPressed()
                true
            }
            R.id.action_delete_memo -> {
                deleteMemo()
                val snackBar = Snackbar.make(
                    fab, R.string.snackbar_text_delete, Snackbar.LENGTH_LONG
                )
                snackBar.setAction("OK") { snackBar.dismiss() }
                snackBar.show()
                invalidateOptionsMenu()
                true
            }
            R.id.action_sort_by_date -> {
                model.sortItem(false)
                true
            }
            R.id.action_sort_by_date_reverse -> {
                model.sortItem(true)
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
            if (model.getAdapter().setData.size == 0) {
                Toast.makeText(this@MainActivity, R.string.text_delete_none, Toast.LENGTH_SHORT)
                    .show()
            } else {
                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setTitle(R.string.dialog_text_delete_title)
                alertDialog.setMessage(
                    String.format(
                        getString(R.string.dialog_text_delete_message),
                        model.getAdapter().setData.size
                    )
                )
                alertDialog.setPositiveButton(R.string.dialog_text_delete_ok) { _, _ ->
                    model.getAdapter().setData.forEach {
                        database.child("user").child(model.userKey.get().toString()).child("memo")
                            .child(it.key).ref.removeValue()
                    }
                    Toast.makeText(this@MainActivity, R.string.text_delete, Toast.LENGTH_SHORT)
                        .show()
                    onBackPressed()
                }
                alertDialog.setNegativeButton(R.string.dialog_text_delete_cancel) { _, _ ->
                    onBackPressed()
//                    mAdapter.notifyDataSetChanged()
                }
                alertDialog.show()
            }

        }
        model.deleteMode.set(true)
    }

    fun openWriteActivity(item: MemoListData?) {
        val intent = Intent(this, MemoActivity::class.java)
        startActivityForResult(intent.apply {
            item?.let {
                putExtra("KEY", it.key)
                putExtra("TITLE", it.title)
                putExtra("TEXT", it.text)
            }
        }, MEMO)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == MEMO) {

            data ?: return

            val userKey = model.userKey.get()
            val date = System.currentTimeMillis()
            val title = data.getStringExtra("TITLE_NEW")
            val text = data.getStringExtra("TEXT")

            if (resultCode == MEMO_RENAME) {
                val memoKey = data.getStringExtra("KEY")
                val memo = MemoListData(memoKey!!, date, title!!, text!!)
                database.child("user").child(userKey!!).child("memo").child(memoKey).setValue(memo)
            } else if (resultCode == MEMO_WRITE) {
                val memoKey =
                    database.child("user/${model.userKey.get().toString()}/memo").push().key
                if (memoKey == null) {
                    Log.w(tag, "Couldn't get push key for posts")
                    return
                }
                val memo = MemoListData(memoKey, date, title!!, text!!) //신규 유저 추가
                val memoValues = memo.toMap()
                val childUpdates = HashMap<String, Any>()
                childUpdates["/user/$userKey/memo/$memoKey"] = memoValues
                database.updateChildren(childUpdates)
            }
        } else if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try { // 구글 로그인 성공
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    fireBaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun fireBaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        /*auth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) { // 로그인 성공
                    Toast.makeText(
                        this@MainActivity,
                        R.string.action_sign_in,
                        Toast.LENGTH_SHORT
                    ).show()
                    setUserInfo()
                } else { // 로그인 실패
                    Toast.makeText(
                        this@MainActivity,
                        R.string.action_sign_in_fail,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }*/
//        signInWithGoogleAuthCredential(credential)
    }
/*
    fun signInWithGoogleAuthCredential(googleAuthCredential : AuthCredential) {
        model.signInWithGoogle(googleAuthCredential)
        model.authenticatedUserLiveData!!.observe(this, Observer {
            if(it.isNew){
                createNewUser(authenticatedUser)
            }else{
                goToMainActivity(authenticatedUser)
            }
        })

    }


    fun createNewUser(authenticatedUser:User) {
        model.createUser(authenticatedUser)
        model.createdUserLiveData.observe(this, Observer<User> {
            if (user.isCreated) {
//            toastMessage(user.name)
            }
//        goToMainActivity(user)
        })
    }*/


    private fun setUserInfo() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            model.email.set(currentUser.email)
            model.uid.set(currentUser.uid)
            model.verified.set(currentUser.isEmailVerified)

            listener = object : ValueEventListener { //메모쪽만 실행해서 반복하면 된다.

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.child("user").let {

                        if (!model.userKey.get().isNullOrEmpty()) return

                        it.children.forEach { now ->
                            if (currentUser.uid == now.child("uid").value) {
                                model.userKey.set(now.key)
                                //loading start
//                                textCenter.text = getString(R.string.text_memo_loading)
                                textCenter.text = getString(R.string.text_new_memo)
                                Log.d(tag, "set listener")
                                database.child("user").child(now.key!!).child("memo")
                                    .addChildEventListener(memoListener)
                                return
                            }
                        }

                        val key = database.child("user").push().key
                        if (key == null) {
                            Log.w(tag, "Couldn't get push key for posts")
                            return
                        }

                        model.userKey.set(key)
                        database.child("user").child(key).child("memo")
                            .addChildEventListener(memoListener)
//                        val user = User(currentUser.email, currentUser.uid) //신규 유저 추가
//                        val userValues = user.toMap()
                        val childUpdates = HashMap<String, Any>()
//                        childUpdates["/user/$key"] = userValues
                        database.updateChildren(childUpdates)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(tag, "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.orderByChild("user").addListenerForSingleValueEvent(listener)

        }
        invalidateOptionsMenu()
    }
/*
    fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
        return map {
            if (block(it)) newValue else it
        }
    } //새로운 가능성을 보임.
*/

    override fun onDestroy() {
        database.orderByChild("user").apply {
            removeEventListener(listener)
        }
        model.userKey.get()?.let {
            database.child("user").child(it).child("memo").removeEventListener(memoListener)
        }
        super.onDestroy()
    }

    override fun onClick(p0: View?) {

    }

}
