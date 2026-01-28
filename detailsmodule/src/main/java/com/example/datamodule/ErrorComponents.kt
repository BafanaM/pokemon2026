package com.example.datamodule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Enhanced error content component for network errors.
 */
@Composable
fun NetworkErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon using emoji
        Text(
            text = when {
                message.contains("No internet connection", ignoreCase = true) -> "📶"
                message.contains("timeout", ignoreCase = true) -> "⏰"
                message.contains("server", ignoreCase = true) -> "☁️"
                message.contains("network", ignoreCase = true) -> "📡"
                else -> "❌"
            },
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Error title
        Text(
            text = when {
                message.contains("No internet connection", ignoreCase = true) -> "No Internet Connection"
                message.contains("timeout", ignoreCase = true) -> "Request Timed Out"
                message.contains("server", ignoreCase = true) -> "Server Error"
                message.contains("network", ignoreCase = true) -> "Network Error"
                else -> "Something Went Wrong"
            },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Error message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Retry button
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("🔄 Retry")
        }
        
        // Additional help text for specific errors
        when {
            message.contains("No internet connection", ignoreCase = true) -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Check your Wi-Fi or mobile data connection",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            message.contains("timeout", ignoreCase = true) -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your connection might be slow",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            message.contains("server", ignoreCase = true) -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "The server is experiencing issues",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Simple error content component for general errors.
 */
@Composable
fun SimpleErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "❌",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Text("🔄 Retry")
        }
    }
}

/**
 * Loading content component with a spinner.
 */
@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}
