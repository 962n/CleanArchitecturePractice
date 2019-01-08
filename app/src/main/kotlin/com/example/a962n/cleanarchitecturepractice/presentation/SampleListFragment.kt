package com.example.a962n.cleanarchitecturepractice.presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.a962n.cleanarchitecturepractice.NetworkHandler
import com.example.a962n.cleanarchitecturepractice.R
import com.example.a962n.cleanarchitecturepractice.data.impl.SampleListNetworkDummy
import com.example.a962n.cleanarchitecturepractice.databinding.FragmentSampleListBinding
import com.example.a962n.cleanarchitecturepractice.domain.impl.GetSampleList
import com.example.a962n.cleanarchitecturepractice.extension.addGlobalLayoutOnce
import com.example.a962n.cleanarchitecturepractice.extension.observe

class SampleListFragment : Fragment() {

    private var adapter = SampleListAdapter()
    private lateinit var viewModel: SampleListViewModel
    private lateinit var binding: FragmentSampleListBinding

    class Factory(private val sampleListUseCases: SampleListUseCases) : ViewModelProvider.Factory {
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
        var networkHandler = NetworkHandler(context)
        var repository = SampleListNetworkDummy(networkHandler)
        var useCase = GetSampleList(repository)
        var useCases = SampleListUseCases(useCase)
        var factory = Factory(useCases)

        ViewModelProviders.of(this, factory).get(SampleListViewModel::class.java).let {
            viewModel = it
            viewModel.failure(this) { failure ->
                binding.swipeRefresh.isRefreshing = false
            }
            viewModel.success(this) {success ->
                when(success){
                    is SampleListViewModel.Success.Refresh -> {
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
            observe(viewModel.list) { list ->
                list?.apply {
                    adapter.collections = this.toList()
                }
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding: FragmentSampleListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sample_list, container, false)
        initializeView(binding)
        return binding.root
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

        binding.swipeRefresh.addGlobalLayoutOnce {
            binding.swipeRefresh.isRefreshing = true
            viewModel.refresh()
        }
    }

}