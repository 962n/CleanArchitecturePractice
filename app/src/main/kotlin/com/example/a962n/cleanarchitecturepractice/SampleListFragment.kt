package com.example.a962n.cleanarchitecturepractice

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a962n.cleanarchitecturepractice.databinding.FragmentSampleListBinding
import com.example.a962n.cleanarchitecturepractice.extension.addGlobalLayoutOnce

class SampleListFragment : Fragment() {

    private var adapter = SampleListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentSampleListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sample_list, container, false)
        initializeView(binding)
        return binding.root
    }

    private fun initializeView(binding: FragmentSampleListBinding) {
        context?.let {
            binding.recyclerView.layoutManager = LinearLayoutManager(it)
        }
        binding.recyclerView.adapter = adapter

        var refreshItem = {
            Handler().postDelayed({
                var list: List<SampleListItemView> = arrayListOf(
                        SampleListItemView("hoge1"),
                        SampleListItemView("hoge2"),
                        SampleListItemView("hoge3"),
                        SampleListItemView("hoge4"),
                        SampleListItemView("hoge5"),
                        SampleListItemView("hoge6"),
                        SampleListItemView("hoge7"),
                        SampleListItemView("hoge8"),
                        SampleListItemView("hoge9")
                )
                adapter.collections = list
                binding.swipeRefresh.isRefreshing = false
            }, 1000)
        }

        binding.swipeRefresh.setOnRefreshListener {
            refreshItem()
        }

        binding.swipeRefresh.addGlobalLayoutOnce {
            binding.swipeRefresh.isRefreshing = true
            refreshItem()
        }
    }

}