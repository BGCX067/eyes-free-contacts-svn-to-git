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

/* Main entry point for the system. Designed to allow the user to finish in the ContactsView, 
 * and return to dial a number. 
 * 
 * I don't know if this is good coding standards. The Talking Dialer seems to do it, but I am not sure
 * if it is needed. Part of the problem with this approach is that it seems that if you exit the program, 
 * due to an interruption, and the system restarts on the last running activity, that this class is never
 * initiated and thus all of the data that the other programs need to access can't be reached.
 * 
 * Something to look into as this code is worked on and cleaned
 * 
 */
package com.blindroid.talkingcontacts;

import com.blindroid.contacts.ContactsAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

public class TalkingContacts extends Activity {
	public static TextToSpeech tts;
	Context context;
	
	public static final ContactsAdapter CONTACTS_ADAPTER 
		= ContactsAdapter.getInstance();
	
	private final int REQ_CODE = 1;
	
	/** Called when the activity is first created. */	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tts = new TextToSpeech(this, ttsInitListener);
		
		Intent i = new Intent(this, ContactsView.class);
		startActivityForResult(i, REQ_CODE);
		
	}
	
	/*
	 * This is called whenever the ContactsView class finishes and exists.
	 * This can either occur due to the user selecting someone to call, 
	 * or any other reason.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQ_CODE) {
			if(resultCode == Activity.RESULT_CANCELED) {
				finish();
				return;
			}
			//TODO Call logic needs to go here
		}
	}
	
	/*
	 * Shuts down the app and destroys the TTS object in a safe manner
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		tts.shutdown();
		super.onDestroy();
	}
	
	/*
	 * Initiates the text to speech listener and packages it with specific
	 * sounds
	 */
	private TextToSpeech.OnInitListener ttsInitListener 
    		= new TextToSpeech.OnInitListener() {
				
    	public void onInit(int status) {
			String pkgName = this.getClass().getPackage().getName();
			tts.addSpeech("[tock]", pkgName, R.raw.tock_snd);
		}
	};
}