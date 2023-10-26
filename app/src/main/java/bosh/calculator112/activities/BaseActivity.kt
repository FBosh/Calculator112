package bosh.calculator112.activities

import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import bosh.calculator112.utilities.BuildConfigBuffer

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    protected abstract val vb: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    private fun init() {
        initUI()
    }

    @CallSuper
    protected open fun initUI() {
        setContentView(vb.root)

        setKeepScreenOn(true)
    }

    protected fun printBoshLog(msg: Any) {
        Log.i("Bosh_Tag", msg.toString())
    }

    protected fun setKeepScreenOn(setting: Boolean) {
        if (!BuildConfigBuffer.isDebugVer) return

        vb.root.keepScreenOn = setting
    }
}
