package com.jcorp.rc_mission_6

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jcorp.rc_mission_6.databinding.ItemLoadframeBinding

class LoadAdapter : RecyclerView.Adapter<LoadAdapter.LoadViewHolder>() {
    private var loadList = mutableListOf<LoadData>()
    private lateinit var longClickListener : LongClickListener
    private lateinit var clickListener : ClickListener

    interface LongClickListener {
        fun onLongClick (view : View, position : Int)
    }
    interface ClickListener {
        fun onClick (view : View, position : Int)
    }

    fun longClickListener (longClickListener: LongClickListener) {
        this.longClickListener = longClickListener
    }
    fun clickListener (clickListener: LoadDialog) {
        this.clickListener = clickListener
    }

    inner class LoadViewHolder (val binding : ItemLoadframeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : LoadData) {
            binding.itemSaveTime.text = item.saveTime
            binding.itemBossHp.text = item.bossHealth
            binding.itemPlayerHp.text = item.playerHealth
            binding.itemCurMoney.text = item.currentMoney
            binding.itemReqMoney.text = item.requireMoney

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loadframe, parent, false)
        return LoadViewHolder(ItemLoadframeBinding.bind(view))
    }

    override fun onBindViewHolder(holder: LoadViewHolder, position: Int) {
        holder.bind(loadList[position])
        val mItem = loadList[position]

        holder.itemView.setOnClickListener {
            clickListener.onClick(it, position)
        }

        holder.itemView.setOnLongClickListener {
            longClickListener.onLongClick(it, position)
            true
        }

        holder.binding.itemSaveTime.text = mItem.saveTime
        holder.binding.itemBossHp.text = mItem.bossHealth
        holder.binding.itemPlayerHp.text = mItem.playerHealth
        holder.binding.itemReqMoney.text = mItem.requireMoney
        holder.binding.itemCurMoney.text = mItem.currentMoney

    }

    override fun getItemCount(): Int {
        return loadList.size
    }

    fun setItem (list : MutableList<LoadData>) {
        loadList = list
    }
}