package com.sherryyuan.aphora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sherryyuan.aphora.navigation.AphoraRootNav
import com.sherryyuan.aphora.ui.theme.AphoraTheme

class AphoraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AphoraTheme {
                AphoraRootNav()
            }
        }
    }
}
