package com.dci.dev.locationsearch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dci.dev.locationsearch.R
import com.dci.dev.locationsearch.databinding.FragmentLocationSearchBinding
import com.dci.dev.locationsearch.domain.model.Location
import com.dci.dev.locationsearch.utils.dp2px


class LocationSearchFragment : Fragment() {

    companion object {
        fun newInstance() = LocationSearchFragment()
    }

    private var _binding: FragmentLocationSearchBinding? = null
    private val binding: FragmentLocationSearchBinding
        get() = _binding!!
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var viewModel: LocationSearchViewModel
    var onLocationSelectedCallback: ((Location) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationSearchBinding.inflate(inflater, container, false)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LocationSearchViewModel::class.java)
        bindData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationAdapter = LocationAdapter(requireContext()).apply {
            onClick = {
                onLocationSelectedCallback?.invoke(it)
            }
        }

        binding.searchview.setBackgroundResource(R.drawable.lib_stroke_rounded_corners_background)
        binding.buttonSearch.setBackgroundResource(R.drawable.lib_textview_ripple)

        binding.recyclerviewResults.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = locationAdapter
        }

        binding.buttonSearch.setOnClickListener {
            viewModel.search()
        }

        binding.buttonBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.searchview.addTextChangedListener { editable ->
            editable?.toString()?.let { query ->
                viewModel.validateSearchQuery(query)
            }
        }

        Glide.with(requireContext())
            .load(R.drawable.lib_loading_animation)
            .into(binding.imageviewLoading)
    }

    private fun bindData() {
        with(viewModel) {

            loading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    when {
                        binding.imageviewError.isVisible -> {
                            binding.imageviewError.animate()
                                .scaleX(0.25f)
                                .scaleY(0.25f)
                                .translationY(-128.dp2px.toFloat())
                                .alpha(0f)
                                .setDuration(1000L)
                                .withStartAction {
                                    binding.imageviewError.animation?.cancel()
                                }
                                .withEndAction {
                                    binding.imageviewLoading.isVisible = true
                                }.start()
                        }
                        binding.imageviewInitialData.alpha > 0f -> {
                            binding.imageviewInitialData.animate()
                                .alpha(0f)
                                .setDuration(250L)
                                .withEndAction {
                                    binding.imageviewLoading.isVisible = true
                                }.start()
                        }
                        binding.imageviewNoData.alpha > 0f -> {
                            binding.imageviewNoData.animate()
                                .alpha(0f)
                                .setDuration(250L)
                                .withEndAction {
                                    binding.imageviewLoading.isVisible = true
                                }.start()
                        }
                    }
                } else {
                    binding.imageviewLoading.isVisible = false
                }
            }

            isHintVisible.observe(viewLifecycleOwner) {
                binding.textviewHint.isVisible = it
            }

            isApiError.observe(viewLifecycleOwner) {
                if (it) {
                    binding.imageviewNoData.animate()
                        .alpha(0f)
                        .setDuration(250L)
                        .start()
                    binding.imageviewInitialData.animate()
                        .alpha(0f)
                        .setDuration(250L)
                        .start()
                    binding.recyclerviewResults.animate()
                        .alpha(0f)
                        .setDuration(250L)
                        .start()
                    binding.imageviewError.scaleX = 0f
                    binding.imageviewError.scaleY = 0f
                    binding.imageviewError.alpha = 0f
                    binding.imageviewError.visibility = View.VISIBLE
                    binding.imageviewError.translationY = -128.dp2px.toFloat()
                    binding.imageviewError.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .translationY(0f)
                        .alpha(0.75f)
                        .setDuration(2000L)
                        .setStartDelay(200)
                        .withEndAction {
                            val bounceAnimation: Animation =
                                AnimationUtils.loadAnimation(activity, com.dci.dev.locationsearch.R.anim.bounce_animation)
                            binding.imageviewError.startAnimation(bounceAnimation)
                        }
                        .start()
                }
                binding.imageviewError.isVisible = it
            }

            searchResult.observe(viewLifecycleOwner) { locationsList ->
                locationAdapter.addItems(locationsList)
                binding.textviewResultsCount.isVisible = locationsList.isNotEmpty()
                binding.textviewResultsCount.text = "${locationsList.size} results"

                if (locationsList.isEmpty()) {
                    when {
                        binding.recyclerviewResults.alpha > 0f -> {
                            binding.recyclerviewResults.animate()
                                .alpha(0f)
                                .setDuration(250L)
                                .withEndAction {
                                    binding.imageviewNoData.animate()
                                        .alpha(1f)
                                        .setDuration(500)
                                        .start()
                                }.start()
                        }
                        else -> {
                            binding.imageviewNoData.animate()
                                .alpha(1f)
                                .setDuration(500)
                                .start()
                        }
                    }
                } else {
                    binding.recyclerviewResults.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .start()
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}