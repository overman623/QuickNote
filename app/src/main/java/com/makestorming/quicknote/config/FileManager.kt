package com.makestorming.quicknote.config

import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class FileManager(var title: String = "", private val beforeTitle: String = "") {

    private val tag = FileManager::class.java.simpleName

    private val mainPath = Environment.getDataDirectory().absolutePath +
            "/data/com.makestorming.quicknote/memo"
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
        var mainText: String? = ""

        while (br.readLine().also{ read = it } != null) {
            mainText += read
        }

        fr.close()
        br.close()
        return mainText
    }

    fun readLine(file: File) : String{
        val fr = FileReader(file)
        val br = BufferedReader(fr)
        var read: String?
        var mainText: String? = null

        while (br.readLine().also{ read = it } != null) {
//            Log.d("file", read)
            if((read?.trim() ?: "").isNotBlank()) {
                mainText = read
                break
            }
        }

        fr.close()
        br.close()

        return mainText!!
    }

    fun isDuplicate() : Boolean{
        File(mainPath)
            .apply {
                if(exists()){
                    listFiles()?.forEach {
                        if(it.name == ("$title.txt")) return true
                    }
                    return false
                }else
                    return true
            }

    }

    fun deleteFile(){
        if(mainFile.exists()){
            mainFile.delete()
        }
    }

}