package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

class AuthenticationFragment: Fragment() {
    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private val authViewModel by viewModels<AuthenticationViewModel>()
    private lateinit var binding: FragmentAuthenticationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_authentication,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
    }

    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user: starts the reminders activity
     * If there is no logged in user: show a login button
     */
    private fun observeAuthenticationState() {
        authViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
                when (authenticationState) {
                    AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                        // start reminders activity
                        val intent = Intent(requireActivity(), RemindersActivity::class.java)
                        // Once authenticated, we don't want to go back to the login screen, unless the users actually presses on the Logout button
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                    else -> {
                        binding.authButton.setOnClickListener {
                            launchSignInFlow()
                        }
                    }
                }
            })
    }

    private fun launchSignInFlow() {
        // Provide options to login with email and Google Login
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the SIGN_IN_RESULT_CODE
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            AuthenticationFragment.SIGN_IN_RESULT_CODE
        )
    }
}