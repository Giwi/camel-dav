/**
 * 
 */
package org.giwi.camel.dav;

import org.apache.camel.main.Main;

/**
 * @author xavier
 * 
 */
public class MainTest {

	/**
	 * A main() so we can easily run these routing rules in our IDE
	 */
	public static void main(String... args) throws Exception {
		Main main = new Main();
		main.enableHangupSupport();
		main.addRouteBuilder(new MyRouteBuilder());
		main.run(args);
		Thread.sleep(30000);
	}
}
