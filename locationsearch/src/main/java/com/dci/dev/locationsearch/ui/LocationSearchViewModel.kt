package com.dci.dev.locationsearch.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dci.dev.locationsearch.di.DataProvider
import com.dci.dev.locationsearch.di.Instances
import com.dci.dev.locationsearch.domain.LocationSearchRepository
import com.dci.dev.locationsearch.domain.model.Location
import com.dci.dev.locationsearch.utils.NetworkResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.delayEach
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LocationSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository: LocationSearchRepository =
        Instances.provideLocationSearchRepository(application, DataProvider.PositionStackDataProvider(application))

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _isApiError = MutableLiveData<Boolean>(false)
    val isApiError: LiveData<Boolean> = _isApiError

    private val _isHintVisible = MutableLiveData<Boolean>(true)
    val isHintVisible: LiveData<Boolean> = _isHintVisible

    private val _searchResult = MutableLiveData<List<Location>>()
    val searchResult: LiveData<List<Location>> = _searchResult

    var searchQueryText = ""
    private var lastSuccessfulSearchQueryText = ""

    fun validateSearchQuery(query: String) {
        searchQueryText = query
        _isHintVisible.postValue(query.length < 3)
    }

    fun search() {
        if (searchQueryText != lastSuccessfulSearchQueryText && searchQueryText.length >= 3) {
            _loading.postValue(true)
            _isApiError.postValue(false)
            viewModelScope.launch {
                locationRepository.search(searchQueryText)
                    .onEach {
                        delay(1000L)
                    }
                    .catch {
                        _loading.postValue(false)
                        _isApiError.postValue(true)
                    }
                    .collect {
                        _loading.postValue(false)
                        when(it) {
                            is NetworkResult.Success -> {
                                _isApiError.postValue(false)
                                lastSuccessfulSearchQueryText = searchQueryText
                                it.data?.let { data ->
                                    _searchResult.postValue(data)
                                }
                            }
                            is NetworkResult.Error -> {
                                _isApiError.postValue(true)
                                lastSuccessfulSearchQueryText = ""
                            }
                        }
                    }
            }
        }
    }

}