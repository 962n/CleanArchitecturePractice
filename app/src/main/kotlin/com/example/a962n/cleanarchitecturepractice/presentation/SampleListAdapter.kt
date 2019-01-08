package com.example.a962n.cleanarchitecturepractice.presentation

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.a962n.cleanarchitecturepractice.R
import com.example.a962n.cleanarchitecturepractice.databinding.AdapterSampleListItemBinding
import com.example.a962n.cleanarchitecturepractice.extension.inflater
import kotlin.properties.Delegates

class SampleListAdapter : RecyclerView.Adapter<SampleListAdapter.ViewHolder>() {

    var collections: List<SampleListItemView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        var inflater = parent.inflater()
        var binding: AdapterSampleListItemBinding = DataBindingUtil.inflate(inflater, R.layout.adapter_sample_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collections[position])
    }

    override fun getItemCount(): Int {
        return collections.count()
    }

    class ViewHolder(private val binding: AdapterSampleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SampleListItemView) {
            binding.viewEntity = item
        }
    }

}