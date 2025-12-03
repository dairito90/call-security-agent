package com.callsecurity.agent2.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.callsecurity.agent.R

/**
 * Main Activity - Setup and status display
 */
class MainActivity : AppCompatActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.READ_CONTACTS
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            updateStatus()
            showSuccessMessage()
        } else {
            showPermissionDeniedDialog()
        }
    }

    private lateinit var statusText: TextView
    private lateinit var setupButton: Button
    private lateinit var settingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        setupButton = findViewById(R.id.setupButton)
        settingsButton = findViewById(R.id.settingsButton)

        setupButton.setOnClickListener {
            checkAndRequestPermissions()
        }

        settingsButton.setOnClickListener {
            openCallBlockingSettings()
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun checkAndRequestPermissions() {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        } else {
            updateStatus()
            showSuccessMessage()
        }
    }

    private fun updateStatus() {
        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            statusText.text = """
                ✅ Call Security Agent Active
                
                Status: Protecting your phone
                Mode: Aggressive Blocking
                Threshold: 70% confidence
                
                Next Steps:
                1. Tap 'Open Phone Settings' below
                2. Enable 'Call Security Agent' in Call Blocking
                3. You're protected!
            """.trimIndent()
            
            setupButton.text = "Permissions Granted ✓"
            setupButton.isEnabled = false
            settingsButton.isEnabled = true
        } else {
            statusText.text = """
                🛡️ Call Security Agent
                
                Setup Required:
                
                This app needs permissions to:
                • Identify incoming calls
                • Block spam automatically
                • Access your contacts (to never block them)
                
                Tap 'Grant Permissions' to start
            """.trimIndent()
            
            setupButton.text = "Grant Permissions"
            setupButton.isEnabled = true
            settingsButton.isEnabled = false
        }
    }

    private fun showSuccessMessage() {
        Toast.makeText(
            this,
            "✅ Permissions granted! Now enable in Phone settings",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage(
                "Call Security Agent needs these permissions to protect you from spam calls:\n\n" +
                "• Phone: To identify incoming calls\n" +
                "• Call Log: To analyze calling patterns\n" +
                "• Contacts: To never block your contacts\n\n" +
                "Without these permissions, the app cannot function."
            )
            .setPositiveButton("Grant Permissions") { _, _ ->
                checkAndRequestPermissions()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun openCallBlockingSettings() {
        try {
            // Try to open call blocking settings directly
            val intent = Intent("android.telecom.action.CHANGE_DEFAULT_DIALER")
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general phone settings
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
                
                Toast.makeText(
                    this,
                    "Go to Permissions → Phone → Enable Call Screening",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e2: Exception) {
                Toast.makeText(
                    this,
                    "Please enable Call Screening in Phone settings manually",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
