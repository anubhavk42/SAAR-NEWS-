package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppNavigation
import com.example.ui.MainViewModel
import com.example.ui.theme.DigestTheme

class MainActivity : ComponentActivity() {

    // One system prompt, once. We don't care about the result here — whether the user
    // grants or denies, we respect that choice and never ask again (the "already asked"
    // flag is persisted in PreferencesManager).
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op: respect the user's choice */ }

        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val themeProfile by viewModel.themeProfile.collectAsState()
            val onboardingSeen by viewModel.onboardingSeen.collectAsState()
            val permissionAlreadyRequested by viewModel.notificationPermissionRequested.collectAsState()

            // Ask for the POST_NOTIFICATIONS permission exactly once, right after the
            // onboarding flow completes. On API < 33 the permission is granted at install
            // time, so there is nothing to do.
            LaunchedEffect(onboardingSeen, permissionAlreadyRequested) {
                if (onboardingSeen &&
                    !permissionAlreadyRequested &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ) {
                    viewModel.setNotificationPermissionRequested(true)
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            DigestTheme(themeProfile = themeProfile) {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
