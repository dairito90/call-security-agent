package com.callsecurity.agent2.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.callsecurity.agent2.core.CallClassifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val classifier = CallClassifier()

        setContent {
            var input by remember { mutableStateOf("") }
            var result by remember { mutableStateOf("") }

            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("CallShield AI") })
                    }
                ) { padding ->
                    Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            label = { Text("Enter phone number") }
                        )

                        Button(
                            onClick = {
                                val spamScore = classifier.classificationScore(input)
                                result = "Spam score: $spamScore"
                            },
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text("Analyze")
                        }

                        if (result.isNotEmpty()) {
                            Text(result, modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }
            }
        }
    }
}
