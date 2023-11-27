package io.myabcwallet.securechannel_android.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.myabcwallet.securechannel.network.auth.GetSecureChannelUseCase
import io.myabcwallet.securechannel.network.auth.encrypt
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 * @author jin on 11/27/23
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getSecureChannelUseCase: GetSecureChannelUseCase,
) : ViewModel() {

    val TAG = "ABC"

    fun onLogin(id: String, password: String) {
        viewModelScope.launch {
            getSecureChannelUseCase().collect { secureChannelData ->
                secureChannelData?.let {
                    Log.d(TAG, "secureChannelId: ${secureChannelData.channelId}")
                    Log.d(TAG, "sharedSecret: ${secureChannelData.secret}")

                    val encryptedPassword = password.encrypt(secureChannelData.secret)

                    login(id, encryptedPassword)
                }
            }
        }
    }

    fun login(id: String, encryptedPassword: String) {
        // TODO: Communicate about login
    }
}