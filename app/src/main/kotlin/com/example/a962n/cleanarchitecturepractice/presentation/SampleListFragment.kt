package com.example.a962n.cleanarchitecturepractice.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a962n.cleanarchitecturepractice.R
import com.example.a962n.cleanarchitecturepractice.databinding.FragmentSampleListBinding
import com.example.a962n.domain.useCase.sample.AsyncGetSampleList
import com.example.a962n.cleanarchitecturepractice.extension.observe
import com.example.a962n.cleanarchitecturepractice.util.pagedlist.DataSourceState

class SampleListFragment : Fragment() {

    private var adapter = SampleListAdapter()
    private lateinit var viewModel: SampleListViewModel
    private lateinit var binding: FragmentSampleListBinding

    private inner class Factory
    constructor(private val sampleListUseCases: SampleListUseCases) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass == SampleListViewModel::class.java)
                return SampleListViewModel(sampleListUseCases) as T

            throw IllegalArgumentException("Unknown ViewModel class : ${modelClass.name}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let {
            initialize(it)
        }
    }

    private fun initialize(context: Context) {
        var networkHandler = com.example.a962n.data.NetworkHandler(context)
        var repository = com.example.a962n.data.repositoryImpl.SampleListNetworkDummy(networkHandler)
        var useCase = AsyncGetSampleList(repository)
        var useCases = SampleListUseCases(useCase)
        var factory = Factory(useCases)

        viewModel = ViewModelProviders.of(this, factory).get(SampleListViewModel::class.java)
        observe(viewModel.dataSourceState) {
            binding.swipeRefresh.isRefreshing = it == DataSourceState.LoadingInit
            adapter.setState(it)
        }
        adapter.setRetryListener {
            viewModel.retry()
        }
        viewModel.failure(this) {

        }
        viewModel.success(this) {

        }
        observe(viewModel.pagedList) { pagedList ->
            adapter.submitList(pagedList)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentSampleListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sample_list, container, false)
        initializeView(binding)
        return binding.swipeRefresh
    }

    private fun initializeView(binding: FragmentSampleListBinding) {
        this.binding = binding
        context?.let {
            binding.recyclerView.layoutManager = LinearLayoutManager(it)
        }
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

}