/*
 * Copyright (C) 2010 Project Blindroid
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

/* This is the framework for allowing a phone whose version of android uses the new version of contacts
 * (in ecliar or newer) or the older one.
 * We currently only have the older verion, the people api, implemented but the framework is there for
 * the new one to be added.
 * 
 * Class only slighlty modified from Google.
 * Located @
 * http://developer.android.com/resources/samples/BusinessCard/src/com/example/android/businesscard/ContactAccessor.html
 */
package com.blindroid.contacts;

import java.util.ArrayList;

import com.blindroid.talkingcontacts.ContactInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

public abstract class ContactsAdapter {
	private static ContactsAdapter sInstance;
	protected Context context;
	
	/*
	 * Singleton access point for the ContactsAdapter object
	 * TODO Fully implement the eclair version of the API
	 */
	public static ContactsAdapter getInstance() {
		if(sInstance == null) {
			String className;
			
			//TODO eclair compare
			int sdkVersion = Build.VERSION.SDK_INT;
			className = "com.blindroid.contacts.ContactsAdapterSdk3_4";
			
			//Find required class by name and instantiate it
			try {
				Class<? extends ContactsAdapter> clazz =
					Class.forName(className).asSubclass(ContactsAdapter.class);
				sInstance = clazz.newInstance();
			} catch (Exception e) {
                throw new IllegalStateException(e);
            }
		}
		return sInstance;
	}
	
	/* Abstract method to be overridden by subclass APIs */
	public abstract void loadContacts(ContentResolver contentResolver);
	/* Abstract method to be overridden by subclass APIs */
	public abstract ArrayList<String> getNames();
	/* Abstract method to be overridden by subclass APIs */
	public abstract ContactInfo getContactInfo(int index);
	/* Abstract method to be overridden by subclass APIs */
	public abstract boolean addContact(ContentResolver contentResolver, ContactInfo cInfo);
	/* Abstract method to be overridden by subclass APIs */
	public abstract boolean editContact(ContentResolver contentResolver, ContactInfo cInfo);
	/* Abstract method to be overridden by subclass APIs */
	public abstract boolean deleteContact(ContentResolver contentResolver, ContactInfo cInfo);
}