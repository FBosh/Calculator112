package bosh.calculator112

import android.app.Application
import android.os.Handler
import android.os.Looper

class App : Application() {
    companion object {
        const val TAG = "App"
        private lateinit var instance: App

        fun shared() = instance
    }

    val appHandler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()

        instance = this
    }
}
