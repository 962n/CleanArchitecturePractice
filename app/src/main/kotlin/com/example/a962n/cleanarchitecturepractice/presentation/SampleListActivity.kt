package com.example.a962n.cleanarchitecturepractice.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a962n.cleanarchitecturepractice.R
import com.example.a962n.domain.repository.SampleListRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class SampleListActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: SampleListRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_list)
        title = "Sample List"
        AndroidInjection.inject(this)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, SampleListFragment())
                    .commit()
        }
    }

}