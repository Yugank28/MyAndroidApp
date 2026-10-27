package com.example.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.model.SessionRole
import com.example.ui.admin.AdminHubScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.parent.ParentHubScreen
import com.example.ui.viewmodel.PoshanViewModel

@Composable
fun PoshanAppScaffold(
    viewModel: PoshanViewModel,
    modifier: Modifier = Modifier
) {
    val session by viewModel.userSession.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = session.role, label = "role_transition") { role ->
                when (role) {
                    SessionRole.GUEST -> {
                        AuthScreen(viewModel = viewModel)
                    }
                    SessionRole.ADMIN -> {
                        AdminHubScreen(viewModel = viewModel)
                    }
                    SessionRole.PARENT -> {
                        ParentHubScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
