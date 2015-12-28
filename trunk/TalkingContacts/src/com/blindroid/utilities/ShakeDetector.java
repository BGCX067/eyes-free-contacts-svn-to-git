/*
 * Copyright (C) Copyright (C) 2010 Project Blindroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Enables the system to detect when the user shakes their phone and then return
 * that information to the parent view
 * 
 * Code modified only slightly from Google's Talking Dialer
 */
package com.blindroid.utilities;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeDetector {
	public interface ShakeListener {
		public void onShakeDetected();
	}

	private final String TAG = "ShakeDetector";
	
	private SensorManager mSensorManager;
	private SensorEventListener mSensorListener;
	private ShakeListener mCallback;
	
	private final double DELETION_FORCE = .8;
	private final int DELETION_COUNT = 2;
	private final int SHAKE_TIMEOUT = 500;
	
	private int mShakeCount;
	private boolean mLastShakePositive = false;

	/*
	 * Initiates the shake detector and creates the thread that slows down the detection 
	 * operation.
	 */
	public ShakeDetector(Context context, ShakeListener callback) {
		Log.d(TAG, "Initiated");
		mCallback = callback;
		mSensorListener = new SensorEventListener() {
			
			public void onSensorChanged(SensorEvent event) {
				if((event.values[1] > DELETION_FORCE) && !mLastShakePositive) {
					(new Thread(new _resetShakeCount())).start();
					mShakeCount++;
					mLastShakePositive = true;
				} else if((event.values[1] < -DELETION_FORCE) && mLastShakePositive) {
					(new Thread(new _resetShakeCount())).start();
					mShakeCount++;
					mLastShakePositive = false;
				}
				
				if(mShakeCount > DELETION_COUNT) {
					mShakeCount = 0;
					mCallback.onShakeDetected();
				}
			}
			
			class _resetShakeCount implements Runnable {
				public void run() {
					try {
						Thread.sleep(SHAKE_TIMEOUT);
						mShakeCount = 0;
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			public void onAccuracyChanged(Sensor sensor, int accuracy) {	}
		};
		
		//Initiates the manager which watches the accelerometer
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	//Shutsdown the listener for the shake detector
	public void shutdown() {
		mSensorManager.unregisterListener(mSensorListener);
	}

}
