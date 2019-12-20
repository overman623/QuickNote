package com.makestorming.quicknote

import android.os.Environment
import android.util.Log
import java.io.*


class FileManager(title: String, private val beforeTitle: String = "") {

    private val mainPath = Environment.getDownloadCacheDirectory().absolutePath + File.separator + "memo"
    private val mainFile = File(mainPath + File.separator + "$title.txt")


    fun makeFile(
        date: String?,
        text: String?,
        order: Int
    ): Boolean {
        val oldFile = File(mainPath, File.separator + "$beforeTitle.txt")
        val newFile = mainFile

        if(oldFile.exists()){
            oldFile.renameTo(newFile)
        }

        //I will add metadata : date
        FileWriter(newFile, false).apply {
            write(text)
            flush()
            close()
        }
        return true
    }

    fun readFile(): String? {
        val fr = FileReader(mainFile)
        val br = BufferedReader(fr)
        var read: String?
        var mainText: String? = null

        while (br.readLine().also{ read = it } != null) {
            Log.d("file", read)
            mainText += read
        }

        fr.close()
        br.close()
        return mainText
    }

    fun deleteFile(){
        if(mainFile.exists()){
            mainFile.delete()
        }
    }

}