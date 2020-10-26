package com.example.tesis

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_first.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        buttonA.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(DEEPLINK_B)
            }
            startActivityForResult(intent, CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getStringExtra(MESSAGE)?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if  (hasFocus) {
            Log.d("WINDOW CHANGED", "ACA ESTOY")
        }

        super.onWindowFocusChanged(hasFocus)
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
        const val MESSAGE = "Message"
        const val DEEPLINK_B = "activity://b"
    }
}
