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
@Suite.SuiteClasses({ FromFileToDavTest.class, FileToDavTempFileNameTest.class, FromFileToDavDefaultRootRenameStrategyTest.class, })
public class AllTests {

}
