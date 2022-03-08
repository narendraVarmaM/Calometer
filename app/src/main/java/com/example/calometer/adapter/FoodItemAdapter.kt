package com.example.calometer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calometer.models.entity.FoodConsumed
import com.example.calometer.databinding.FoodListItemBinding

class FoodItemAdapter(private val foodList:List<FoodConsumed>):RecyclerView.Adapter<FoodItemAdapter.ViewHolder>() {

    class ViewHolder(val binding: FoodListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FoodListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current=foodList[position]
        holder.binding.apply{
            tvFoodName.text=current.foodName
            tvQuantity.text="Qty: "+current.quantity.toString()

            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(current)
                }
            }

        }
    }

    private var onItemClickListener: ((FoodConsumed) -> Unit)? = null

    fun setOnItemClickListener(listener:(FoodConsumed) -> Unit){
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return foodList.size
    }
}