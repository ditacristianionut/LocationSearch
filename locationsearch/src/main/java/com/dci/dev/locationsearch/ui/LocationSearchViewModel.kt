package com.dci.dev.locationsearch.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dci.dev.locationsearch.LocationSearchConfig
import com.dci.dev.locationsearch.di.Instances
import com.dci.dev.locationsearch.domain.LocationSearchRepository
import com.dci.dev.locationsearch.domain.model.Location
import com.dci.dev.locationsearch.utils.NetworkResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LocationSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository: LocationSearchRepository =
        Instances.provideLocationSearchRepository(LocationSearchConfig.dataProviderType, application)

    private val _screenState = MutableLiveData<ScreenState>(ScreenState.Idle)
    val screenState: LiveData<ScreenState> = _screenState

    private val _searchResult = MutableLiveData<List<Location>>()
    val searchResult: LiveData<List<Location>> = _searchResult

    private var _searchQueryText = mutableStateOf("")
    val searchQueryText: State<String> get() = _searchQueryText

    private var lastSuccessfulSearchQueryText = ""

    fun validateSearchQuery(query: String) {
        _searchQueryText.value = query
        if (query.isBlank()) {
            _searchResult.postValue(emptyList())
            lastSuccessfulSearchQueryText = ""
            _screenState.postValue(ScreenState.Idle)
        }
    }

    fun search() {
        if (_searchQueryText.value != lastSuccessfulSearchQueryText && _searchQueryText.value.length >= 3) {
            _screenState.postValue(ScreenState.Loading)

            viewModelScope.launch {
                locationRepository.search(_searchQueryText.value)
                    .onEach {
                        delay(2000L)
                    }
                    .catch {
                        _screenState.postValue(ScreenState.Error)
                        Log.e("LocationSearch", "Failed to get location:\n ${it.stackTrace}")
                    }
                    .collect {
                        when(it) {
                            is NetworkResult.Success -> {
                                _screenState.postValue(ScreenState.Success)
                                lastSuccessfulSearchQueryText = _searchQueryText.value
                                it.data?.let { data ->
                                    _searchResult.postValue(data.sortedBy { it.country })
                                }
                            }
                            is NetworkResult.Error -> {
                                _screenState.postValue(ScreenState.Error)
                                lastSuccessfulSearchQueryText = ""
                                Log.e("LocationSearch", "Failed to get location:\n ${it.message}")
                            }
                        }
                    }
            }
        }
    }

}

sealed class ScreenState {
    object Idle: ScreenState()
    object Loading: ScreenState()
    object Success: ScreenState()
    object Error: ScreenState()
}