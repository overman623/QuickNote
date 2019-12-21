package com.makestorming.quicknote;

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class PermissionsChecker(private var context: Context) {

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
