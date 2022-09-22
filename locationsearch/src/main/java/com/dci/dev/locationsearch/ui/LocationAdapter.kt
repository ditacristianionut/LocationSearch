package com.dci.dev.locationsearch.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dci.dev.locationsearch.databinding.ItemLocationBinding
import com.dci.dev.locationsearch.domain.model.Location

class LocationAdapter(private val context: Context) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    private val appsList: MutableList<Location> = mutableListOf()
    private val inflater = LayoutInflater.from(context)
    var onClick: ((Location) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(inflater, parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(appsList[position]) {
            onClick?.invoke(it)
        }
    }

    override fun getItemCount(): Int = appsList.size


    fun addItems(list: List<Location>) {
        appsList.clear()
        appsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class LocationViewHolder(private val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: Location, onClick: (Location) -> Unit) {
            binding.textviewTitle.text = location.name
            binding.textviewSubtitle.text = "${location.region}, ${location.country}"
            binding.root.setOnClickListener { onClick.invoke(location) }
        }

    }

}