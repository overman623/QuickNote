package com.makestorming.quicknote

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val tag : String = MainActivity::class.java.simpleName
    private val permissions : Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1
    private val PICK_CONTACT_REQUEST = 1
    private val textItems : MutableList<TextListData> = mutableListOf()

    private val mAdapter = TextListAdapter(textItems, object : TextListAdapter.Callback{
        override fun getAction(item : TextListData?) {
            openWriteActivity(item)
        }
    })

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

        loadFiles()

        if(textItems.size == 0) textCenter.text = getString(R.string.text_center)



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

    private fun loadFiles(){


        val file = File(Environment.getDataDirectory().absolutePath +
                "/data/" + packageName + "/memo/ffff.txt")
        val lastModDate = Date(file.lastModified())
        Log.d(tag, "File last modified @ : " +

                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(lastModDate))


        textItems.clear()
        File(Environment.getDataDirectory().absolutePath +
                "/data/" + packageName + "/memo")
            .apply {
                if(exists()){
                    listFiles()?.forEach {
                        textItems.add(
                            TextListData(0,
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it.lastModified()),
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
            loadFiles()
//            if (resultCode == Activity.RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Do something with the contact here (bigger example below)
//            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
