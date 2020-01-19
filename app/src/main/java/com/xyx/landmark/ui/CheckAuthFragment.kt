package com.xyx.landmark.ui


import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.xyx.landmark.R
import com.xyx.landmark.vo.updateUserInfo

class CheckAuthFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        // Check if user is signed in (non-null) and update UI accordingly.
        auth.currentUser?.run {
            Toast.makeText(
                context?.applicationContext,
                getString(R.string.tip_account_info, this.displayName),
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigate(R.id.action_checkAuthFragment_to_mapFragment)
            updateUserInfo(uid, displayName ?: uid)
        } ?: run { callAuth() }
    }

    private fun callAuth() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        startActivity(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
        )
    }


}
