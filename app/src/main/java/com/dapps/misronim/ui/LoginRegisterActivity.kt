package com.dapps.misronim.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dapps.misronim.databinding.ActivityLoginRegisterBinding
import com.dapps.misronim.model.AuthState
import com.dapps.misronim.viewmodel.LoginRegisterViewModel

class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginRegisterBinding
    private lateinit var registerLoginViewModel: LoginRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Binding
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLoginViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]

        checkIfUserConnected()
        observeButtons()


        //checkIfUserConnected()

        binding.registerButton.setOnClickListener {

            val emailEditText = binding.emailAddressEditText.text.toString()
            val passwordEditText = binding.passwordEditText.text.toString()

            registerLoginViewModel.registerUser(emailEditText, passwordEditText)

        }

        binding.loginButton.setOnClickListener {

            val emailEditText = binding.emailAddressEditText.text.toString()
            val passwordEditText = binding.passwordEditText.text.toString()

            registerLoginViewModel.loginUser(emailEditText, passwordEditText)

        }

        binding.forgotPasswordTextView.setOnClickListener {

            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

    }

    private fun observeButtons() {

        // Register Button
        registerLoginViewModel.getAuthState().observe(this, object : Observer<AuthState?> {
            override fun onChanged(state: AuthState?) {
                when (state) {
                    is AuthState.Success -> {
                        hideLoadingScreen()
                        Toast.makeText(
                            this@LoginRegisterActivity,
                            "Welcome to Misronim !",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                this@LoginRegisterActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    }
                    is AuthState.AuthError -> {
                        Toast.makeText(
                            this@LoginRegisterActivity,
                            state.message,
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

        //Login Button
        registerLoginViewModel.getAuthState()
            .observe(this@LoginRegisterActivity, object : Observer<AuthState?> {
                override fun onChanged(loginState: AuthState?) {

                    when (loginState) {

                        is AuthState.Success -> {
                            hideLoadingScreen()
                            Toast.makeText(
                                this@LoginRegisterActivity,
                                "Welcome Back!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Intent(this@LoginRegisterActivity, MainActivity::class.java)
                            finish()
                        }

                        is AuthState.AuthError -> {
                            hideLoadingScreen()
                            Log.e("Error:", "Error Message: ${loginState.message}")
                            Toast.makeText(
                                this@LoginRegisterActivity,
                                loginState.message,
                                Toast.LENGTH_SHORT
                            ).show()
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


    private fun checkIfUserConnected() {

        if (registerLoginViewModel.checkIfUserConnected()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun showLoadingScreen() {
        binding.registerLoginLoadingLayout?.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        binding.registerLoginLoadingLayout?.visibility = View.GONE
    }


}