package com.fakegpspro.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.fakegpspro.app.MainActivity
import com.fakegpspro.app.R
import kotlinx.coroutines.*

class MockLocationService : Service() {

    private var serviceJob: Job? = null
    private var locationManager: LocationManager? = null
    private var isRunning = false
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "MockLocationChannel"
        const val ACTION_STOP = "STOP_MOCK_LOCATION"
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopMockLocation()
                return START_NOT_STICKY
            }
            else -> {
                val latitude = intent?.getDoubleExtra("latitude", 0.0) ?: 0.0
                val longitude = intent?.getDoubleExtra("longitude", 0.0) ?: 0.0
                
                if (latitude != 0.0 && longitude != 0.0) {
                    startMockLocation(latitude, longitude)
                }
            }
        }
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "خدمة محاكاة الموقع",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات خدمة محاكاة الموقع"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, MockLocationService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("محاكاة الموقع نشطة")
            .setContentText("الموقع: ${String.format("%.6f", currentLatitude)}, ${String.format("%.6f", currentLongitude)}")
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(mainPendingIntent)
            .addAction(R.drawable.ic_stop, "إيقاف", stopPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    private fun startMockLocation(latitude: Double, longitude: Double) {
        if (isRunning) {
            stopMockLocation()
        }

        currentLatitude = latitude
        currentLongitude = longitude
        isRunning = true

        startForeground(NOTIFICATION_ID, createNotification())

        serviceJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // إعداد الموقع الوهمي
                setupMockLocation()
                
                while (isRunning && isActive) {
                    setMockLocation(currentLatitude, currentLongitude)
                    
                    // تحديث الإشعار
                    withContext(Dispatchers.Main) {
                        val notificationManager = getSystemService(NotificationManager::class.java)
                        notificationManager.notify(NOTIFICATION_ID, createNotification())
                    }
                    
                    delay(1000) // تحديث كل ثانية
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupMockLocation() {
        try {
            locationManager?.addTestProvider(
                LocationManager.GPS_PROVIDER,
                false, false, false, false, true, true, true,
                android.location.Criteria.POWER_LOW,
                android.location.Criteria.ACCURACY_FINE
            )
            locationManager?.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setMockLocation(latitude: Double, longitude: Double) {
        try {
            val location = Location(LocationManager.GPS_PROVIDER).apply {
                this.latitude = latitude
                this.longitude = longitude
                altitude = 0.0
                accuracy = 1.0f
                time = System.currentTimeMillis()
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bearingAccuracyDegrees = 1.0f
                    verticalAccuracyMeters = 1.0f
                    speedAccuracyMetersPerSecond = 1.0f
                }
            }
            
            locationManager?.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopMockLocation() {
        isRunning = false
        serviceJob?.cancel()
        
        try {
            locationManager?.removeTestProvider(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMockLocation()
    }
}

