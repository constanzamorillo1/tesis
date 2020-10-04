package uala.mvvm.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uala.mvvm.core.ProductResponse
import uala.mvvm.domain.ProductRepository
import uala.mvvm.domain.RepositoryResult

class ProductsViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableLiveData<State>()
    private val _loading = MutableLiveData<State.Loading>()

    val products : LiveData<State>
        get() = _products

    val loading : LiveData<State.Loading>
        get() = _loading

    fun getProducts(search: String) {
        _loading.postValue(State.Loading.Show)
        repository.getProducts(search) {
            when(it) {
                is RepositoryResult.Success -> {
                    _products.postValue(State.SuccessState(it.value))
                }
                is RepositoryResult.ErrorWithCode -> {
                    _products.postValue(State.ErrorState(it.statusCode))
                }
            }
            _loading.postValue(State.Loading.Hide)
        }
    }
}

sealed class State {

    sealed class Loading : State() {
        object Show: Loading()
        object Hide : Loading()
    }

    data class SuccessState(val value: ProductResponse) : State()
    data class ErrorState(val code: Int?) : State()
}