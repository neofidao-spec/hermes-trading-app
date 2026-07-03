// HermesApp.kt – Application class for Hilt DI
package com.hermes.trading

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HermesApp : Application() {
    // No custom logic yet – serves as Hilt entry point.
}