package com.makestorming.quicknote.splash

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.makestorming.quicknote.database.User
import java.util.HashMap

@SuppressWarnings("ConstantConditions")
class SplashRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user : User = User("", "", null, false) //빈 생성자가 되어야함.
    private val rootRef  = FirebaseDatabase.getInstance()
    private val usersRef = rootRef.reference
    private var listener: ValueEventListener = object : ValueEventListener { //메모쪽만 실행해서 반복하면 된다.

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val firebaseUser : FirebaseUser = firebaseAuth.currentUser!!
            dataSnapshot.child("user").let {

                it.children.forEach { now ->
                    if (firebaseUser.uid == now.child("uid").value) { //유저가 있는 경우.
                        Log.w("test", "uid 일치")
//                            user.uid = now.child("uid").value // 이 값에서 이메일정보등을 추출해서 user에 대입함.
//                            userMutableLiveData.value = user
                        return
                    }
                }

                Log.w("test", "uid 비 일치")
                val key = usersRef.child("user").push().key
                if (key == null) {
                    Log.w("test", "Couldn't get push key for posts")
                    return
                }

//                    usersRef.child("user").child(key).child("memo")
//                        .addChildEventListener(memoListener)
                user.email = firebaseUser.email
                user.uid = firebaseUser.uid
                user.isAuthenticated = true
//                User(firebaseUser.email, firebaseUser.uid, null, true) //신규 유저 추가
                val userValues = user.toMap()
                val childUpdates = HashMap<String, Any>()
                childUpdates["/user/$key"] = userValues
                usersRef.updateChildren(childUpdates)
//                userMutableLiveData.value = user // <- user데이터 넣어야함.
            }

        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
        }
    }

    fun checkIfUserIsAuthenticatedInFirebase() : User  {
//        val isUserAuthenticateInFirebaseMutableLiveData: MutableLiveData<User> = MutableLiveData()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            user.isAuthenticated = false
//            isUserAuthenticateInFirebaseMutableLiveData.setValue(user) //인증화면으로 넘어가게된다.
        } else {
            user.uid = firebaseUser.uid
            user.isAuthenticated = true
//            isUserAuthenticateInFirebaseMutableLiveData.setValue(user)
        }
//        return isUserAuthenticateInFirebaseMutableLiveData
        return user
    }

    fun addUserToLiveData(uid : String) : User { //추가된 유저의 정보가 반환되어야 할것 같음.
        val userMutableLiveData: MutableLiveData<User> = MutableLiveData() //데이터 베이스에 유저 추가하고.
        val firebaseUser : FirebaseUser = firebaseAuth.currentUser!!
        usersRef.orderByChild("user").addListenerForSingleValueEvent(listener)

/*
        usersRef.document(uid).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot document = userTask.getResult()
                if(document.exists()) {
                    User user = document.toObject(User.class)
                    userMutableLiveData.setValue(user)
                }
            } else {
//                logErrorMessage(userTask.getException().getMessage())
            }
        })*/
//        return userMutableLiveData
        return user
    }

    fun removeListener(){
        usersRef.orderByChild("user").apply {
            removeEventListener(listener)
        }
    }
}