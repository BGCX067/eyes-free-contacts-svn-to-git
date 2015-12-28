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

/* This class is a data structure that allows us to access contact information in one 
 * constant manner across the program
 * 
 * Class modified from Google.
 * Located @
 * http://developer.android.com/resources/samples/BusinessCard/src/com/example/android/businesscard/ContactInfo.html
 */
package com.blindroid.talkingcontacts;

public class ContactInfo {
	private String mDisplayName;
	private String mPhoneNumber;
	private String mPhoneType;
	private String mPhone_ID;
	private String _ID;
	private int mIndex;
	
	public ContactInfo() {	}
	
	public ContactInfo(String name, int index) {
		mDisplayName = name;
		mIndex = index;
	}
	
	public void setDisplayName(String displayName) {
	    this.mDisplayName = displayName;
	}	
	public String getDisplayName() {
	    return mDisplayName;
	}
	
	public void setPhoneNumber(String phoneNumber) {
	    this.mPhoneNumber = phoneNumber;
	}	
	public String getPhoneNumber() {
	    return mPhoneNumber;
	}
	

	public void setPhoneType(String type) {
		mPhoneType = type;
	}
	public String getPhoneType() {
		return mPhoneType;
	}

	public void setIndex(int index) {
		mIndex = index;
	}
	public int getIndex() {
		return mIndex;
	}

	public void set_ID(String _ID) {
		this._ID = _ID;
	}
	public String get_ID() {
		return _ID;
	}

	public void setPhone_ID(String phone_ID) {
		mPhone_ID = phone_ID;
	}
	public String getPhone_ID() {
		return mPhone_ID;
	}
}
