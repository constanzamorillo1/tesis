package uala.mvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tesis.R
import kotlinx.android.synthetic.main.activity_detail.*
import uala.mvvm.core.Product

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        intent.extras?.let { extras ->
            val model = extras.get("PRODUCT") as Product
            textStatus.text = model.status
            textDomain.text = model.domainId
            textName.text = model.name
        }
    }
}
