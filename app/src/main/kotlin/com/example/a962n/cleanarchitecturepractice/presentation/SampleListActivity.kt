package com.example.a962n.cleanarchitecturepractice.presentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.a962n.cleanarchitecturepractice.R

class SampleListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_list)
        title = "Sample List"

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, SampleListFragment())
                    .commit()
        }
    }

}