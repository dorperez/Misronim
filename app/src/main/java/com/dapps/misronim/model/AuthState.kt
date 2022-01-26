package com.dapps.misronim.model

sealed class AuthState {

    object Idle : AuthState()
    object Success : AuthState()
    object Loading : AuthState()
    class AuthError(val message: String? = null) : AuthState()

}