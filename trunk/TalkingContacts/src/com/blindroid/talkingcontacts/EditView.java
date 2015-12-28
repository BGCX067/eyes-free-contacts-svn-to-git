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

/* This class enables a user to edit contacts or add them to the contacts system.
 * It also allows a user to delete a contact, should they desire to.
 * 
 * TODO Refactoring of common elements out of the class into BlindView
 * TODO Split add and edit into 2 seperate classes. The if code just makes this more complex than it needs to be 
 */
package com.blindroid.talkingcontacts;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blindroid.utilities.Observer;
import com.blindroid.utilities.ShakeDetector;
import com.blindroid.utilities.StrokeCharacters;
import com.blindroid.utilities.ShakeDetector.ShakeListener;

public class EditView extends Activity implements Observer {
	private static final String TAG = "EditView";
	
	//The various keys for the intent values
	public static final String CONTACT_ACTION = "contact_action";
	public static final String CONTACT_NAME = "contact_name";
	public static final String EDITVIEW_STATUS = "editview_status";
	
	//The states that the application could have ended in
	public static final int ACTION_EDITED = 1;
	public static final int ACTION_DELETED = 2;
	public static final int ACTION_ADDED = 3; 
	
	private final int TRACKBALL_TIMEOUT = 500;

	private StrokeCharacters mStrokeCharacters;
	private ContactInfo mDisplayContact;
	private ShakeDetector mShakeDetector;

	private TextView mEditTextLabel;
	private TextView mEditTextView;

	private boolean mEditing = false;
	private boolean mEditingName;
	
	private boolean mTrackballEnabled = true;
	private long mBackDownTime = 0;
	private long mBackUpTime = 0;
	private final long mBackWaitTime = 1500;

	private boolean waitingToConfirm = false;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_contact);

		// If the user selected to edit a contact, this information is set
		Bundle extras = getIntent().getExtras();
		String textString;
		if (extras != null) {
			mEditing = true;
			int index = extras.getInt(ContactsView.CONTACT_INDEX);
			mDisplayContact = TalkingContacts.CONTACTS_ADAPTER
					.getContactInfo(index);
			textString = "Editing " + mDisplayContact.getDisplayName();
		} else {
			mDisplayContact = new ContactInfo();
			textString = "Add screen";
		}
		TalkingContacts.tts.speak(textString, TextToSpeech.QUEUE_FLUSH, null);

		mEditTextLabel = (TextView) findViewById(R.id.edit_text_label);
		mEditTextView = (TextView) findViewById(R.id.edit_text);

		// Sets it up so the user can edit the name
		mEditingName = true;
		setEditField();

		// Create the stroke dialer and register this class as an observer
		mStrokeCharacters = new StrokeCharacters(this);
		mStrokeCharacters.registerObserver(this);

		RelativeLayout edit_view = (RelativeLayout) findViewById(R.id.edit_view);
		edit_view.addView(mStrokeCharacters);
		
		//Shake listener for detecting when the system has been shaken
		mShakeDetector = new ShakeDetector(this, new ShakeListener() {
        	public void onShakeDetected() {
        		Log.d(TAG, "onShakeDetected Called");
        		deleteCharacter();
        	}
        });
	}
	
	/*
	 * Needed to remove the shake listener's functionality after the view has exited
	 */
    public void shutdown() {
    	mShakeDetector.shutdown();
    }
	
	public void update(String character) {
		TalkingContacts.tts.speak(character, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	/*
	 * This registers whenever the user touches the screen. 
	 * Currently it calls the StrokeCharacters class to display the StrokeCharacter view ontop of this one
	 * enabling eyes free input.
	 * 
	 * TODO Implement StrokeNumbers - Not enough time
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mEditingName) {
			mStrokeCharacters.screenTouched(event);
		} else {
			// mStrokeNumbers.screenTouched(event);
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (mEditingName) {
				addCharacter(mStrokeCharacters.getCurrentCharacter());
			} else {
				// addCharacter(mStrokeNumbers.getCurrentCharacter());
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

	/*
	 * Registers whenever a user presses a key down and responds appropriately
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		String keyString = "";
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
	        scrollContact();
	        return true;
	      case KeyEvent.KEYCODE_DPAD_UP:
	        scrollContact();
	        return true;
		case KeyEvent.KEYCODE_MENU:
			setResult(RESULT_CANCELED);
			finish();
			return true;
		case KeyEvent.KEYCODE_CALL:
			if (waitingToConfirm) {
				boolean saveSucceeded = updateContact();

				Intent i = new Intent();
				if(mEditing)
					i.putExtra(CONTACT_ACTION, ACTION_EDITED);
				else
					i.putExtra(CONTACT_ACTION, ACTION_ADDED);
				i.putExtra(EDITVIEW_STATUS, saveSucceeded);
				i.putExtra(CONTACT_NAME, mDisplayContact.getDisplayName());

				setResult(RESULT_OK, i);
				shutdown();
				finish();
				return true;
			} else {
				waitingToConfirm = true;
				TalkingContacts.tts.speak(
						"Press call again to save the following information.",
						TextToSpeech.QUEUE_FLUSH, null);
				TalkingContacts.tts.speak("New Name "
						+ mDisplayContact.getDisplayName(),
						TextToSpeech.QUEUE_ADD, null);
				TalkingContacts.tts.speak("New Number "
						+ mDisplayContact.getPhoneNumber(),
						TextToSpeech.QUEUE_ADD, null);
			}
			return true;
		case KeyEvent.KEYCODE_DEL:
    		deleteCharacter();
    		return true;
		}
		keyString = getCharacterInput(keyCode);
		addCharacter(keyString);
		waitingToConfirm = false;
		if(keyString.equals(""))
			return super.onKeyDown(keyCode, event);
		else return true;
	}

	/*
	 * Scrolls the edit area between number and name 
	 * whenever the user moves the trackball up or down
	 * 
	 * @see android.app.Activity#onTrackballEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (!mTrackballEnabled)
			return true;

		if (event.getY() > .16 || event.getY() < -.16) {
			mTrackballEnabled = false;
			(new Thread(new trackballTimeout())).start();
			scrollContact();
			return true;
		}
		return super.onTrackballEvent(event);
	}

	/*
	 * Called by the trackball to prevent it from scrolling
     * too many times
	 */
	class trackballTimeout implements Runnable {
		public void run() {
			try {
				Thread.sleep(TRACKBALL_TIMEOUT);
				mTrackballEnabled = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Enables a user to delete a contact while they are in the edit mode.
	 * It happens when the user presses and holds the back button for a
	 * predetermined period of time.
	 */
	@Override
	public void onBackPressed() {
		if(mEditing) {
			if(mBackDownTime == 0 || mBackUpTime == 0) {
				return;
			} else if((mBackUpTime - mBackDownTime) > mBackWaitTime
					&& mEditing) {
				mBackUpTime = mBackDownTime = 0;
				deleteContact(); //Delete the current contact
			}
		}
	}
	
	/*
	 * This will test for when the back button has been released 
	 * and call onBackPressed this is used for all SDK versions
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(mEditing) {
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				if(event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getRepeatCount() == 0) {
					//Strat tracking when the key was pressed
					mBackDownTime = event.getDownTime();
					return true;
				} else if(event.getAction() == KeyEvent.ACTION_UP) {
					mBackUpTime = event.getEventTime();
					onBackPressed();
					return true;
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	/*
	 * Deletes a character from the current edit field
	 * and then redisplays everything
	 */
	private void deleteCharacter() {
		CharSequence chars = mEditTextView.getText();
		if(chars.length() > 0) {
			TalkingContacts.tts.speak(chars.subSequence(
    				chars.length() - 1, chars.length()).toString() + " deleted", 
    				TextToSpeech.QUEUE_FLUSH, null);
			mEditTextView.setText(chars.subSequence(0, chars.length() - 1));
			
			if(mEditingName){
				mDisplayContact.setDisplayName(mEditTextView.getText().toString());
			} else {
				mDisplayContact.setPhoneNumber(mEditTextView.getText().toString());
			}
		}  else {
			TalkingContacts.tts.speak("No letters to delete", TextToSpeech.QUEUE_FLUSH, null);
		}
	}
	
	/*
	 * Gets the character input from the keypad
	 * TODO This stuff can be refactored out
	 */
	private String getCharacterInput(int keyCode) {
		String keyString = "";
		if (mEditingName) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_SPACE:
				keyString = " ";
				break;
			case KeyEvent.KEYCODE_A:
				keyString = "a";
				break;
			case KeyEvent.KEYCODE_B:
				keyString = "b";
				break;
			case KeyEvent.KEYCODE_C:
				keyString = "c";
				break;
			case KeyEvent.KEYCODE_D:
				keyString = "d";
				break;
			case KeyEvent.KEYCODE_E:
				keyString = "e";
				break;
			case KeyEvent.KEYCODE_F:
				keyString = "f";
				break;
			case KeyEvent.KEYCODE_G:
				keyString = "g";
				break;
			case KeyEvent.KEYCODE_H:
				keyString = "h";
				break;
			case KeyEvent.KEYCODE_I:
				keyString = "i";
				break;
			case KeyEvent.KEYCODE_J:
				keyString = "j";
				break;
			case KeyEvent.KEYCODE_K:
				keyString = "k";
				break;
			case KeyEvent.KEYCODE_L:
				keyString = "l";
				break;
			case KeyEvent.KEYCODE_M:
				keyString = "m";
				break;
			case KeyEvent.KEYCODE_N:
				keyString = "n";
				break;
			case KeyEvent.KEYCODE_O:
				keyString = "o";
				break;
			case KeyEvent.KEYCODE_P:
				keyString = "p";
				break;
			case KeyEvent.KEYCODE_Q:
				keyString = "q";
				break;
			case KeyEvent.KEYCODE_R:
				keyString = "r";
				break;
			case KeyEvent.KEYCODE_S:
				keyString = "s";
				break;
			case KeyEvent.KEYCODE_T:
				keyString = "t";
				break;
			case KeyEvent.KEYCODE_U:
				keyString = "u";
				break;
			case KeyEvent.KEYCODE_V:
				keyString = "v";
				break;
			case KeyEvent.KEYCODE_W:
				keyString = "w";
				break;
			case KeyEvent.KEYCODE_X:
				keyString = "x";
				break;
			case KeyEvent.KEYCODE_Y:
				keyString = "y";
				break;
			case KeyEvent.KEYCODE_Z:
				keyString = "z";
				break;
			}
		} else {
			switch(keyCode) {
			case KeyEvent.KEYCODE_0:
				keyString = "0";
				break;
			case KeyEvent.KEYCODE_1:
				keyString = "1";
				break;
			case KeyEvent.KEYCODE_2:
				keyString = "2";
				break;
			case KeyEvent.KEYCODE_3:
				keyString = "3";
				break;
			case KeyEvent.KEYCODE_4:
				keyString = "4";
				break;
			case KeyEvent.KEYCODE_5:
				keyString = "5";
				break;
			case KeyEvent.KEYCODE_6:
				keyString = "6";
				break;
			case KeyEvent.KEYCODE_7:
				keyString = "7";
				break;
			case KeyEvent.KEYCODE_8:
				keyString = "8";
				break;
			case KeyEvent.KEYCODE_9:
				keyString = "9";
				break;
			}
		}
		return keyString;
	}

	/*
	 * Adds a character to the current text view then says the character aloud
	 */
	private void addCharacter(String character) {
		if (character.length() > 0) {
			mEditTextView.append(character);
			if (character.equals(" "))
				character = "space";
			if(mEditingName){
				mDisplayContact.setDisplayName(mEditTextView.getText().toString());}
			else{
				mDisplayContact.setPhoneNumber(mEditTextView.getText().toString());}
			TalkingContacts.tts.speak(character + " added",
					TextToSpeech.QUEUE_ADD, null);
		}
	}

	/*
	 * Sets the current edit field the user is on, either name or number
	 */
	private void setEditField() {
		if (mEditingName) {
			mEditTextLabel.setText("Name");
			String name = mDisplayContact.getDisplayName();
			if (name != null)
				mEditTextView.setText(name);
			else
				mEditTextView.setText("");
		} else {
			mEditTextLabel.setText("Phone Number");
			String number = mDisplayContact.getPhoneNumber();
			if (number != null)
				mEditTextView.setText(number);
			else
				mEditTextView.setText("");
		}
	}
	
	/*
	 * Scrolls the current edit field and sets it depending on what the user is editing currently
	 * Sets it either to the name or number
	 */
	private void scrollContact(){
		if (mEditingName) {
			mEditingName = false;
			setEditField();
			TalkingContacts.tts.speak("Editing Number",
					TextToSpeech.QUEUE_FLUSH, null);
		} else {
			mEditingName = true;
			setEditField();
			TalkingContacts.tts.speak("Editing Name",
					TextToSpeech.QUEUE_FLUSH, null);
		}	
	}
	
	/*
	 * Attempts to delete the current contact from the database
	 * If it succedes it passes the contact data to the system
	 */
	private void deleteContact() { 
		boolean deleteSuccessful = TalkingContacts.CONTACTS_ADAPTER.deleteContact(
				getContentResolver(), mDisplayContact);
		
		Intent i = new Intent();
		i.putExtra(CONTACT_ACTION, ACTION_DELETED);
		i.putExtra(CONTACT_NAME, mDisplayContact.getDisplayName());
		i.putExtra(EDITVIEW_STATUS, deleteSuccessful);

		setResult(RESULT_OK, i);
		finish();
	}
	
	/*
	 * Either updates or adds the contact to the contact database This is
	 * determined by whether or not the user selected to add or edit a contact
	 */
	private boolean updateContact() {
		// TODO Add actual update logic
		if (mEditing) {
			return TalkingContacts.CONTACTS_ADAPTER.editContact(
					getContentResolver(), mDisplayContact);
		} else {
			return TalkingContacts.CONTACTS_ADAPTER.addContact(
					getContentResolver(), mDisplayContact);
		}
	}

}
