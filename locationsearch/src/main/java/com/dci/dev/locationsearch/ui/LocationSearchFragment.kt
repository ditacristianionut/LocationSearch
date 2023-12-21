package com.dci.dev.locationsearch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dci.dev.locationsearch.domain.model.Location


class LocationSearchFragment : Fragment() {

    companion object {
        fun newInstance() = LocationSearchFragment()
    }

    private val viewModel: LocationSearchViewModel by lazy {
        ViewModelProvider(this)[LocationSearchViewModel::class.java]
    }
    var onLocationSelectedCallback: ((Location) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {

                val searchResults by viewModel.searchResult.observeAsState(emptyList())
                val screenState by viewModel.screenState.observeAsState(ScreenState.Idle)

                SearchScreen(
                    searchQuery = viewModel.searchQueryText.value,
                    searchResults = searchResults,
                    screenState = screenState,
                    onSearchQueryChange = viewModel::validateSearchQuery,
                    search = viewModel::search,
                    onItemSelected = { onLocationSelectedCallback?.invoke(it) },
                    onBackPressed = { activity?.onBackPressed() }
                )
            }
        }
    }
}