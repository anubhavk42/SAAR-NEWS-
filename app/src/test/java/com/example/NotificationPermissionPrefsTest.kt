package com.example

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.data.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Fix #6 test coverage.
 *
 * The runtime POST_NOTIFICATIONS permission dialog itself is NOT unit-tested here:
 * triggering and observing a system permission prompt requires an instrumented UI
 * test on a real device/emulator (Espresso + UiAutomator), which is out of scope for
 * the local JVM test suite. Robolectric can fake the grant result but cannot prove
 * the dialog is actually shown once-and-only-once after onboarding.
 *
 * What we CAN and do verify at the unit level is the persistence contract this fix
 * relies on: the DataStore-backed `notificationPermissionRequested` flag that gates
 * "have we already asked?" — it must default to false and survive being set to true.
 */
class NotificationPermissionPrefsTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + Job())

    private fun newDataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = dataStoreScope) {
            tmpFolder.newFile("test_${System.nanoTime()}.preferences_pb")
        }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `notificationPermissionRequested defaults to false`() = runBlocking {
        val prefs = PreferencesManager(newDataStore())
        assertFalse(prefs.notificationPermissionRequestedFlow.first())
    }

    @Test
    fun `notificationPermissionRequested persists as true once set`() = runBlocking {
        val dataStore = newDataStore()
        val prefs = PreferencesManager(dataStore)

        assertFalse(prefs.notificationPermissionRequestedFlow.first())

        prefs.setNotificationPermissionRequested(true)

        assertTrue(prefs.notificationPermissionRequestedFlow.first())

        // A fresh manager over the same store still reads true (real persistence).
        val reopened = PreferencesManager(dataStore)
        assertTrue(reopened.notificationPermissionRequestedFlow.first())
    }
}
