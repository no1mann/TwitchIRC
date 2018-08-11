package twitch.utils;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Key event manager
 */
public class KeyPressed {
	
	//Set of keys that are currently pressed. Contains key ID
	private static Set<Integer> keyList = ConcurrentHashMap.newKeySet();
	
	//Checks whether a certain key is pressed
	public static boolean isPressed(int event) {
		synchronized (KeyPressed.class) {
			return keyList.contains(event);
		}
	}

	public KeyPressed() {
		//Event dispatch thread for key event
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent ke) {
				synchronized (KeyPressed.class) {
					switch (ke.getID()) {
					
					//Key Pressed
					case KeyEvent.KEY_PRESSED:
						//Adds key too keyList
						if (!keyList.contains(ke.getKeyCode()))
							keyList.add(ke.getKeyCode());
						break;
						
					//Key Released
					case KeyEvent.KEY_RELEASED:
						//Removes key from keyList
						if (keyList.contains(ke.getKeyCode()))
							keyList.remove(ke.getKeyCode());
						break;
						
					}
					return false;
				}
			}
		});
	}
}
