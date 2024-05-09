package com.strezh.composition.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.strezh.composition.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
}