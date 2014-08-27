package org.mt4j.test.testUtil;

import org.mt4j.MTApplication;

public class DummyMTApplication extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize();
	}
	
	@Override
	public void startUp() {
		this.addScene(new DummyScene(this, "Dummy Scene"));
	}
}
