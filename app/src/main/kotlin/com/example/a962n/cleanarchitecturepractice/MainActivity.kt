package com.example.a962n.cleanarchitecturepractice

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a962n.cleanarchitecturepractice.databinding.ActivityMainBinding
import com.example.a962n.cleanarchitecturepractice.presentation.SampleListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.buttonSampleList.setOnClickListener {
            startActivity(Intent(this@MainActivity, SampleListActivity::class.java))
        }
    }
}
