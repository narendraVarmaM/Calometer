package com.example.calometer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.calometer.R
import com.example.calometer.models.entity.FoodConsumed
import com.example.calometer.databinding.MealTypeListItemBinding
import com.example.calometer.models.enums.MealType
import com.example.calometer.ui.fragments.DiaryFragmentDirections

class MealTypeAdapter(private val mealTypes:Array<MealType>,  var foodConsumed: List<FoodConsumed>):RecyclerView.Adapter<MealTypeAdapter.ViewHolder>() {

    class ViewHolder(val binding: MealTypeListItemBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MealTypeListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current=mealTypes[position]
        holder.binding.apply{

            val foodList=foodConsumed.filter { it.mealType==current.name }
            tvTotalCalorie.text= (foodList.map { it.calories }.sum()).toString()
            tvMealType.text=current.name
            when(current){
                MealType.Breakfast ->ivMealIcon.setImageResource(R.drawable.ic_breakfast_24)
                MealType.Lunch -> ivMealIcon.setImageResource(R.drawable.ic_lunch_24)
                MealType.Dinner -> ivMealIcon.setImageResource(R.drawable.ic_snacks_24)
                MealType.Snacks -> ivMealIcon.setImageResource(R.drawable.ic_dinner_24)
            }

            Log.i(" child recyclerview","${foodList.size} is the meal size")
            var foodItemAdapter= FoodItemAdapter(foodList)
                //FoodItemAdapter(FoodItem.values().map { it.name })
            this.rvFoodItem.apply {
                adapter=foodItemAdapter

            }
            foodItemAdapter.setOnItemClickListener {
                holder.itemView.findNavController().navigate(
                DiaryFragmentDirections.actionDiaryFragmentToEditFoodItemFragment(it))}
            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(current)
                }
            }

        }
    }
    private var onItemClickListener: ((MealType) -> Unit)? = null

    fun setOnItemClickListener(listener:(MealType) -> Unit){
        onItemClickListener = listener
    }
    override fun getItemCount(): Int {
        Log.i("in recyclerview","${mealTypes.size} is the size")
       return mealTypes.size
    }
}