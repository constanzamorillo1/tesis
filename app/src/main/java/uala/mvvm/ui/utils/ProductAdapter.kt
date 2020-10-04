package uala.mvvm.ui.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tesis.R
import com.example.tesis.databinding.ActivityLayoutItemBinding
import uala.mvvm.core.Product

class ProductAdapter(private var products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private lateinit var listener: AdapterListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
         ProductViewHolder(
             DataBindingUtil.inflate(
                 LayoutInflater.from(parent.context),
                 R.layout.activity_layout_item,
                 parent,
                 false
             )
         )

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.productBinding.product = products[position]
    }

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    fun setAdapterListener(listener: AdapterListener) {
        this.listener = listener
    }

    /*inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        fun bind(model: Product) {
            itemView.findViewById<TextView>(R.id.textStatus).apply {
                text = model.status
            }
            itemView.findViewById<TextView>(R.id.textDomain).apply {
                text = model.domainId
            }
            itemView.findViewById<TextView>(R.id.textName).apply {
                text = model.name
            }
            itemView.setOnClickListener {
                listener.onItemClick(model)
            }
        }
    }*/

    inner class ProductViewHolder(val productBinding: ActivityLayoutItemBinding) : RecyclerView.ViewHolder(productBinding.root) {

    }
}

interface AdapterListener {

    fun onItemClick(model: Product)
}