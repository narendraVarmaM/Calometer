package com.example.calometer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.calometer.R
import com.example.calometer.models.entity.WeightDetails
import com.example.calometer.databinding.WeightListItemBinding
import com.example.calometer.util.Util.Companion.convertUTCToDate


class WeightDetailsAdapter(): RecyclerView.Adapter<WeightDetailsAdapter.ViewHolder>()  {

    private val differCallback = object: DiffUtil.ItemCallback<WeightDetails>(){
        override fun areItemsTheSame(
            oldItem: WeightDetails,
            newItem: WeightDetails
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: WeightDetails, newItem: WeightDetails)= oldItem==newItem

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeightDetailsAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }
    val differ = AsyncListDiffer(this, differCallback)


    override fun onBindViewHolder(holder: WeightDetailsAdapter.ViewHolder, position: Int) {
        var weight=differ.currentList[position]

        holder.binding.apply {
            if(position<differ.currentList.size-1){
                when {
                    differ.currentList[position].weight - differ.currentList[position+1].weight>0 -> {
                        ivChangeIcon.setImageResource(R.drawable.ic_outline_arrow_drop_up_24)
                    }
                    differ.currentList[position].weight - differ.currentList[position+1].weight<0 -> {
                        ivChangeIcon.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                    }
                    else -> {
                        ivChangeIcon.setImageResource(R.drawable.ic_baseline_horizontal_rule_24)
                    }
                }
            }
            tvDate.text=convertUTCToDate(weight.date)
            tvWeightValue.text=weight.weight.toString()
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(weight)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.i("in adapter","${differ.currentList.size}")
        return differ.currentList.size
    }

    class ViewHolder(val binding: WeightListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = WeightListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    private var onItemClickListener: ((WeightDetails) -> Unit)? = null

    fun setOnItemClickListener(listener:(WeightDetails) -> Unit){
        onItemClickListener = listener
    }


}