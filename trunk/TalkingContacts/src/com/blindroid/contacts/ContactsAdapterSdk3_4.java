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

/* This enables users of SDK version 4 or lower with the People api to access their contacts
 * This code works perfectly in the emulator, but for some reason it causes problems with the
 * phone contacts. I believe there is some sort of problem with the URI syntax or the retrieval of the
 * URI from the contacts database.
 * 
 * Class only slighlty modified from Google.
 * Located @
 * http://developer.android.com/resources/samples/BusinessCard/src/com/example/android/businesscard/ContactAccessorSdk3_4.html
 */

package com.blindroid.contacts;

import java.util.ArrayList;

import com.blindroid.talkingcontacts.ContactInfo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.Contacts.PeopleColumns;
import android.provider.Contacts.Phones;
import android.provider.Contacts.PhonesColumns;
import android.util.Log;

/**
 * This class is designed to allow older operating system versions of the phone
 * to be able to access their contacts system.
 * SDK Verion 4 or older.
 * @author Justin B. Burris
 *
 */
@SuppressWarnings("deprecation")
class ContactsAdapterSdk3_4 extends ContactsAdapter {
	private static final String TAG = "ContactsAdapterSdk3_4";
	
	//The projection is what we use to format the cursor into the form we want
	private static final String[] PROJECTION =
		new String[] {People._ID, PeopleColumns.NAME, Phones._ID, PhonesColumns.NUMBER, PhonesColumns.TYPE,
			PeopleColumns.CUSTOM_RINGTONE,};
	
	//Column indices for the cursor
	private static final int _ID = 0;
	private static final int NAME = 1;
	private static final int PHONE_ID = 2;
	private static final int NUMBER = 3;
	private static final int TYPE = 4;

	private Cursor mCursor;
	
	private ArrayList<String> mContactNames;

	/*
	 * Loads the contact information from the internal system data into a local cursor that
	 * we can read and iterate over.
	 * @see com.blindroid.contacts.ContactsAdapter#loadContacts(android.content.ContentResolver)
	 */
	@Override
	public void loadContacts(ContentResolver contentResolver) {		
		// Get the base URI for People table in Contacts content provider.
	    // ie. content://contacts/people/
		Uri contacts = Phones.CONTENT_URI;
		
		mCursor = contentResolver.query(contacts, PROJECTION, 
				null, //WHERE clause -- not specified
				null, //Selection arguments
				PeopleColumns.NAME + " ASC"); //Order by clause
		
		boolean moveSucceeded = mCursor.moveToFirst();
		
		mContactNames = new ArrayList<String>();
		while(moveSucceeded) {
			mContactNames.add(mCursor.getString(NAME));
			moveSucceeded = mCursor.moveToNext();
		}
	}

	@Override
	public ArrayList<String> getNames() {
		return mContactNames;
	}

	/*
	 * Returns the contact info for a particular contact in the cursor.
	 * It sets up all of the information, and does some logic to determine human readable names
	 * @see com.blindroid.contacts.ContactsAdapter#getContactInfo(int)
	 */
	@Override
	public ContactInfo getContactInfo(int index) {
		ContactInfo cInfo = new ContactInfo();
		mCursor.moveToPosition(index);
		cInfo.setDisplayName(mCursor.getString(NAME));
		cInfo.setPhoneNumber(mCursor.getString(NUMBER));
		cInfo.setPhone_ID(mCursor.getString(PHONE_ID));
		cInfo.set_ID(mCursor.getString(_ID));
		
		cInfo.setIndex(index);
		
		String type;
		int phoneType = Integer.parseInt(mCursor.getString(TYPE));
		switch(phoneType) {
		case PhonesColumns.TYPE_HOME:
			type = "home";
			break;
		case PhonesColumns.TYPE_MOBILE:
			type = "mobile";
			break;
		case PhonesColumns.TYPE_WORK:
			type = "work";
			break;
		case PhonesColumns.TYPE_OTHER:
			type = "other";
			break;
		case PhonesColumns.TYPE_CUSTOM:
			type = "custom";
			break;
		default:
			type = "";
			break;				
		}
		cInfo.setPhoneType(type);
		
		return cInfo;
	}

	/*
	 * Adds a contact to the system contacts database.
	 * @see com.blindroid.contacts.ContactsAdapter#addContact(android.content.ContentResolver, com.blindroid.talkingcontacts.ContactInfo)
	 */
	@Override
	public boolean addContact(ContentResolver contentResolver, ContactInfo cInfo) {
		if(cInfo != null 
				&& cInfo.getDisplayName() != null 
				&& cInfo.getPhoneNumber() != null) {
			//Add name
			ContentValues values = new ContentValues();
			values.put(People.NAME, cInfo.getDisplayName());
			Uri uri = contentResolver.insert(People.CONTENT_URI, values);
			Log.d("ANDROID", uri.toString());
			
			//Add Number
			Uri numberUri = Uri.withAppendedPath(uri, People.Phones.CONTENT_DIRECTORY);
			values.clear();
			values.put(Phones.TYPE, Phones.TYPE_MOBILE);
			values.put(People.NUMBER, cInfo.getPhoneNumber());
			contentResolver.insert(numberUri, values);
			return true;
		}		
		return false;
	}

	/*
	 * Edits a contact in the systems contacts database.
	 * TODO Fix the following bug, offers ideas as to what is causing it currently
	 * WARNING ISSUE :: On real phones with existing contacts this code, or another bug earlier on
	 * WARNING ISSUE :: seems to be updating the incorrect contact. I think there is a conflict with the code used for
	 * WARNING ISSUE :: either the URI syntax for interacting with the contacts database, or there is a problem with
	 * WARNING ISSUE :: the core code used to construct the ContactInfo object. It could be setting the _ID and phone_ID
	 * WARNING ISSUE :: incorrectly, thus causing the issue. This code does however work fine on an emulator. Needs further
	 * WARNING ISSUE :: research into the contacts API to really be sure 
	 * 
	 * @see com.blindroid.contacts.ContactsAdapter#editContact(android.content.ContentResolver, com.blindroid.talkingcontacts.ContactInfo)
	 */
	@Override
	public boolean editContact(ContentResolver contentResolver, ContactInfo cInfo) {
		if(cInfo != null) {
			long id = Integer.parseInt(cInfo.get_ID());
			
			//Update with new name
			Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, id);
			ContentValues values = new ContentValues();
			values.put(PeopleColumns.NAME, cInfo.getDisplayName());
			int effectedRows = contentResolver.update(uri, values, null, null);
			
			//Used to test the above mentioned bug. 
			//Currently program shows the phone_ID and the _ID as being the same
			//This is WRONG
			Log.d(TAG, TAG + " nameSt " + cInfo.getDisplayName());
			Log.d(TAG, TAG + " nameID " + cInfo.get_ID());
			Log.d(TAG, TAG + " phoneSt " + cInfo.getPhoneNumber());
			Log.d(TAG, TAG + " phoneID " + cInfo.getPhone_ID());
			
			//Update with new number
			Uri numberUri = Uri.withAppendedPath(uri, People.Phones.CONTENT_DIRECTORY);
			values.clear();
			values.put(Contacts.Phones.TYPE, getPhoneType(cInfo.getPhoneType()));
			values.put(People.Phones.NUMBER, cInfo.getPhoneNumber());
			effectedRows += contentResolver.update(Uri.withAppendedPath(numberUri, cInfo.getPhone_ID()), values, null, null);
			
			if(effectedRows <= 0)
				return false;
			return true;
		}
		return false;
	}
	
	/*
	 * Deletes a contact from the database using the specified _ID
	 * TODO Fix the above issue and this one should be easy to fix
	 * WARNING ISSUE :: This code probably isn't working either on devices that are not an emulator
	 * WARNING ISSUE :: due to the problem described above
	 * @see com.blindroid.contacts.ContactsAdapter#deleteContact(android.content.ContentResolver, com.blindroid.talkingcontacts.ContactInfo)
	 */
	public boolean deleteContact(ContentResolver contentResolver, ContactInfo cInfo) {
		long id = Integer.parseInt(cInfo.get_ID());
		Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, id);
		int rowsDeleted = contentResolver.delete(uri, null, null);
		
		if(rowsDeleted >= 1) {
			return true;
		}
		return false;
		
	}
	
	/*
	 * Returns the system readable type for the phone
	 */
	private int getPhoneType(String type) {
		if(type.equals("home"))
			return PhonesColumns.TYPE_HOME;
		else if(type.equals("mobile"))
			return PhonesColumns.TYPE_MOBILE;
		else if(type.equals("work"))
			return PhonesColumns.TYPE_WORK;
		else if(type.equals("other"))
			return PhonesColumns.TYPE_OTHER;
		else if(type.equals("custom"))
			return PhonesColumns.TYPE_CUSTOM;
		else
			return PhonesColumns.TYPE_OTHER;
	}
}
