package com.example.sadkaquran.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QiblaSensorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    fun getDeviceHeadingFlow(): Flow<Float> = callbackFlow {
        if (sensorManager == null) {
            trySend(0f)
            close()
            return@callbackFlow
        }

        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)
        var lastGravity = FloatArray(3)
        var lastGeomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val azimuthInDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    val normalizedHeading = (azimuthInDegrees + 360) % 360
                    trySend(normalizedHeading)
                } else {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        lastGravity = event.values.clone()
                        hasGravity = true
                    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        lastGeomagnetic = event.values.clone()
                        hasGeomagnetic = true
                    }

                    if (hasGravity && hasGeomagnetic) {
                        val success = SensorManager.getRotationMatrix(
                            rotationMatrix,
                            null,
                            lastGravity,
                            lastGeomagnetic
                        )
                        if (success) {
                            SensorManager.getOrientation(rotationMatrix, orientationAngles)
                            val azimuthInDegrees =
                                Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                            val normalizedHeading = (azimuthInDegrees + 360) % 360
                            trySend(normalizedHeading)
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            if (accelerometer != null) {
                sensorManager.registerListener(
                    listener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
            if (magnetometer != null) {
                sensorManager.registerListener(
                    listener,
                    magnetometer,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
