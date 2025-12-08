package com.callsecurity.agent2.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.callsecurity.agent2.R
import com.google.android.material.button.MaterialButton
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var btnPermission: MaterialButton

    private val requestPermissionLauncher = 
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle the result of the runtime permissions request
            val allGranted = permissions.entries.all { it.value }
            if (allGranted) {
                // Permissions granted, now move to the final Call Screening setting
                promptForDefaultCallScreeningApp()
            } else {
                Toast.makeText(this, "Permissions denied. Cannot enable Call Shield AI.", Toast.LENGTH_LONG).show()
                // You can add logic here to show a dialog explaining why permissions are needed
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPermission = findViewById(R.id.btnPermission)
        btnPermission.text = "Enable Call Shield AI"
        
        // Initial check to determine the button's action
        checkAppState()

        // Button listener: either requests permissions or opens settings
        btnPermission.setOnClickListener {
            if (arePermissionsGranted()) {
                promptForDefaultCallScreeningApp()
            } else {
                requestPermissions()
            }
        }
    }
    
    // --- CORE LOGIC FUNCTIONS ---
    
    private fun checkAppState() {
        if (arePermissionsGranted() && isCallScreeningAppEnabled()) {
            btnPermission.text = "Call Shield AI is Active"
            btnPermission.isEnabled = false // App is fully enabled, no more actions needed
        } else if (arePermissionsGranted()) {
            btnPermission.text = "Final Step: Set as Default Screener"
        } else {
            btnPermission.text = "Grant Required Permissions"
        }
    }

    private fun arePermissionsGranted(): Boolean {
        var allGranted = true
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false
                break
            }
        }
        return allGranted
    }

    private fun requestPermissions() {
        // Request the permissions using the new Activity Result API
        requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun isCallScreeningAppEnabled(): Boolean {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        // Check if the user has set your app as the default call screening service
        return telecomManager.hasBecomeDefaultDialer() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && telecomManager.defaultDialerPackage == packageName)
    }

    private fun promptForDefaultCallScreeningApp() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // CRITICAL FIX: Direct link to Call Screening settings page (Q+)
            Intent(TelecomManager.ACTION_CONFIGURE_PHONE_ACCOUNT_SETTINGS)
                .putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, "your_phone_account_handle_here") // Optional but helpful
        } else {
            // Fallback for older APIs if necessary, though CallScreeningService is Q+ feature
            Intent(Settings.ACTION_SETTINGS)
        }
        
        Toast.makeText(this, "Select 'Call Shield AI' in the next menu.", Toast.LENGTH_LONG).show()
        startActivityForResult(intent, CALL_SCREENING_SETTINGS_REQUEST)
    }
    
    override fun onResume() {
        super.onResume()
        // Re-check state every time the user returns from settings
        checkAppState()
    }
}
