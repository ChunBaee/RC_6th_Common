package com.jcorp.rc_mission_6

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jcorp.rc_mission_6.databinding.ItemDiceframeBinding

class DiceAdapter(context: Context) : RecyclerView.Adapter<DiceAdapter.DiceViewHolder>() {
    private var DiceList = mutableListOf<DiceData>()
    private lateinit var longClickListener : LongClickListener
    val mContext = context

    interface LongClickListener {
        fun onLongClick (view : View, position : Int)
    }
    fun longClickListener (longClickListener : LongClickListener) {
        this.longClickListener = longClickListener
    }

    inner class DiceViewHolder(val binding: ItemDiceframeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiceData) {

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_diceframe,
            parent,
            false
        )
        return DiceViewHolder(ItemDiceframeBinding.bind(view))
    }

    override fun onBindViewHolder(holder: DiceViewHolder, position: Int) {
        holder.bind(DiceList[position])
        val mItem = DiceList[position]
        holder.itemView.setOnLongClickListener {
            longClickListener.onLongClick(it, position)
            true
        }
        when (mItem.Type) {
            "Fire" -> {
                Glide.with(mContext).load(Dices.Fire[mItem.level]).into(holder.binding.Frame)

            }
            "Ice" -> {
                Glide.with(mContext).load(Dices.Ice[mItem.level]).into(holder.binding.Frame)
            }
            "Electric" -> {
                Glide.with(mContext).load(Dices.Electric[mItem.level]).into(holder.binding.Frame)
            }
            "Heal" -> {
                Glide.with(mContext).load(Dices.Heal[mItem.level]).into(holder.binding.Frame)
            }
            else -> {
                Glide.with(mContext).load(R.drawable.normal_effect).into(holder.binding.Frame)
            }
        }

    }

    override fun getItemCount(): Int {
        return DiceList.size
    }

    fun setItem(list: MutableList<DiceData>) {
        DiceList = list
    }
}