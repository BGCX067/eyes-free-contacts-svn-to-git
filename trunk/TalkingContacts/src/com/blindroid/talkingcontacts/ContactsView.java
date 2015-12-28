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

/* This class is the view for a users contacts list. It operates in a manner
 * similar to that of talkingdialer's ContactsView, with added edit functionality
 * and some code cleanup.
 * TODO refactor some common elements into an object that this class could extend called BlindView which would enable a lot cleaner class for this class and EditView
 * 
 * Class extensively modified from Google from the original talkingdialer code
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

public class ContactsView extends Activity implements Observer {
	public static final String CONTACT_INDEX = "contact_index";
	private static final String TAG = "ContactsView";
	
	private final int EDIT_REQ_CODE = 1;
	private final int ADD_REQ_CODE = 2;

	private TextView mSearchString;
	private StrokeCharacters mStrokeCharacters;
	private ShakeDetector mShakeDetector;

	protected static FilterableContactsList mFilteredContacts;
	private ContactInfo mDisplayContact;
	private ContactInfo cInfo = null;
	
	private boolean mTrackballEnabled = true;
	private int mTrackballTimeout = 500;
	private long mMenuDownTime;
	private long mKeyUpTime = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, TAG + " onCreate called");
        
        mStrokeCharacters = new StrokeCharacters(this);
        mStrokeCharacters.registerObserver(this);
        
        setContentView(R.layout.contacts);        
        RelativeLayout contact_view = (RelativeLayout) findViewById(R.id.contact_view);
        contact_view.addView(mStrokeCharacters);        
        
        initializeFilter();
        
        mSearchString = (TextView) findViewById(R.id.search_string);
        	
        TalkingContacts.tts.speak("Viewing Contacts", TextToSpeech.QUEUE_FLUSH, null);

        mShakeDetector = new ShakeDetector(this, new ShakeListener() {
        	public void onShakeDetected() {
        		Log.d(TAG, "onShakeDetected Called");
        		deleteCharacter();
        	}
        });
    }
    
    public void shutdown() {
    	mShakeDetector.shutdown();
    }
    
    /*
     * Initializes the contact filter when the program first runs
     */
    private void initializeFilter() {
    	//Loads the contact adapter with the people information
    	TalkingContacts.CONTACTS_ADAPTER.loadContacts(getContentResolver());
    	
    	//Load the filtered contacts list with the data from the contact adapter
        mFilteredContacts = new FilterableContactsList(TalkingContacts.CONTACTS_ADAPTER.getNames());
    }
    
    /*
     * Is called by StrokeCharacters whenever a character needs 
     * to be returned
     * @see blindroid.testing.testcontacts.Observer#update(java.lang.String)
     */
    public void update(String character) {
    	TalkingContacts.tts.speak(character, TextToSpeech.QUEUE_FLUSH, null);
    }
    
    /*
     * Is called by ShakeDetector whenever a character needs to be
     * deleted
     * @see blindroid.testing.testcontacts.Observer#update()
     */
    public void update() {
    	deleteCharacter();
    }
    
    /*
     * Detects user down keypress and then reacts appropriately
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	String keyString = "";
    	switch(keyCode) {
    	case KeyEvent.KEYCODE_DPAD_DOWN:
	        nextContact();
	        return true;
	      case KeyEvent.KEYCODE_DPAD_UP:
	        prevContact();
	        return true;
    	case KeyEvent.KEYCODE_DEL:
    		deleteCharacter();  
    		return true;
    	case KeyEvent.KEYCODE_MENU:
    		//Logic for handleing this is included in the onKeyUp method
    		if(event.getRepeatCount() == 0) {
    			Log.d(TAG, "MENU pressed");
    			mMenuDownTime = event.getDownTime();
    		}
    		break;
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
    	addCharacter(keyString);
    	if(keyString.equals(""))
    		return super.onKeyDown(keyCode, event);
    	else 
    		return true;
    }
    
    /*
     * Detects user up keypress and then reacts appropriately
     * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	switch(keyCode) {
    	case KeyEvent.KEYCODE_MENU:
    		Log.d(TAG, "MENU released");
    		
    		if(mKeyUpTime == 0) {
    			mKeyUpTime = event.getEventTime();
    		} else {
    			mKeyUpTime = 0;
    			return true;
    		}
    		if(mKeyUpTime - mMenuDownTime > 1500) {
    			addContact();
    		} else {
    			editContact();
    		}
    		return true;
    	}
    	return super.onKeyUp(keyCode, event);
    }
    
    /* 
     * Activates whenever the user clicks the screen(non-Javadoc)
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	mStrokeCharacters.screenTouched(event);
    	if(event.getAction() == MotionEvent.ACTION_UP) {
    		addCharacter(mStrokeCharacters.getCurrentCharacter());
    		return true;
    	}
    	return super.onTouchEvent(event);
    }
 
    /*
     * Activates whenever the user uses the trackball
     * @see android.app.Activity#onTrackballEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTrackballEvent(MotionEvent event) {
    	if(!mTrackballEnabled)
    		return true;
    	cInfo = null; 	  	
    	if(event.getY() > .16) {
    		mTrackballEnabled = false;
    		(new Thread(new trackballTimeout())).start();
    		prevContact();
    		
    	} else if(event.getY() < -.16) {
        	mTrackballEnabled = false;
    		(new Thread(new trackballTimeout())).start();
    		nextContact();
    		
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
    			Thread.sleep(mTrackballTimeout);
    			mTrackballEnabled = true;
    		} catch(InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /*
     * Deletes a character from the end of the current search string
     */
    private void deleteCharacter() { 
    	CharSequence chars = mSearchString.getText();
		if(chars.length() > 0) {
			TalkingContacts.tts.speak(chars.subSequence(
    				chars.length() - 1, chars.length()).toString() + " deleted", 
    				TextToSpeech.QUEUE_FLUSH, null);
			mSearchString.setText(chars.subSequence(0, chars.length() - 1));
			mFilteredContacts.filter(mSearchString.getText().toString());
    		filterContacts();
		} 
    }
    
    /*
     * Adds a character to the end of the current search string
     */
    private void addCharacter(String character) {
    	if(character.length() > 0) {
    		mSearchString.append(character);
    		mFilteredContacts.filter(mSearchString.getText().toString());
    		filterContacts();
    		if(mDisplayContact != null) {
				speakContactInfo();
				updateContact();
			}			
		}
    }
   
    /*
     * Moves the cursor to the previous contact
     * Then updates the display
     */
    private void prevContact(){
    	
		cInfo = mFilteredContacts.next();
		
		//Strange bug. If it's the first time you scroll the wheel up or 
		//down after scrolling the opposite direction, the program will
		//repeat the current contact again. I think there is a problem
		//with my understanding of how the list iterator is working
		if(mDisplayContact != null &&
				cInfo.getIndex() == mDisplayContact.getIndex()) {
			cInfo = mFilteredContacts.next();
		}
		
		if(cInfo != null) {
	    	mDisplayContact = TalkingContacts.CONTACTS_ADAPTER.getContactInfo(cInfo.getIndex());
	    	
	    	updateContact();
	    	speakContactInfo();
    	}
    }
    
    /*
     * Moves the cursor to the next contact
     * Then updates the display 
     */
    private void nextContact(){
    
		cInfo = mFilteredContacts.previous();
		
		//Same bug as described in prevContact
		if(mDisplayContact != null &&
				cInfo.getIndex() == mDisplayContact.getIndex()) {
			cInfo = mFilteredContacts.previous();
		}
		
		if(cInfo != null) {
	    	mDisplayContact = TalkingContacts.CONTACTS_ADAPTER.getContactInfo(cInfo.getIndex());
	    	
	    	updateContact();
	    	speakContactInfo();
    	}
    }
    
    /*
     * Called whenever the search string changes in order to get the
     * newest list of filtered contacts
     */
    private void filterContacts() {
    	ContactInfo cInfo = mFilteredContacts.next();
    	
    	if(cInfo == null) {
    		TalkingContacts.tts.speak("No contacts found.", 0, null);
    		CharSequence chars = mSearchString.getText();
    		if(chars.length() > 0) {
    			mSearchString.setText(chars.subSequence(0, chars.length() - 1));
    		}
    	} else {
    		mDisplayContact = TalkingContacts.CONTACTS_ADAPTER.getContactInfo(cInfo.getIndex());
    	}
    	
    }
    
    /*
     * Updates the displayed contact on the screen and announces the
     * name to the user
     */
    private void updateContact() {	
    	TextView displayNameView = (TextView) findViewById(R.id.name);
    	displayNameView.setText(mDisplayContact.getDisplayName());
    	
    	TextView phoneNumberView = (TextView) findViewById(R.id.number);
    	phoneNumberView.setText(mDisplayContact.getPhoneType());
    }    
    
    /*
     * Speaks the contact's contact info
     */
    private void speakContactInfo() {
    	TalkingContacts.tts.speak(mDisplayContact.getDisplayName(), TextToSpeech.QUEUE_ADD, null);
    	TalkingContacts.tts.speak(mDisplayContact.getPhoneType(), TextToSpeech.QUEUE_ADD, null);
    }

    /*
     * Opens a new intent (EditView) allowing the user to edit an 
     * already selected contact
     */
    private void editContact() {
    	if(mDisplayContact != null) {
    		Intent i = new Intent(this, EditView.class);
    		i.putExtra(CONTACT_INDEX, mDisplayContact.getIndex());
    		startActivityForResult(i, EDIT_REQ_CODE);
    	} else {
    		TalkingContacts.tts.speak("Select a contact to edit", 
    				TextToSpeech.QUEUE_FLUSH, null);
    	}
    }
    
    /*
     * Opens a new intent (EditView) allowing the user to add a 
     * new contact
     */
    private void addContact() {
    	Intent i = new Intent(this, EditView.class);
    	startActivityForResult(i, ADD_REQ_CODE);
    }

    /*
     * Tests to see which activity was completed and the result code of the activity.
     * Should the data save be successful, it announces the completion of the activity
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	String textString = null;
    	String contactName = null;
    	boolean successful = false;
    	int actionState = -1;
    	
    	if(data != null) {
    		contactName = data.getExtras().getString(EditView.CONTACT_NAME);
    		successful = data.getExtras().getBoolean(EditView.EDITVIEW_STATUS);
    		actionState = data.getExtras().getInt(EditView.CONTACT_ACTION);
    		
    	}
    	if(requestCode == EDIT_REQ_CODE) {
    		if(resultCode == Activity.RESULT_CANCELED) {
    			textString = "Editing Canceled";
    		} else if(resultCode == Activity.RESULT_OK) {    			
    			if(successful) {
    				if(actionState == EditView.ACTION_EDITED) {
    					textString = contactName + " saved";
    				} else {
    					textString = contactName + " deleted";
    				}
    			} else {
    				textString = "Unable to save contact data";
    			}
    		}
    	} else if(requestCode == ADD_REQ_CODE) {
    		if(resultCode == Activity.RESULT_CANCELED) {
    			textString = "Adding Canceled";
    		} else if(resultCode == Activity.RESULT_OK) {   			
    			if(successful) {
    				textString = "New Contact, " + contactName + ", added";
    			} else {
    				textString = "Unable to add contact";
    			}
    		}
    	}
    	TalkingContacts.tts.speak(textString, TextToSpeech.QUEUE_FLUSH, null);
    	initializeFilter();
		mFilteredContacts.filter(mSearchString.getText().toString());
    	filterContacts();
    	updateContact();
    }
}
