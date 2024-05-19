package com.example.nextgen.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class BaseAdapter<T> : RecyclerView.Adapter<BaseAdapter.BaseViewHolder<T>>() {

  var itemList: MutableList<T> = mutableListOf()
    set(value) {
      field = value
      notifyDataSetChanged()
    }
    get() = field


  var expressionViewHolderBinding: ((T, ViewBinding) -> Unit)? = null
  var expressionOnCreateViewHolder: ((ViewGroup) -> ViewBinding)? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {

    return expressionOnCreateViewHolder?.let {
      it(parent).let {
        BaseViewHolder(
          it,
          expressionViewHolderBinding!!
        )
      }
    }!!
  }

  override fun getItemCount(): Int {
    return itemList.size
  }

  override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
    holder.bind(itemList[position])
  }

  fun addItems(newItems:MutableList<T>){
    itemList.addAll(newItems)
    notifyDataSetChanged()
  }

  // Base ViewHolder
  class BaseViewHolder<T> internal constructor(
    private val binding: ViewBinding,
    private val expression: (T, ViewBinding) -> Unit,
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T) {
      expression(item, binding)
    }

  }
}