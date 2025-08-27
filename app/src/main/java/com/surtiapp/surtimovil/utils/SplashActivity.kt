package com.surtiapp.surtimovil.utils

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.surtiapp.surtimovil.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { SplashScreenView() }

        lifecycleScope.launch {
            delay(1500) // 1.5 s
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish() // evita volver al splash con "Back"
        }
    }
}