package com.makestorming.quicknote

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private val tag : String = MainActivity::class.java.simpleName
    private val permissions : Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        PermissionsChecker(this).apply {
            if(lacksPermissions(permissions)){
                ActivityCompat.requestPermissions(this@MainActivity, permissions, MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            }
        }

        fab.setOnClickListener {
            openWriteActivity(null)
        }

        val textItems : MutableList<TextListData> = mutableListOf()
        File(Environment.getDataDirectory().absolutePath +
                "/data/" + packageName + "/memo")
            .apply {
                if(exists()){
                    listFiles()?.forEach {
                        textItems.add(0, TextListData(0, "date",
                            it.name.replace(".txt", ""), FileManager().readLine(it)))
                    }
                }else
                    mkdir()
            }

//        val textItems : MutableList<TextListData> = mutableListOf(TextListData(0,"date1", "Title1", "Text1"),
//            TextListData(0,"date2", "Title2", "Text2"),TextListData(0,"date3", "Title3", "Text3"))

        if(textItems.size == 0) textCenter.text = getString(R.string.text_center)

        val mAdapter = TextListAdapter(textItems, object : TextListAdapter.Callback{
            override fun getAction(item : TextListData?) {
                openWriteActivity(item)
            }
        })

        textViewList.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        textViewList.layoutManager = lm
        textViewList.setHasFixedSize(true)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openWriteActivity(item : TextListData?) {
        val intent = Intent(this, MemoActivity::class.java)
        startActivity(intent.apply {
            item?.let {
                putExtra("TITLE", it.title)
                putExtra("DATE", it.date)
                putExtra("ORDER", it.order)
            }
        })
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
                    Toast.makeText(this, "You have to allow permissions", Toast.LENGTH_SHORT).show()
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

}
