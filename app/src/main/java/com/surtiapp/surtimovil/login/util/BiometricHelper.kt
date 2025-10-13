package com.surtiapp.surtimovil.login.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricHelper(
    private val context: Context,
    private val activity: FragmentActivity,
    private val onSuccess: () -> Unit,
    private val onError: (String) -> Unit
) {

    fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(context)
        val result = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
                    or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        // 🧩 Debug para ver el resultado
        android.util.Log.d("BiometricCheck", "Result code: $result")

        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showPrompt() {
        val executor = ContextCompat.getMainExecutor(context)

        val prompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("No se reconoció el rostro o la huella.")
                }
            })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Iniciar sesión con biometría")
            .setSubtitle("Usa tu rostro o huella para continuar")
            .setNegativeButtonText("Cancelar")
            .build()

        prompt.authenticate(info)
    }
}
