package com.example.calometer.ui

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.calometer.R
import com.example.calometer.databinding.ActivityCalometerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalometerActivity : AppCompatActivity() {
    private val navController by lazy { findNavController(R.id.calometerNavHostFragment) }
    private val appBarConfiguration by lazy { AppBarConfiguration(setOf(R.id.homeFragment,R.id.diaryFragment,R.id.weightFragment, R.id.userProfileFragment, R.id.loginFragment)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Calometer)


        val binding=DataBindingUtil.setContentView<ActivityCalometerBinding>(this,R.layout.activity_calometer)


        val bottomNavigationView=binding.bottomNav

        bottomNavigationView.setupWithNavController(navController)

        setupActionBarWithNavController(navController, appBarConfiguration)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.primaryColor)))

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.homeFragment -> bottomNavigationView.visibility = View.VISIBLE
                R.id.diaryFragment -> bottomNavigationView.visibility = View.VISIBLE
                R.id.weightFragment -> bottomNavigationView.visibility = View.VISIBLE
                R.id.userProfileFragment -> bottomNavigationView.visibility= View.VISIBLE
                else ->{
                    bottomNavigationView.visibility=View.GONE
                }
            }
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



}

