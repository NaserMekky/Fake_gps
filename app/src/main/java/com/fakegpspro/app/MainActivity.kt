package com.fakegpspro.app

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fakegpspro.app.databinding.ActivityMainBinding
import com.fakegpspro.app.fragment.HomeFragment
import com.fakegpspro.app.fragment.MapFragment
import com.fakegpspro.app.fragment.SettingsFragment
import com.fakegpspro.app.service.MockLocationService
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // التحقق من الأذونات
        checkPermissions()
        
        // إعداد التنقل السفلي
        setupBottomNavigation()
        
        // تحميل الشاشة الرئيسية
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_map -> {
                    loadFragment(MapFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            checkMockLocationSettings()
        }
    }

    private fun checkMockLocationSettings() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        try {
            // التحقق من إعدادات الموقع الوهمي
            val isEnabled = Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION,
                0
            ) != 0

            if (!isEnabled) {
                showMockLocationDialog()
            }
        } catch (e: Exception) {
            // في الإصدارات الأحدث من Android
            showDeveloperOptionsDialog()
        }
    }

    private fun showMockLocationDialog() {
        AlertDialog.Builder(this)
            .setTitle("تفعيل الموقع الوهمي")
            .setMessage("يجب تفعيل خيار الموقع الوهمي في إعدادات المطور لاستخدام هذا التطبيق.")
            .setPositiveButton("فتح الإعدادات") { _, _ ->
                try {
                    startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                } catch (e: Exception) {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun showDeveloperOptionsDialog() {
        AlertDialog.Builder(this)
            .setTitle("تفعيل خيارات المطور")
            .setMessage("يجب تفعيل خيارات المطور واختيار هذا التطبيق كتطبيق الموقع الوهمي.")
            .setPositiveButton("فتح الإعدادات") { _, _ ->
                try {
                    startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                } catch (e: Exception) {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    checkMockLocationSettings()
                } else {
                    Toast.makeText(this, "الأذونات مطلوبة لتشغيل التطبيق", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun startMockLocationService(latitude: Double, longitude: Double) {
        val intent = Intent(this, MockLocationService::class.java).apply {
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }
        startForegroundService(intent)
    }

    fun stopMockLocationService() {
        val intent = Intent(this, MockLocationService::class.java)
        stopService(intent)
    }
}

