package com.example.a962n.cleanarchitecturepractice

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.a962n.cleanarchitecturepractice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.buttonSampleList.setOnClickListener {
            startActivity(Intent(this@MainActivity, SampleListActivity::class.java))
        }
    }
}
