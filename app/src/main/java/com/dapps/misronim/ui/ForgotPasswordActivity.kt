package com.dapps.misronim.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dapps.misronim.R
import com.dapps.misronim.databinding.ActivityForgotPasswordBinding
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.viewmodel.LoginRegisterViewModel


class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotPasswordBinding
    lateinit var forgotPasswordViewModel: LoginRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.forgotPasswordToolBar)
        supportActionBar?.title = ""

        forgotPasswordViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]

        binding.resetPasswordButton.setOnClickListener {

            val userEmailAddress = binding.emailAddressEditText.text.toString()
            forgotPasswordViewModel.resetUserPassword(userEmailAddress)

        }

        forgotPasswordViewModel.getAuthState().observe(this, object : Observer<AuthState?> {
            override fun onChanged(authState: AuthState?) {

                when (authState) {
                    is AuthState.Success -> {

                        hideLoadingScreen()
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "A link was sent to your email address successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    is AuthState.AuthError -> {

                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            authState.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        hideLoadingScreen()

                    }
                    is AuthState.Loading -> {

                        showLoadingScreen()

                    }
                    else -> {

                    }

                }

            }
        })


    }

    private fun showLoadingScreen() {
        binding.forgotPasswordLoadingLayout?.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        binding.forgotPasswordLoadingLayout?.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_forgot_password, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuItemID = item.itemId

        if (menuItemID == R.id.forgotPasswordBackButton) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


}