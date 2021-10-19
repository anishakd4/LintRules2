package com.example.lintrules2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WrongActivity2 : CorrectActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong2)
    }
}
