package com.strathmore.ads.fruitMartApp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.strathmore.ads.fruitMartApp.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
            {
                //Code to be executed after timer runs out
                startActivity(Intent(this, DashboardActivity::class.java))
            }, 3000
        )
    }
}
