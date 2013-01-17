/**
 * 
 */
package org.giwi.camel.dav.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author xavier
 * 
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({ FromFileToDavDeleteTest.class, FromFileToDavTest.class, FileToDavTempFileNameTest.class, FromFileToDavDefaultRootRenameStrategyTest.class, FromDavAsyncProcessTest.class,
		FromDavDeleteFileTest.class, FromDavDoNotDeleteFileIfProcessFailsTest.class })
public class AllTests {

}
