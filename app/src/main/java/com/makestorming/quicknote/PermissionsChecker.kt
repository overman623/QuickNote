package com.makestorming.quicknote;

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class PermissionsChecker(private var context: Context) {

    //oncreate에 작성.
    /*
        PermissionsChecker checker = new PermissionsChecker(this);
        if (checker.lacksPermissions(PERMISSIONS)) { //권한 요청
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    */

    //메인엑티비티에서 작성함.
//    private static final String[] PERMISSIONS = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
//oncreate에 작성.

    fun lacksPermissions(permissions: Array<out String>): Boolean {
        for (permission in permissions) {
            if (lacksPermission(permission)) {
                return true
            }
        }
        return false
    }

    private fun lacksPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_DENIED
    }

}
