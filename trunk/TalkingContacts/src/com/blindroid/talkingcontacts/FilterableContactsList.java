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

/* This class is what filters the contact list based upon the users current search string
 * It then sets the cursor to the beginning of the new filtered list for the program to read from
 * 
 * Code modified from Google's Talking Dialer 
 */
package com.blindroid.talkingcontacts;

import java.util.ArrayList;
import java.util.ListIterator;

public class FilterableContactsList {
	private ArrayList<ContactInfo> mFullList;
	private ArrayList<ContactInfo> mFilteredList;
	private ListIterator<ContactInfo> mFilteredListIterator;
	
	@SuppressWarnings("unchecked")
	public FilterableContactsList(ArrayList<String> names) {
		mFullList = new ArrayList<ContactInfo>();
		for(int i = 0; i < names.size(); i++) {
			mFullList.add(new ContactInfo(names.get(i), i));
		}
		mFilteredList = (ArrayList<ContactInfo>) mFullList.clone();
		mFilteredListIterator = mFilteredList.listIterator();		
	}
	
	/*
	 * Returns the next contact in the filtered list
	 */
	public ContactInfo next() {
		if(mFilteredList.size() < 1)
			return null;
		if(!mFilteredListIterator.hasNext())
			mFilteredListIterator = mFilteredList.listIterator();
		return mFilteredListIterator.next();
	}
	
	/*
	 * Returns the previous contact in the filtered list
	 */
	public ContactInfo previous() {
		if(mFilteredList.size() < 1)
			return null;
		if(!mFilteredListIterator.hasPrevious())
			mFilteredListIterator = 
				mFilteredList.listIterator(mFilteredList.size());
		return mFilteredListIterator.previous();
	}
	
	/*
	 * Filters the contact list array with a user provided partial name
	 */
	@SuppressWarnings("unchecked")
	public boolean filter(String partialName) {		
		if(partialName.length() > 0) {
			//Get the lower case of the name for comparison purposes
			String lcPN = partialName.toLowerCase();
			mFilteredList = new ArrayList<ContactInfo>();
			for(int i = 0; i < mFullList.size(); i++) {
				ContactInfo cInfo = mFullList.get(i);
				if(cInfo.getDisplayName() != null) {
					String lcName = cInfo.getDisplayName().toLowerCase();
					if(lcName.startsWith(lcPN))
						mFilteredList.add(cInfo);
				}
			}
			mFilteredListIterator = mFilteredList.listIterator();
		} else {
			mFilteredList = (ArrayList<ContactInfo>) mFullList.clone();
			mFilteredListIterator = mFilteredList.listIterator();
		}
		if(mFilteredList.size() > 0)
			return true;
		else return false;
	}

}
