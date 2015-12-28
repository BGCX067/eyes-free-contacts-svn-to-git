package com.google.blindroid.talkingdialer.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.google.blindroid.talkingdialer.SlideDial;
import com.jayway.android.robotium.solo.Solo;



public class talkingdialerTest extends ActivityInstrumentationTestCase2<SlideDial> {
	private Solo solo;
	
	public talkingdialerTest(){
		super("com.google.blindroid.talkingdialer", SlideDial.class);
	}
	
	protected void setUp() throws Exception{
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	//test dialing a number and deleting the digits
	public void testDialingDelete(){
		solo.assertCurrentActivity("Expected Slide Dial", SlideDial.class);
		solo.enterText(0, "4"); //enter '4' for dialing
		solo.enterText(1, "7");
		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
		solo.enterText(2,"8");
		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
	}
	
	//tests dialing a number
	public void testDial() throws InterruptedException{
		solo.enterText(0, "7");
		solo.enterText(1, "0");
		solo.enterText(2, "6");
		solo.enterText(3, "8");
		solo.enterText(4, "2");
		solo.enterText(5, "5");
		solo.enterText(6, "2");
		solo.enterText(7, "7");
		solo.enterText(8, "3");
		solo.enterText(9, "7");
		
		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_CALL);
		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_ENDCALL);
		solo.goBack();
	}
	
	protected void tearDown() throws Exception{
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
	
}
