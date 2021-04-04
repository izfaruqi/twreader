package com.izfaruqi.twreader

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.androidnetworking.AndroidNetworking
import com.ferfalk.simplesearchview.SimpleSearchView
import com.izfaruqi.twreader.databinding.ActivityMainBinding
import com.izfaruqi.twreader.library.LibraryFragment
import com.izfaruqi.twreader.search.SearchFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.actionBar)
        binding.viewpager.adapter = MainPagerAdapter(this)

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        if(binding.viewpager.currentItem == 0){
            super.onBackPressed()
        } else if (binding.viewpager.currentItem == 1){
            binding.viewpager.currentItem = 0
        }
    }

    private inner class MainPagerAdapter(fa: AppCompatActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            if(position == 0) {
                return LibraryFragment()
            } else if(position == 1){
                return SearchFragment()
            } else {
                return LibraryFragment()
            }
        }
    }

    fun setActionBarTitle(title: String){
        binding.actionBar.title = title
    }

}