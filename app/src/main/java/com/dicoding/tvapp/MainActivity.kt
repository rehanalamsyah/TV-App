package com.dicoding.tvapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dicoding.tvapp.navigation.NavGraph
import com.dicoding.tvapp.ui.theme.TVAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TVAppTheme {
                NavGraph()
            }
        }
    }
}