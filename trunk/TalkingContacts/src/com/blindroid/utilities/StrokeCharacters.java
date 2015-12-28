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

/* Enables the StrokeCharacters view to be displayed ontop of a parent view, thus enabling it to 
 * be used across the program many times.
 * 
 * Code modified from Google's Talking Dialer
 */
package com.blindroid.utilities;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

public class StrokeCharacters extends View implements Observable {
	private final String TAG = "StrokeCharacters";

	// Tracks the state of the stroke dialer wheel
	private static final int AE = 0;
	private static final int IM = 1;
	private static final int QU = 2;
	private static final int Y = 4;
	private static final int NONE = 5;

	// Used to evaluate the motion executed by the user
	private final double THETA_TOLERANCE = (Math.PI / 16);
	private final double LEFT = 0;
	private final double UPLEFT = Math.PI * .25;
	private final double UP = Math.PI * .5;
	private final double UPRIGHT = Math.PI * .75;
	private final double DOWNRIGHT = -Math.PI * .75;
	private final double DOWN = -Math.PI * .5;
	private final double DOWNLEFT = -Math.PI * .25;
	private final double RIGHT = Math.PI;
	private final double RIGHT_WRAP = -Math.PI;

	private ArrayList<Observer> obserbvers;

	private int mCurrentWheel;
	private int mCurrentValue;
	private String mCurrentCharacter = "";

	// Used for tracking the user's finger location state
	private float mDownX, mDownY; // Where the user initially presses
	private float mCurrX, mCurrY; // Current finger position

	public StrokeCharacters(Context context) {
		super(context);
		obserbvers = new ArrayList<Observer>();
		setVisibility(INVISIBLE);
	}
	
	/* Observer code to enable this object to be watched */
	public void registerObserver(Observer o) {
		obserbvers.add(o);
	}
	public void removeObserver(Observer o) {
		int i = obserbvers.indexOf(o);
		if (i >= 0)
			obserbvers.remove(i);
	}
	public void notifyObservers() {
		for (int i = 0; i < obserbvers.size(); i++) {
			Observer o = (Observer) obserbvers.get(i);
			if (mCurrentCharacter.equals(""))
				o.update("[tock]");
			else
				o.update(mCurrentCharacter);
		}
	}

	/* 
	 * Gets the current character from the system 
	 */
	public String getCurrentCharacter() {
		if(mCurrentCharacter.equals("SPACE"))
			mCurrentCharacter = " ";
		return mCurrentCharacter;
	}

	/*
	 * Registers whenever the screen is touched and reacts based upon what sort 
	 * of MoveEvent the event is. E.G. ACTION up means the user lifted their finger
	 */
	public void screenTouched(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();

		if (action == MotionEvent.ACTION_DOWN) {
			initiateMotion(x, y);
		} else if (action == MotionEvent.ACTION_UP) {
			confirmEntry();
		} else {
			mCurrX = x;
			mCurrY = y;

			int prevVal = mCurrentValue;
			mCurrentValue = evalMotion();
			if (mCurrentValue == -1) {
				// Do nothing since we want a deadzone here;
				// Restore state to previous value
				mCurrentValue = prevVal;
				return;
			}
			// There is a wheel that is active active
			if (mCurrentValue != 5) {
				if (mCurrentWheel == NONE) {
					mCurrentWheel = getWheel();
				}
				mCurrentCharacter = getCharacter();
			} else {
				mCurrentCharacter = "";
			}
			invalidate();
			if (prevVal != mCurrentValue) {
				notifyObservers();
			}
		}
	}

	/*
	 * Draws the StrokeCharacters wheel to the screen and sets different values based
	 * upon the users current selection area
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(50);

		int offset = 90;

		int x1 = (int) mDownX - offset;
		int y1 = (int) mDownY - offset;
		int x2 = (int) mDownX;
		int y2 = (int) mDownY - offset;
		int x3 = (int) mDownX + offset;
		int y3 = (int) mDownY - offset;
		int x4 = (int) mDownX - offset;
		int y4 = (int) mDownY;
		int x6 = (int) mDownX + offset;
		int y6 = (int) mDownY;
		int x7 = (int) mDownX - offset;
		int y7 = (int) mDownY + offset;
		int x8 = (int) mDownX;
		int y8 = (int) mDownY + offset;
		int x9 = (int) mDownX + offset;
		int y9 = (int) mDownY + offset;

		y1 -= paint.ascent() / 2;
		y2 -= paint.ascent() / 2;
		y3 -= paint.ascent() / 2;
		y4 -= paint.ascent() / 2;
		y6 -= paint.ascent() / 2;
		y7 -= paint.ascent() / 2;
		y8 -= paint.ascent() / 2;
		y9 -= paint.ascent() / 2;

		switch (mCurrentWheel) {
		case AE:
			paint.setColor(Color.RED);
			drawCharacter("A", x1, y1, canvas, paint, mCurrentCharacter.equals("A"));
			drawCharacter("B", x2, y2, canvas, paint, mCurrentCharacter.equals("B"));
			drawCharacter("C", x3, y3, canvas, paint, mCurrentCharacter.equals("C"));
			drawCharacter("H", x4, y4, canvas, paint, mCurrentCharacter.equals("H"));
			drawCharacter("D", x6, y6, canvas, paint, mCurrentCharacter.equals("D"));
			drawCharacter("G", x7, y7, canvas, paint, mCurrentCharacter.equals("G"));
			drawCharacter("F", x8, y8, canvas, paint, mCurrentCharacter.equals("F"));
			drawCharacter("E", x9, y9, canvas, paint, mCurrentCharacter.equals("E"));
			break;
		case IM:
			paint.setColor(Color.BLUE);
			drawCharacter("P", x1, y1, canvas, paint, mCurrentCharacter.equals("P"));
			drawCharacter("I", x2, y2, canvas, paint, mCurrentCharacter.equals("I"));
			drawCharacter("J", x3, y3, canvas, paint, mCurrentCharacter.equals("J"));
			drawCharacter("O", x4, y4, canvas, paint, mCurrentCharacter.equals("O"));
			drawCharacter("K", x6, y6, canvas, paint, mCurrentCharacter.equals("K"));
			drawCharacter("N", x7, y7, canvas, paint, mCurrentCharacter.equals("N"));
			drawCharacter("M", x8, y8, canvas, paint, mCurrentCharacter.equals("M"));
			drawCharacter("L", x9, y9, canvas, paint, mCurrentCharacter.equals("L"));
			break;
		case QU:
			paint.setColor(Color.GREEN);
			drawCharacter("W", x1, y1, canvas, paint, mCurrentCharacter.equals("W"));
			drawCharacter("X", x2, y2, canvas, paint, mCurrentCharacter.equals("X"));
			drawCharacter("Q", x3, y3, canvas, paint, mCurrentCharacter.equals("Q"));
			drawCharacter("V", x4, y4, canvas, paint, mCurrentCharacter.equals("V"));
			drawCharacter("R", x6, y6, canvas, paint, mCurrentCharacter.equals("R"));
			drawCharacter("U", x7, y7, canvas, paint, mCurrentCharacter.equals("U"));
			drawCharacter("T", x8, y8, canvas, paint, mCurrentCharacter.equals("T"));
			drawCharacter("S", x9, y9, canvas, paint, mCurrentCharacter.equals("S"));
			break;
		case Y:
			paint.setColor(Color.YELLOW);
			drawCharacter(",", x1, y1, canvas, paint, mCurrentCharacter.equals(","));
			drawCharacter("!", x2, y2, canvas, paint, mCurrentCharacter.equals("!"));
			drawCharacter("SPACE", x4, y4, canvas, paint, mCurrentCharacter.equals("SPACE"));
			drawCharacter("Y", x6, y6, canvas, paint, mCurrentCharacter.equals("Y"));
			drawCharacter(".", x7, y7, canvas, paint, mCurrentCharacter.equals("."));
			drawCharacter("?", x8, y8, canvas, paint, mCurrentCharacter.equals("?"));
			drawCharacter("Z", x9, y9, canvas, paint, mCurrentCharacter.equals("Z"));
			break;
		default:
			paint.setColor(Color.RED);
			canvas.drawText("A", x1, y1, paint);
			canvas.drawText("E", x9, y9, paint);
			paint.setColor(Color.BLUE);
			canvas.drawText("I", x2, y2, paint);
			canvas.drawText("M", x8, y8, paint);
			paint.setColor(Color.GREEN);
			canvas.drawText("Q", x3, y3, paint);
			canvas.drawText("U", x7, y7, paint);
			paint.setColor(Color.YELLOW);
			canvas.drawText("Y", x6, y6, paint);
			canvas.drawText("SPACE", x4, y4, paint);
			break;
		}
	}

	/*
	 * Detects when the user initiates the motion of touching the screen and sets the system
	 * to be visible on the parent view
	 */
	private void initiateMotion(float x, float y) {
		mDownX = mCurrX = x;
		mDownY = mCurrY = y;
		mCurrentValue = -1;
		mCurrentWheel = NONE;
		mCurrentCharacter = "";

		setVisibility(VISIBLE);
	}

	/* 
	 * Evaluates the motion of the user and then returns the current value of their position
	 * TODO Clean up this code, the logic here makes sense, but it's doing 3 steps instead of 2
	 */
	private int evalMotion() {
		float rTolerance = 25;

		double r = Math.sqrt(((mDownX - mCurrX) * (mDownX - mCurrX))
				+ ((mDownY - mCurrY) * (mDownY - mCurrY)));
		if (r < rTolerance) {
			return 5;
		}

		// Not sure what's goin on here, the function prototype specifies
		// atan2(x,y), not y,x
		// TODO look into this
		double theta = Math.atan2(mDownY - mCurrY, mDownX - mCurrX);
		
		if (Math.abs(theta - LEFT) < THETA_TOLERANCE) {
			return 4;
		} else if (Math.abs(theta - UPLEFT) < THETA_TOLERANCE) {
			return 1;
		} else if (Math.abs(theta - UP) < THETA_TOLERANCE) {
			return 2;
		} else if (Math.abs(theta - UPRIGHT) < THETA_TOLERANCE) {
			return 3;
		} else if (Math.abs(theta - DOWNRIGHT) < THETA_TOLERANCE) {
			return 9;
		} else if (Math.abs(theta - DOWN) < THETA_TOLERANCE) {
			return 8;
		} else if (Math.abs(theta - DOWNLEFT) < THETA_TOLERANCE) {
			return 7;
		} else if ((theta > RIGHT - THETA_TOLERANCE)
				|| (theta < RIGHT_WRAP + THETA_TOLERANCE)) {
			return 6;
		} else {
			// Off by more than threshold, so it doesn't count
			return -1;
		}
	}

	/* Sets the visibility to invisible so the system is no longer showing a
	 * stroke dialer
	 */
	private void confirmEntry() {
		setVisibility(INVISIBLE);
	}

	/*
	 * Returns the current wheel with the value the user is at
	 */
	private int getWheel() {
		switch (mCurrentValue) {
		case 1:
			return AE;
		case 2:
			return IM;
		case 3:
			return QU;
		case 4:
			return Y;
		case 5:
			return NONE;
		case 6:
			return Y;
		case 7:
			return QU;
		case 8:
			return IM;
		case 9:
			return AE;
		default:
			return NONE;
		}
	}

	/*
	 * Gets the character value of the current position the user is at based
	 * upon whatever the current wheel value is (E.G. are they in A or Q?)
	 */
	private String getCharacter() {
		switch (mCurrentWheel) {
		case AE:
			switch (mCurrentValue) {
			case 1:
				return "A";
			case 2:
				return "B";
			case 3:
				return "C";
			case 4:
				return "H";
			case 5:
				return "";
			case 6:
				return "D";
			case 7:
				return "G";
			case 8:
				return "F";
			case 9:
				return "E";
			default:
				return "";
			}
		case IM:
			switch (mCurrentValue) {
			case 1:
				return "P";
			case 2:
				return "I";
			case 3:
				return "J";
			case 4:
				return "O";
			case 5:
				return "";
			case 6:
				return "K";
			case 7:
				return "N";
			case 8:
				return "M";
			case 9:
				return "L";
			default:
				return "";
			}
		case QU:
			switch (mCurrentValue) {
			case 1:
				return "W";
			case 2:
				return "X";
			case 3:
				return "Q";
			case 4:
				return "V";
			case 5:
				return "";
			case 6:
				return "R";
			case 7:
				return "U";
			case 8:
				return "T";
			case 9:
				return "S";
			default:
				return "";
			}
		case Y:
			switch (mCurrentValue) {
			case 1:
				return ",";
			case 2:
				return "!";
			case 3:
				return ""; // return "MODE";
			case 4:
				return "SPACE";
			case 5:
				return "";
			case 6:
				return "Y";
			case 7:
				return ".";
			case 8:
				return "?";
			case 9:
				return "Z";
			default:
				return "";
			}
		default:
			return "";

		}
	}

	/* 
	 * Draws the character information based upon whether or not it is 
	 * currently selected by the use or not (Big if it is, small if it isn't)
	 */
	private void drawCharacter(String character, int x, int y, Canvas canvas,
			Paint paint, boolean isSelected) {
		int regSize = 50;
		int selectedSize = regSize * 2;
		if(isSelected) {
			paint.setTextSize(selectedSize);
		} else {
			paint.setTextSize(regSize);
		}
		canvas.drawText(character, x, y, paint);
	}
}
