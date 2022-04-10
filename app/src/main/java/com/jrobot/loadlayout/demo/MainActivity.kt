package com.jrobot.loadlayout.demo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jrobot.loadlayout.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.btnContent.setOnClickListener { activityMainBinding.loadLayout.showContent() }
        activityMainBinding.btnLoad.setOnClickListener { activityMainBinding.loadLayout.showLoading() }
        activityMainBinding.btnError.setOnClickListener { activityMainBinding.loadLayout.showError() }
        activityMainBinding.btnEmpty.setOnClickListener { activityMainBinding.loadLayout.showEmpty() }
        activityMainBinding.btnErrorMsg.setOnClickListener {
            activityMainBinding.loadLayout.showError(
                "这是错误消息..."
            )
        }
        activityMainBinding.loadLayout.setRetryListener {
            Toast.makeText(
                this,
                "loading...",
                Toast.LENGTH_SHORT
            ).show()
        }
        activityMainBinding.loadLayout.observerStatus(this) {
            Log.i(TAG, "stats changed: $it")
            activityMainBinding.status = it
//            activityMainBinding.setVariable(BR.status,it)
        }
    }
}