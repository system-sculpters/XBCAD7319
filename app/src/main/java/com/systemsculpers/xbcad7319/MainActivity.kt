package com.systemsculpers.xbcad7319

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.systemsculpers.xbcad7319.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById<DrawerLayout>(R.id.main)

        setupBottomNavigation()
    }
    private fun setupBottomNavigation() {

        //changeCurrentFragment(HomeFragment())

        // Code for when a different button is pressed on the navigation menu

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {

                }//changeCurrentFragment(/*HomeFragment())
                R.id.transactions -> {

                }//changeCurrentFragment(GeneratePinFragment())
                R.id.settings ->{

                } //changeCurrentFragment(SettingsFragment())
            }
            true
        }
    }
    private fun changeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }
    }
}