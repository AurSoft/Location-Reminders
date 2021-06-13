package com.udacity.project4.locationreminders

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.authentication.AuthenticationViewModel
import kotlinx.android.synthetic.main.activity_reminders.*

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)
        observeAuthenticationState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (nav_host_fragment as NavHostFragment).navController.popBackStack()
                return true
            }
            R.id.logout -> {
                AuthUI.getInstance().signOut(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeAuthenticationState() {
        authViewModel.authenticationState.observe(this, Observer { authenticationState ->
            if (authenticationState == AuthenticationViewModel.AuthenticationState.UNAUTHENTICATED) {
                // start authentication activity
                val intent = Intent(this, AuthenticationActivity::class.java)
                // Once authenticated, we don't want the user to still be seeing reminders list, unless the user logs in again
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        })
    }

}
