package com.example.lintrules2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.lintrules2.base.BaseActivity

abstract class CorrectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bark()
        toast()
    }

    fun toast() {
        Toast.makeText(applicationContext, "Anish kumar dubey", Toast.LENGTH_LONG).show()
    }

    fun bark() {
        Log.d(TAG, "woof! woof!")
    }

    companion object {
        private const val TAG = "Sample"
    }
}
