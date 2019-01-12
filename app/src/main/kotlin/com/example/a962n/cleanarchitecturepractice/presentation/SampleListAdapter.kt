package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.paging.PagedListAdapter
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.a962n.cleanarchitecturepractice.R
import com.example.a962n.cleanarchitecturepractice.databinding.AdapterSampleListItemBinding
import com.example.a962n.cleanarchitecturepractice.extension.inflater
import kotlin.properties.Delegates

class SampleListAdapter : PagedListAdapter<SampleListItemView ,SampleListAdapter.ViewHolder>(COMPARATOR) {

    var collections: List<SampleListItemView> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        var inflater = parent.inflater()
        var binding: AdapterSampleListItemBinding = DataBindingUtil.inflate(inflater, R.layout.adapter_sample_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.apply {
            holder.bind(this)
        }
    }



//    override fun getItemCount(): Int {
//        return collections.count()
//    }

    class ViewHolder(private val binding: AdapterSampleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SampleListItemView) {
            binding.viewEntity = item
        }
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<SampleListItemView>() {
            override fun areItemsTheSame(p0: SampleListItemView, p1: SampleListItemView): Boolean {
                return p0 == p1
            }

            override fun areContentsTheSame(p0: SampleListItemView, p1: SampleListItemView): Boolean {
                return p0 == p1
            }
        }
    }

}