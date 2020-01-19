package com.xyx.landmark.ui


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.xyx.landmark.R
import com.xyx.landmark.vo.updateUserInfo

class CheckAuthAndPermissionFragment : Fragment() {

    private val REQUEST_CODE_PERMISSION_LOCATION = 7

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        auth.currentUser?.run {
            Toast.makeText(
                context?.applicationContext,
                getString(R.string.tip_account_info, this.displayName),
                Toast.LENGTH_SHORT
            ).show()
            updateUserInfo(uid, displayName ?: uid)
            if (checkPermission()) nextScreen() else requestPermission()
        } ?: run { callAuth() }
    }

    private fun nextScreen() {
        findNavController().navigate(R.id.action_checkAuthFragment_to_mapFragment)
    }

    private fun callAuth() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivity(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
        )
    }

    private fun checkPermission(): Boolean {
        return context?.run {
            (hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    && hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
        } ?: false
    }

    private fun hasPermission(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), REQUEST_CODE_PERMISSION_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION
            && checkPermission()
        ) {
            nextScreen()
        } else {
            Toast.makeText(
                context?.applicationContext,
                getString(R.string.tip_location_permission_err, getString(R.string.app_name)),
                Toast.LENGTH_LONG
            ).show()
            activity?.onBackPressed()
        }
    }


}
