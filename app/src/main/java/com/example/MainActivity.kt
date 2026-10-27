package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.navigation.PoshanAppScaffold
import com.example.ui.theme.PoshanTrackerTheme
import com.example.ui.viewmodel.PoshanViewModel

class MainActivity : ComponentActivity() {

    private val poshanViewModel: PoshanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PoshanTrackerTheme {
                PoshanAppScaffold(
                    viewModel = poshanViewModel,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

