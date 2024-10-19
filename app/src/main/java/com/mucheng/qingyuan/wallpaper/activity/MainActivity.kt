package com.mucheng.qingyuan.wallpaper.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mucheng.qingyuan.wallpaper.ui.route.Router
import com.mucheng.qingyuan.wallpaper.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Router()
            }
        }
    }
}