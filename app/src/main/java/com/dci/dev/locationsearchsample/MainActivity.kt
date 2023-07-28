package com.dci.dev.locationsearchsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dci.dev.locationsearch.LocationSearchConfig
import com.dci.dev.locationsearch.di.DataProviderType
import com.dci.dev.locationsearch.ui.LocationSearchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LocationSearchConfig.dataProviderType = DataProviderType.LocationIq

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