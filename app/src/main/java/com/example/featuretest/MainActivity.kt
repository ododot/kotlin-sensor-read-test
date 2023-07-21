package com.example.featuretest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MainActivity : ComponentActivity(), SensorEventListener, LocationListener {
    // Sensors
    private lateinit var sensorManager: SensorManager
    private var mAcc: Sensor? = null
    private var mGyro: Sensor? = null
    private var mLinAcc: Sensor? = null

    // User Location
    private var mLocationManager: LocationManager? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 2

    private var rlMap: RelativeLayout? = null
    private var mapView: MapView? = null

    //Btn
    private var btnGetLoc: Button? = null

    // TextViews
    private var tvAccX: TextView? = null
    private var tvAccY: TextView? = null
    private var tvAccZ: TextView? = null
    private var tvGyroX: TextView? = null
    private var tvGyroY: TextView? = null
    private var tvGyroZ: TextView? = null
    private var tvLinAccX: TextView? = null
    private var tvLinAccY: TextView? = null
    private var tvLinAccZ: TextView? = null
    private var tvLoc: TextView? = null

    // Values
    private var acc = FloatArray(3)
    private var gyro = FloatArray(3)
    private var linAcc = FloatArray(3)
    private var loc = DoubleArray(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.monitor)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnGetLoc = findViewById<Button>(R.id.getLoc)
        rlMap = findViewById<RelativeLayout>(R.id.clKakaoMapView)

        tvAccX = findViewById<TextView>(R.id.accX)
        tvAccY = findViewById<TextView>(R.id.accY)
        tvAccZ = findViewById<TextView>(R.id.accZ)

        tvGyroX = findViewById<TextView>(R.id.gyroX)
        tvGyroY = findViewById<TextView>(R.id.gyroY)
        tvGyroZ = findViewById<TextView>(R.id.gyroZ)

        tvLinAccX = findViewById<TextView>(R.id.linAccX)
        tvLinAccY = findViewById<TextView>(R.id.linAccY)
        tvLinAccZ = findViewById<TextView>(R.id.linAccZ)

        tvLoc = findViewById<TextView>(R.id.loc)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAcc = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyro = sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mLinAcc = sensorManager!!.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        btnGetLoc!!.setOnClickListener {
            getLocation()
        }

        mapView = MapView(this)
        rlMap?.addView(mapView)
    }

    override fun onResume() {
        super.onResume()
        if (mAcc != null) {
            mAcc.also { acc ->
                sensorManager!!.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        if (mGyro != null) {
            mGyro.also { gyro ->
                sensorManager!!.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
        if (mLinAcc != null) {
            mLinAcc.also { linAcc ->
            sensorManager!!.registerListener(this, linAcc, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Get sensors data when values changed
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                acc = event.values
                tvAccX!!.text = acc[0].toString()
                tvAccY!!.text = acc[1].toString()
                tvAccZ!!.text = acc[2].toString()
            }

            Sensor.TYPE_GYROSCOPE -> {
                gyro = event.values
                tvGyroX!!.text = gyro[0].toString()
                tvGyroY!!.text = gyro[1].toString()
                tvGyroZ!!.text = gyro[2].toString()
            }

            Sensor.TYPE_LINEAR_ACCELERATION -> {
                linAcc = event.values
                tvLinAccX!!.text = linAcc[0].toString()
                tvLinAccY!!.text = linAcc[1].toString()
                tvLinAccZ!!.text = linAcc[2].toString()
            }

            else -> {}
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    private fun getLocation() {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        loc[0] = location.latitude
        loc[1] = location.longitude
        tvLoc?.text = "Latitude: " + loc[0] + "\nLongitude: " + loc[1]
        mapView?.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(loc[0], loc[1]), true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("Permission Granted")
            }
            else {
                println("Permission Denied")
            }
        }
    }
}