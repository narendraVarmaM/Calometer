package com.example.calometer.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calometer.adapter.SearchFoodItemAdapter
import com.example.calometer.databinding.FragmentSearchFoodBinding
import com.example.calometer.ui.viewmodels.FoodDetailsViewModel
import com.example.calometer.util.Constants.Companion.SEARCH_TIME_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFoodFragment : Fragment() {
    private lateinit var viewModel:FoodDetailsViewModel
    private lateinit var searchFoodAdapter: SearchFoodItemAdapter
    lateinit var binding: FragmentSearchFoodBinding
    private lateinit var arguments:SearchFoodFragmentArgs
    lateinit var editText:EditText


    private var job: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments=SearchFoodFragmentArgs.fromBundle(requireArguments())
        // Inflate the layout for this fragment


        binding = FragmentSearchFoodBinding.inflate(inflater, container, false)

        viewModel=ViewModelProvider(requireActivity()).get(FoodDetailsViewModel::class.java)

        searchFoodAdapter = SearchFoodItemAdapter()
        arguments=SearchFoodFragmentArgs.fromBundle(requireArguments())

        editText=binding.etSearchBar



        editText.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                editText.hideKeyboard()
            }
        }

        binding.apply {
            editText.addTextChangedListener{ editable ->
                job?.cancel()

                job = MainScope().launch {
                    delay(SEARCH_TIME_DELAY)
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            progressHorizontal.visibility = VISIBLE
                            if (view != null) {
                                viewModel.getSearchResult(editable.toString())
                                    .observe(viewLifecycleOwner) { searchList ->
                                        showProgressBar()
                                        if (searchList.isEmpty()) {
                                            showNoResultFound()
                                        } else {
                                            hideProgressBar()
                                            hideNoResultFound()
                                            searchFoodAdapter.differ.submitList(searchList)
                                        }
                                    }
                            }
                        }
                    }
                }
            }

            rvSearch.apply {
                adapter = searchFoodAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }
        searchFoodAdapter.setOnItemClickListener {
            viewModel.setFoodItemId(it.foodId)
            findNavController().navigate(SearchFoodFragmentDirections.actionSearchFoodFragmentToSaveFoodItemFragment(arguments.mealType,arguments.dateSelected,it))
        }
//        viewModel.apply {
//            searchResult.observe(viewLifecycleOwner, {
//                it?.let { foodResult->
//                    searchFoodAdapter.differ.submitList(foodResult)
//                    doneShowingSearchResults()
//                }
//            })
//        }
        return binding.root
    }

//    private fun setUpRecyclerView(nutrients:List<String>, values:List<String>){
//        nutritionalInfoAdapter= NutritionalInfoAdapter(nutrients, values)
//        nutritionalInfoAdapter.notifyDataSetChanged()
//        binding.rvNutrientValues.apply {
//            adapter=nutritionalInfoAdapter
//            layoutManager=LinearLayoutManager(activity)
//        }
//    }

    override fun onResume() {
        super.onResume()
        editText.run { postDelayed({ showKeyboard() }, 50) }
    }
    private fun EditText.showKeyboard() {
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        requestFocus()
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
    private fun EditText.hideKeyboard(){
        this.clearFocus()
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    }

    private fun hideProgressBar(){
        binding.progressHorizontal.visibility = INVISIBLE
    }

    private fun showProgressBar(){
        hideNoResultFound()
        binding.apply {
            progressHorizontal.visibility = VISIBLE
            rvSearch.visibility = INVISIBLE
        }
    }
    private fun showNoResultFound(){
        hideProgressBar()
        binding.apply {
            rvSearch.visibility = INVISIBLE
            imgNotFound.visibility = VISIBLE
        }
    }

    private fun hideNoResultFound(){
        binding.apply {
            rvSearch.visibility = VISIBLE
            imgNotFound.visibility = GONE
        }
    }
    override fun onStop() {
        super.onStop()
        editText.hideKeyboard()
    }

    //binding.etSearchBar.requestFocus()
//        enableKeyboard(binding.etSearchBar)

//        editText.setOnQueryTextFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                showInputMethod(editText)
//            }
//        }

//        editText.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                editText.clearFocus()
//                job?.cancel()
//
//                job = MainScope().launch {
//                    delay(SEARCH_TIME_DELAY)
//                    query?.let {
//                        if (it.isNotEmpty()) {
//                            viewModel.getSearchResult(it).observe(viewLifecycleOwner, {searchList->
//                                searchFoodAdapter.differ.submitList(searchList)
//                            })
//                        }
//                    }
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                job?.cancel()
//
//                job = MainScope().launch {
//                    delay(SEARCH_TIME_DELAY)
//                    newText?.let {
//                        if (it.isNotEmpty()) {
//                            viewModel.getSearchResult(it).observe(viewLifecycleOwner, {searchList->
//                                searchFoodAdapter.differ.submitList(searchList)
//                            })
//                        }
//                    }
//                }
//               return false
//            }
//
//        })

    //    private fun showInputMethod(view: SearchView) {
//        val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        if(inputMethodManager!=null){
//            inputMethodManager.showSoftInput(view, 0)
//        }
//
//    }

//    private fun enableKeyboard(view: EditText) {
//        view.requestFocus()
//        Log.i("inside enable","here")
//        val imm=requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);
//        //imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
//
//        imm.showSoftInput(view.rootView, InputMethodManager.SHOW_IMPLICIT)
//
//    }

//    private fun hideKeyboard(view: EditText) {
//
//
//        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
//    }
}