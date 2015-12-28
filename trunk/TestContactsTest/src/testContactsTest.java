import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;
import android.view.KeyEvent;
import com.blindroid.talkingcontacts.*;

public class testContactsTest extends ActivityInstrumentationTestCase2<TalkingContacts>{
	Solo solo;
	
	public testContactsTest(){
		super("blindroid.testing.testcontacts", TalkingContacts.class);
	}
	
	protected void setUp() throws Exception{
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	/**
	 * testChooseContact doesnt seem to be working. Problem when pressing
	 * the call button(changes views).
	 */
	
	//user chooses a contact to dial
//	public void testChooseContact() throws InterruptedException{
//		solo.enterText(0, "D");
//		//confirm and call number
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_CALL);
//		Thread.sleep(8000);
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_CALL);
//	}
	
	/**
	 * testEditContact
	 * @throws InterruptedException 
	 */
//	public void testEditContact() throws InterruptedException{
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//		solo.enterText(0, "D");
//		Thread.sleep(3000);
//		//confirm Donald Toulson and start editing
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//		solo.enterText(1, "h");
//		Thread.sleep(4000);
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
//		//confirm edit
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_CALL);
//		Thread.sleep(7000);
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_CALL);
//		Thread.sleep(7000);
//	}
	
	/**
	 * testDeleteContact
	 * @throws InterruptedException 
	 */
//	public void testDeleteContact() throws InterruptedException{
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//		solo.enterText(0, "E");
//		Thread.sleep(3000);
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
//	}
	
	/**
	 * testAddContact
	 */
	public void testAddContact(){
		
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
