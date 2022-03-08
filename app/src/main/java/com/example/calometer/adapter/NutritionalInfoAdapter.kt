package com.example.calometer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calometer.databinding.ActivityCalometerBinding.inflate
import com.example.calometer.databinding.FoodNutritionListItemBinding
import com.example.calometer.databinding.MealTypeListItemBinding
import com.example.calometer.util.Util

class NutritionalInfoAdapter(
    private val nutrients:List<String>,
    private val values:List<String>
    ):RecyclerView.Adapter<NutritionalInfoAdapter.ViewHolder>() {

    class ViewHolder(val binding:FoodNutritionListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FoodNutritionListItemBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current=nutrients[position]
//        Log.i("in nutrition","$current")
        holder.binding.apply {
            tvNutrient.text=current
            tvNutrientValue.text= values[position]
        }
    }

    override fun getItemCount(): Int {
        return nutrients.size
    }


}