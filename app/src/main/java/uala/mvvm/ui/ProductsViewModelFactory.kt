package uala.mvvm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uala.mvvm.domain.ProductRepository

@Suppress("UNCHECKED_CAST")
class ProductsViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProductsViewModel(repository) as T
    }
}