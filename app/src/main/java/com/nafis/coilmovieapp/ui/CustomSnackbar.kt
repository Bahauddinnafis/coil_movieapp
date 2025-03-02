package com.nafis.coilmovieapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.nafis.coilmovieapp.ui.home.defaultPadding
import com.nafis.coilmovieapp.ui.home.itemSpacing
import kotlin.let

@Composable
fun CustomSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData ->
                Snackbar(
                    modifier = modifier
                        .padding(defaultPadding)
                        .align(Alignment.BottomCenter),
                    containerColor = Color(0xFF323232),
                    contentColor = Color.White,
                    actionContentColor = Color(0xFFFFC107),
                    shape = RoundedCornerShape(itemSpacing),
                    action = {
                        snackbarData.visuals.actionLabel?.let { actionLabel ->
                            TextButton(onClick = { snackbarData.performAction() }) {
                                Text(
                                    text = actionLabel,
                                    color = Color(0xFFFFC107),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Text(text = snackbarData.visuals.message)
                }
            }
        )
    }
}