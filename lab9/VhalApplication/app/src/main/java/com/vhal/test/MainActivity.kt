package com.vhal.test

import android.car.Car
import android.car.hardware.property.CarPropertyManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Integer
import android.app.Application
import android.car.hardware.CarPropertyValue
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val VENDOR_EXTENSION_LED_CONTROL_PROPERTY = 0x21400104
    private val VENDOR_EXTENSION_I2C_CONTROL_INT_PROPERTY = 0x21400105
    private val areaID = 0

    private lateinit var car: Car
    private lateinit var carPropertyManager: CarPropertyManager

    private lateinit var seekBar: SeekBar
    private lateinit var resultTextView: TextView
    private var isRunning = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnOn = findViewById<Button>(R.id.btnOn)
        val btnOff = findViewById<Button>(R.id.btnOff)
        seekBar = findViewById<SeekBar>(R.id.seekBar)
        resultTextView=  findViewById(R.id.resultTextView)
        btnOff.setOnClickListener {
            setLedState(Integer(0))
        }
        btnOn.setOnClickListener {
            setLedState(Integer(1))
        }
        car = Car.createCar(this.applicationContext)
        if (car == null) {
            Log.e("Steering", "Failed to create Car instance")
        } else {
            carPropertyManager = car!!.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
            Log.e("Steering", "CarPropertyManager initialized successfully")
        }
        startI2cValueUpdater()

    }

    fun setLedState(ledState:Integer) {
        try {
            synchronized(carPropertyManager) {

                carPropertyManager.setProperty(Integer::class.java, VENDOR_EXTENSION_LED_CONTROL_PROPERTY, areaID,ledState)
                Log.d("LedState", "led State changed to: ${ledState}")
            }

        } catch (e: Exception) {
            Log.e("LedState", "Error set Led State", e)
        }
    }

    private fun startI2cValueUpdater() {
        Thread {
            while (isRunning) {
                try {
                    val propertyValue: CarPropertyValue<Integer>? = carPropertyManager.getProperty(
                        Integer::class.java, VENDOR_EXTENSION_I2C_CONTROL_INT_PROPERTY, 0
                    )
                    val value = propertyValue?.getValue()?.toInt() ?: -1
                    val mapped = (value * 100) / 66

                    runOnUiThread {
                        seekBar.setProgress(mapped)
                        resultTextView.text = "Mapped Value: $mapped"
                    }
                } catch (e: Exception) {
                    Log.e("I2C", "Error reading I2C property", e)
                }

                Thread.sleep(500)
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }
}
