package com.example.calometer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.calometer.models.entity.FoodData
import com.example.calometer.databinding.SearchFoodListItemBinding

class SearchFoodItemAdapter(): RecyclerView.Adapter<SearchFoodItemAdapter.FoodViewHolder>() {

    private val differCallback = object: DiffUtil.ItemCallback<FoodData>(){
        override fun areItemsTheSame(
            oldItem: FoodData,
            newItem: FoodData
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FoodData, newItem: FoodData)= oldItem==newItem

    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        var foodItem = differ.currentList[position]
        holder.binding.apply {
            tvFoodName.text=foodItem.foodName
            tvFoodServing.text=foodItem.servingDescription?:"100g"
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(foodItem)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.i("adapter","${differ.currentList.size} is the size")
        return differ.currentList.size
    }

    private var onItemClickListener: ((FoodData) -> Unit)? = null

    fun setOnItemClickListener(listener:(FoodData) -> Unit){
        onItemClickListener = listener
    }

    class FoodViewHolder(val binding: SearchFoodListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup): FoodViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SearchFoodListItemBinding.inflate(layoutInflater, parent, false)
                return FoodViewHolder(binding)
            }
        }
    }


}