package io.myabcwallet.securechannel_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.myabcwallet.securechannel_android.login.LoginViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onLogin(
            id = "{id}",
            password = "{password}"
        )
    }
}