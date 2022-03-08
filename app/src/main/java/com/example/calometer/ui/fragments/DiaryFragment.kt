package com.example.calometer.ui.fragments


import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calometer.adapter.MealTypeAdapter
import com.example.calometer.models.entity.FoodData
import com.example.calometer.databinding.FragmentDiaryBinding
import com.example.calometer.models.enums.MealType
import com.example.calometer.ui.viewmodels.FoodDetailsViewModel
import com.example.calometer.util.Util
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DiaryFragment : Fragment() {

    private lateinit var viewModel:FoodDetailsViewModel
    private lateinit var binding:FragmentDiaryBinding
    private lateinit var mealTypeAdapter: MealTypeAdapter
    private lateinit var pieChart: PieChart

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar!!.show()


        binding= FragmentDiaryBinding.inflate(inflater,container,false)



        viewModel = ViewModelProvider(requireActivity()).get(FoodDetailsViewModel::class.java)


        hideSummary()

        pieChart=binding.chart2

        mealTypeAdapter=MealTypeAdapter(MealType.values(), emptyList())

        mealTypeAdapter.setOnItemClickListener {
            findNavController().navigate(DiaryFragmentDirections.actionDiaryFragmentToSearchFoodFragment(it,
                viewModel.dateSelected.value!!.toLong()))
        }


        viewModel.apply {
            dateSelected.observe(viewLifecycleOwner, {
            it?.let {
                binding.tvSelectedDate.text = Util.convertUTCToDate(it)
                applyChanges(it)
            }
            })
            foodData.observe(viewLifecycleOwner,{
                it?.let{ listFoodData ->
                    showSummary(listFoodData)
                }
            })
            totalCalories.observe(viewLifecycleOwner,{
                it?.let{ totalCalories->
                    binding.apply {

                        val totalGoal=viewModel.sessionManager.getCalorieGoal()
                        tvCaloriesConsumedValue.text=totalCalories.toString()
                        tvCaloriesRemainingValue.text=(totalGoal - totalCalories).toString()

                        tvCaloriesConsumedValueS.text=totalCalories.toString()
                        tvCaloriesRemainingValueS.text=(totalGoal - totalCalories).toString()

                        val percentage=(totalCalories*100/totalGoal)

                        tvRdiPercentage.text="$percentage% of RDI "
                        tvTotalGoal.text=totalGoal.toString()

                    }
                }
            })
        }

        binding.apply {
            ivEditCalendarIcon.setOnClickListener{
                viewModel.datePicker.show(parentFragmentManager, "tag")

                viewModel.datePicker.addOnPositiveButtonClickListener  {
                    viewModel.setDateSelected(viewModel.datePicker.selection!!.toLong())
                }
            }

            rvMealType.apply {
                adapter = mealTypeAdapter
                rvMealType.layoutManager = LinearLayoutManager(activity)
            }
        }
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun applyChanges(date: Long) {

        viewModel.apply {
            getFoodConsumed(date).observe(viewLifecycleOwner, { listFood ->
                getTotalCaloriesConsumed(listFood)
                if(listFood.isEmpty()){
                    binding.llSummary.visibility=View.GONE
                }
                getFoodDetailsById(listFood)
                mealTypeAdapter.foodConsumed = listFood
                mealTypeAdapter.notifyDataSetChanged()
            })
        }
        binding.apply {
            rvMealType.apply {
                adapter=mealTypeAdapter
                rvMealType.layoutManager=LinearLayoutManager(activity)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun showSummary(listFoodData: List<FoodData>) {
        binding.apply {
            if (listFoodData.isNotEmpty()) {
                llSummary.visibility = View.VISIBLE
                val carbohydrate = listFoodData.sumOf { foodData -> foodData.carbohydrate }
                val fat = listFoodData.sumOf { foodData -> foodData.fat }
                val cholesterol = listFoodData.sumOf { foodData -> foodData.cholesterol ?: 0.0 }
                val sodium = listFoodData.sumOf { foodData -> foodData.sodium ?: 0.0 }
                val fiber = listFoodData.sumOf { foodData -> foodData.fiber ?: 0.0 }
                val sugar = listFoodData.sumOf { foodData -> foodData.sugars ?: 0.0 }
                val protein = listFoodData.sumOf { foodData -> foodData.protein }

                setPieChart()
                loadPieChartDetails(carbohydrate, fat, protein)

                tvTotalFat.text = "Total Fat: ${Util.roundOffDecimal(fat)} g"
                tvCholesterol.text = "Cholesterol: ${Util.roundOffDecimal(cholesterol)} mg"
                tvSodium.text = "Sodium: ${Util.roundOffDecimal(sodium)} mg"
                tvTotalCarbohydrate.text =
                    "Total Carbohydrate: ${Util.roundOffDecimal(carbohydrate)} g"
                tvDietaryFiber.text = "Dietary Fiber: ${Util.roundOffDecimal(fiber)} g"
                tvSugars.text = "Sugars: ${Util.roundOffDecimal(sugar)} g"
                tvProtein.text = "Protein: ${Util.roundOffDecimal(protein)} g"
            }
        }
    }

    private fun setPieChart(){
        pieChart.isDrawHoleEnabled = true

        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled=false

        val legend=pieChart.legend


        when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                pieChart.setHoleColor(Color.WHITE)
                legend.textColor=Color.BLACK
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                pieChart.setHoleColor(Color.BLACK)
                legend.textColor=Color.WHITE
            }
        }

        legend.verticalAlignment=Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment=Legend.LegendHorizontalAlignment.LEFT
        legend.orientation=Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.isEnabled=true


    }

    private fun loadPieChartDetails(carbohydrate: Double, fat: Double, protein: Double) {

        val entries:ArrayList<PieEntry> = ArrayList()

        val total=carbohydrate+fat+protein

        val carbohydratePercentage :Float = ((carbohydrate/total)*100).toFloat()

        val fatPercentage :Float = ((fat/total)*100).toFloat()

        val proteinPercentage :Float = ((protein/total)*100).toFloat()

        entries.add(PieEntry(carbohydratePercentage, "Carbs"))
        entries.add(PieEntry(fatPercentage, "fat"))
        entries.add(PieEntry(proteinPercentage, "protein"))

        val colors:ArrayList<Int> = ArrayList()

        for(color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }

        for(color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        val dataSet = PieDataSet(entries,"Macros")
        dataSet.colors=colors
        val data = PieData(dataSet)

        data.setDrawValues(false)

        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(10f)

        pieChart.data=data

        pieChart.invalidate()
    }

    private fun hideSummary() {
        binding.apply {
            llSummary.visibility=View.GONE
        }
    }
}
