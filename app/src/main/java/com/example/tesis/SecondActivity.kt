package com.example.tesis

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        buttonB.setOnClickListener {
            val intent = Intent().putExtra("Message", "COTIIIII B")
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("onStart", "Estoy en onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("onRestart", "Estoy en onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResume", "Estoy en onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("onPause", "Estoy en onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("onStop", "Estoy en onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "Estoy en onDestroy")
    }

    companion object {
        const val CODE = 1
    }
}
