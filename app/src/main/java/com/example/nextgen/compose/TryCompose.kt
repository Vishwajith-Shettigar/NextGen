package com.example.nextgen.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class TryCompose : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        Greeting()
      }
    }
  }

  @Preview(showBackground = false, name = "greeting")
  @Composable
  fun Greeting() {
    Text(text = "Hello Jetpack Compose")
  }
}
