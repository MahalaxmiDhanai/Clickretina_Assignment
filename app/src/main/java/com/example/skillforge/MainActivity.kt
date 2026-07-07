package com.example.skillforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.skillforge.theme.SkillforgeTheme
import com.example.skillforge.ui.navigation.SkillforgeNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillforgeTheme {
                SkillforgeNavHost()
            }
        }
    }
}
