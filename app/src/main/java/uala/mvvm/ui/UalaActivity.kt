package uala.mvvm.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tesis.R
import kotlinx.android.synthetic.main.activity_uala.*
import uala.mvvm.core.Product
import uala.mvvm.domain.ProductRepository
import uala.mvvm.ui.utils.AdapterListener
import uala.mvvm.ui.utils.ProductAdapter

class UalaActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductsViewModel
    private lateinit var factory: ProductsViewModelFactory
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uala)
        factory = ProductsViewModelFactory(ProductRepository())
        viewModel = ViewModelProvider(this, factory).get(ProductsViewModel::class.java)
        init()
    }

    private fun init() {
        recyclerViewComponents()
        viewModel.products.observe(this, Observer { state ->
            when (state) {
                is State.SuccessState -> {
                    //adapter.updateProducts(state.value.status)
                }
                is State.ErrorState -> {
                    // Nothing here
                }
            }
        })

        viewModel.loading.observe(this, Observer { state ->
            progressBar.visibility = when(state) {
                is State.Loading.Hide -> {
                    View.GONE
                }
                is State.Loading.Show -> {
                    View.VISIBLE
                }
            }
        })

        searchButton.setOnClickListener {
            viewModel.getProducts(searchText.text.toString())
        }
    }

    private fun recyclerViewComponents() {
        val layoutManager = LinearLayoutManager(this@UalaActivity)
        adapter = ProductAdapter(emptyList())
        adapter.setAdapterListener(object: AdapterListener{
            override fun onItemClick(model: Product) {
                val bundle = Bundle()
                bundle.putSerializable(KEY, model)
                val intent = Intent(this@UalaActivity, DetailActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, layoutManager.orientation))

    }

    companion object {
        private const val KEY = "PRODUCT"
    }
}
