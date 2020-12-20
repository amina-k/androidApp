package com.strathmore.ads.fruitMartApp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.strathmore.ads.fruitMartApp.R
import com.strathmore.ads.fruitMartApp.fragments.ItemsFragment
import com.strathmore.ads.fruitMartApp.fragments.OrdersFragment
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            val defaultFragment = ItemsFragment()
            openFragment(defaultFragment)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit app", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { process ->
        when (process.itemId) {
            R.id.navigation_orders -> {
                val ordersFragment = OrdersFragment.newInstance()
                openFragment(ordersFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_prices -> {

                val productsFragment = ItemsFragment.newInstance()
                openFragment(productsFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_add_item -> {
                val dialogBuilder = AlertDialog.Builder(this)

                dialogBuilder.setMessage("What would you like to add?")
                    .setCancelable(true)
                    .setPositiveButton("Item", DialogInterface.OnClickListener { _, _ ->
                        startActivity(Intent(this, ItemActivity::class.java))

                    })
                    .setNegativeButton("Order", DialogInterface.OnClickListener { _, _ ->
                        startActivity(Intent(this, OrderActivity::class.java))
                    })

                val alert = dialogBuilder.create()
                alert.setTitle("FruitMart")
                alert.show()
            }
        }
        false
    }


    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
