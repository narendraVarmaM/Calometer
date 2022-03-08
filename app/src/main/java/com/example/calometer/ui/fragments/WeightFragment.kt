package com.example.calometer.ui.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calometer.R
import com.example.calometer.adapter.WeightDetailsAdapter
import com.example.calometer.models.entity.WeightDetails
import com.example.calometer.databinding.FragmentWeightBinding
import com.example.calometer.models.entity.User
import com.example.calometer.models.enums.Weight
import com.example.calometer.ui.viewmodels.UserProfileViewModel
import com.example.calometer.ui.viewmodels.WeightDetailsViewModel
import com.example.calometer.util.Util
import com.example.calometer.util.Util.Companion.convertUTCToDateYY
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeightFragment : Fragment() {

    lateinit var binding: FragmentWeightBinding
    private lateinit var lineChart: LineChart
    private lateinit var viewModel:WeightDetailsViewModel
    private lateinit var userViewModel:UserProfileViewModel
    private var weightList = ArrayList<Weight>()
    private lateinit var currentUser: User
    private var weightDetailsList : List<WeightDetails> = emptyList()
    private lateinit var weightDetailsAdapter: WeightDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentWeightBinding.inflate(inflater,container,false)

        viewModel=ViewModelProvider(requireActivity()).get(WeightDetailsViewModel::class.java)
        userViewModel=ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)
        lineChart=binding.chart1

        weightDetailsAdapter=WeightDetailsAdapter()


        initLineChart()

        viewModel.getWeightDetails()



        binding.apply {
            tvRecentWeight.setOnClickListener {
                findNavController().navigate(WeightFragmentDirections.actionWeightFragmentToSaveWeightDetailsFragment(tvRecentWeight.text.toString(),0))
            }
            rvWeightDetails.apply {
                adapter=weightDetailsAdapter
                layoutManager=LinearLayoutManager(activity)
            }
            tvGoalWeight.setOnClickListener {
                findNavController().navigate(WeightFragmentDirections.actionWeightFragmentToSaveWeightDetailsFragment(tvGoalWeight.text.toString(), 2))           }
            tvStartWeight.setOnClickListener {
                viewModel.setDateSelectedUTC(weightDetailsList[0].date)
                viewModel.setDateSelected(Util.convertUTCToDate(weightDetailsList[0].date))
                findNavController().navigate(WeightFragmentDirections.actionWeightFragmentToSaveWeightDetailsFragment(tvStartWeight.text.toString(),1))
            }
            ivAddIcon.setOnClickListener {
                findNavController().navigate(WeightFragmentDirections.actionWeightFragmentToSaveWeightDetailsFragment(tvRecentWeight.text.toString(), 0))
            }
        }



        viewModel.apply {
            getWeightDetails().observe(viewLifecycleOwner) { resultList ->
                resultList?.let {
                    if (resultList.isNotEmpty()) {
                        resultList.sortedBy { weightDetails: WeightDetails -> weightDetails.date }
                            .forEach { weightDetail ->
                                weightList.add(
                                    Weight(
                                        convertUTCToDateYY(weightDetail.date), weightDetail.weight
                                    )
                                )
                            }
                        if (resultList.size == 1) {
                            weightList.add(
                                Weight(
                                    convertUTCToDateYY(resultList[0].date),
                                    resultList[0].weight
                                )
                            )
                        }
                    }
                    userViewModel.getUserDetails().observe(viewLifecycleOwner) { user ->
                        this@WeightFragment.currentUser = user.copy()
                        Log.i("in chart ", "goal : ${user.goalWeight}, ${currentUser.goalWeight}")
                        setDataToLineChart(user.goalWeight)
                        binding.apply {
                            if (user != null) {
                                tvGoalWeight.text = user.goalWeight.toString()
                            }
                        }
                    }

                    val orderedList =
                        resultList.sortedByDescending { weightDetails: WeightDetails -> weightDetails.date }
                    this@WeightFragment.weightDetailsList = orderedList
                    binding.apply {
                        tvRecentWeight.text = orderedList[0].weight.toString()
                        tvStartWeight.text = orderedList[orderedList.size - 1].weight.toString()
                    }
                    weightDetailsAdapter.differ.submitList(orderedList)
                }

            }
        }

        weightDetailsAdapter.setOnItemClickListener {
            viewModel.setWeightSelected(it)
            findNavController().navigate(WeightFragmentDirections.actionWeightFragmentToEditWeightDetailsFragment())
        }
//        //setDataToLineChart()
//
//        //setLineChartData()
        return binding.root

    }


    private fun initLineChart() {

        lineChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)


        //remove right y-axis
        lineChart.axisRight.isEnabled = false


        lineChart.xAxis.isEnabled=false

        //remove legend
        lineChart.legend.isEnabled = false


        //remove description label
        lineChart.description.isEnabled = false


        //add animation
        lineChart.animateX(1000, Easing.EaseInSine)

        // to draw label on xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.valueFormatter = MyAxisFormatter()
//        xAxis.textColor=ContextCompat.getColor(requireContext(),R.color.label_color)
//        xAxis.setDrawLabels(true)
//        xAxis.granularity = 1f
//        xAxis.labelRotationAngle = +90f


    }


    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < weightList.size) {
                weightList[index].date
            } else {
                ""
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun setDataToLineChart(goal:Double) {
        //now draw bar chart with dynamic data
        val entries: ArrayList<Entry> = ArrayList()
        //you can replace this data object with  your custom object
        var max=0.0
        var min=1000.0
        Log.i("in chart ", "$weightList")
        for (i in weightList.indices) {
            val score = weightList[i]
            if(max<score.score){
                max=score.score
            }
            if(min>score.score){
                min=score.score
            }
            entries.add(Entry(i.toFloat(), score.score.toFloat()))
        }


        val lineDataSet = LineDataSet(entries, "")

        val data = LineData(lineDataSet)
//        val goalLimitLine=LimitLine(70f,"goal")
//        goalLimitLine.apply {
//            lineWidth=1f
//            lineColor=R.color.primaryLightColor
//            enableDashedLine(10f,10f,0f)
//            textSize=10f
//        }
//        lineChart.axisLeft.removeAllLimitLines()
//        lineChart.axisLeft.addLimitLine(goalLimitLine)

        lineChart.data=data

//        val max:Float=weightList.maxOf { it.score }.toFloat()
//        val min=weightList.minOf { it.score }.toFloat()
        Log.i("in chart", "$min $max")
        Log.i("in chart","$goal")
        val miny=if(min > goal){
            goal-5
        }
        else{
            min-5
        }

        val maxy=if(max>goal){
            max+2
        }
        else{
            goal+2
        }
//        lineChart.isScrollContainer=true
//        lineChart.isHorizontalScrollBarEnabled=true
//        lineChart.isScaleXEnabled=true

        //lineChart.moveViewToX(3F)
        val yAxis:YAxis=lineChart.axisLeft


        yAxis.removeAllLimitLines()

        val upperLimit=LimitLine(goal.toFloat(),goal.toString())
        upperLimit.lineWidth=1f
        upperLimit.enableDashedLine(10f, 10f, 0f)
        upperLimit.labelPosition=LimitLine.LimitLabelPosition.RIGHT_TOP
        upperLimit.textSize=10f
        upperLimit.lineColor= Color.rgb(0,176,255)

        val lowerLimit=LimitLine(weightList[0].score.toFloat(),weightList[0].score.toString())
        lowerLimit.lineWidth=1f
        lowerLimit.enableDashedLine(10f, 10f, 0f)
        lowerLimit.labelPosition=LimitLine.LimitLabelPosition.RIGHT_TOP
        lowerLimit.textSize=10f
        lowerLimit.lineColor=Color.rgb(245,0,87)

        when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                upperLimit.textColor=Color.BLACK
                lowerLimit.textColor=Color.BLACK
                yAxis.textColor=Color.BLACK
                lineDataSet.circleHoleColor=Color.WHITE
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                upperLimit.textColor=Color.WHITE
                lowerLimit.textColor=Color.WHITE
                yAxis.textColor=Color.WHITE
                lineDataSet.circleHoleColor=Color.BLACK

            }
        }


        yAxis.addLimitLine(upperLimit)
        yAxis.addLimitLine(lowerLimit)
        yAxis.axisMaximum= maxy.toFloat()
        yAxis.axisMinimum= miny.toFloat()
        yAxis.setDrawLimitLinesBehindData(false)



        lineDataSet.apply {
            circleRadius=4f
            setDrawCircleHole(true)
            circleHoleRadius=3f
            color=ContextCompat.getColor(requireContext(),R.color.orange_200)
            fillDrawable=ContextCompat.getDrawable(requireContext(), R.drawable.bg_line_chart)
            setDrawFilled(true)
            setDrawValues(false)
        }
        lineChart.invalidate()
        //lineChart.setVisibleXRangeMaximum(4f)
    }
    // simulate api call
    // we are initialising it directly
    override fun onStop() {
        super.onStop()
        weightList.clear()
    }
}