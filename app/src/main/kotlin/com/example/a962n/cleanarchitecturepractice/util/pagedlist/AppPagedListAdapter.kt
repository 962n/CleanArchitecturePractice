package com.example.a962n.cleanarchitecturepractice.util.pagedlist

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.a962n.cleanarchitecturepractice.R
import com.example.a962n.cleanarchitecturepractice.extension.inflater


abstract class AppPagedListAdapter<T> : PagedListAdapter<T, RecyclerView.ViewHolder> {

    protected constructor(config: AsyncDifferConfig<T>) : super(config)
    protected constructor(diffCallback: DiffUtil.ItemCallback<T>) : super(diffCallback)

    private var dataSourceState: DataSourceState? = null
    private var retryListener :(() -> Unit)? = null

    abstract fun getFeatureViewType(position:Int):Int
    abstract fun onFeatureBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    abstract fun onFeatureCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun setRetryListener(listener:(() -> Unit)){
        retryListener = listener
    }

    private fun hasExtraRow(): Boolean {
        if (dataSourceState == null) {
            return false
        }
        return dataSourceState is DataSourceState.LoadingMore || dataSourceState is DataSourceState.LoadMoreFailed
    }


    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            R.layout.adapter_datasource_state_item -> {
                return DataSourceStateItemViewHolder.create(parent, retryListener)
            }
        }
        return onFeatureCreateViewHolder(parent,viewType)
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.adapter_datasource_state_item -> {
                (holder as DataSourceStateItemViewHolder).bind(dataSourceState)
                return
            }
        }
        onFeatureBindViewHolder(holder, position)
    }

    final override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1 && hasExtraRow()){
            return R.layout.adapter_datasource_state_item
        }
        return getFeatureViewType(position)
    }

    final override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun getActualItemCount():Int {
        return itemCount - if (hasExtraRow()) 1 else 0
    }

    fun setState(state: DataSourceState?) {
        val previousState = this.dataSourceState
        val hadExtraRow = hasExtraRow()
        this.dataSourceState = state
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != dataSourceState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    /**
     * A View Holder that can display a loading or have click action.
     * It is used to show the network state of paging.
     */
    private class DataSourceStateItemViewHolder(view: View,
                                     private val retryCallback: (() -> Unit)?)
        : RecyclerView.ViewHolder(view) {
        private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        private val retry = view.findViewById<Button>(R.id.retry_button)
        private val errorMsg = view.findViewById<TextView>(R.id.error_msg)
        init {
            retry.setOnClickListener {
                retryCallback?.apply {
                    this()
                }
            }
        }
        fun bind(state: DataSourceState?) {
            progressBar.visibility = toVisibility(state is DataSourceState.LoadingMore)
            retry.visibility = toVisibility(state is DataSourceState.LoadMoreFailed)
            errorMsg.visibility = toVisibility(/*networkState?.msg != null*/false)
//            errorMsg.text = networkState?.msg
        }

        companion object {
            fun create(parent: ViewGroup, retryCallback:(() -> Unit)?): AppPagedListAdapter.DataSourceStateItemViewHolder {
                val view = parent.inflater().inflate(R.layout.adapter_datasource_state_item, parent, false)
                return DataSourceStateItemViewHolder(view, retryCallback)
            }

            fun toVisibility(constraint : Boolean): Int {
                return if (constraint) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

}