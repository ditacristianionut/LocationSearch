package com.dci.dev.locationsearchsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dci.dev.locationsearch.ui.LocationSearchFragment
import com.dci.dev.locationsearchsample.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val locationSearchFragment = LocationSearchFragment.newInstance().apply {
            onLocationSelectedCallback = {
                Toast.makeText(applicationContext, it.niceName, Toast.LENGTH_SHORT).show()
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, locationSearchFragment)
                .commitNow()
        }
    }
}