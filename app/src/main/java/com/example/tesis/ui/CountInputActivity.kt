package com.example.tesis.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.tesis.databinding.ActivityCountInputBinding

class CountInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setComponents()
    }

    private fun setComponents() {
        binding.run {
            countInput.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //nothing here
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // nothing
                    enterButton.isEnabled = count > 0
                }

                override fun afterTextChanged(s: Editable?) {
                    //nothing here
                }
            })

            enterButton.setOnClickListener {
                val intent = Intent(applicationContext, OsmdroidActivity::class.java)
                intent.putExtra(COUNT, countInput.text.toString().toInt())
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val COUNT = "COUNT"
    }
}