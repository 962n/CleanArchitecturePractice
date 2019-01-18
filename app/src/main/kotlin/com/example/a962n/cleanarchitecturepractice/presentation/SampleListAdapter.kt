package com.example.a962n.cleanarchitecturepractice.presentation

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.a962n.cleanarchitecturepractice.R
import com.example.a962n.cleanarchitecturepractice.databinding.AdapterSampleListItemBinding
import com.example.a962n.cleanarchitecturepractice.extension.inflater
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.AppPagedListAdapter

class SampleListAdapter : AppPagedListAdapter<SampleListItemView>(COMPARATOR) {

    override fun getFeatureViewType(position: Int): Int {
        return R.layout.adapter_sample_list_item
    }

    override fun onFeatureBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }


    override fun onFeatureCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var inflater = parent.inflater()
        var binding: AdapterSampleListItemBinding = DataBindingUtil.inflate(inflater, R.layout.adapter_sample_list_item, parent, false)
        return ViewHolder(binding)
    }

    class ViewHolder(private val binding: AdapterSampleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SampleListItemView?) {
            item?.apply {
                binding.viewEntity = this
            }
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