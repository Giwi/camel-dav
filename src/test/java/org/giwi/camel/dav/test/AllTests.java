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
@Suite.SuiteClasses({ 
	FileToDavTempFileNameTest.class,
	FromDavAsyncProcessTest.class,
	FromDavDeleteFileTest.class,
	FromDavDoNotDeleteFileIfProcessFailsTest.class,
	FromDavExclusiveReadNoneStrategyTest.class,
	FromDavFilterTest.class,
	FromFileToDavDefaultRootRenameStrategyTest.class,
	FromFileToDavDeleteTest.class,
	FromFileToDavTest.class,
	FromDavKeepLastModifiedTest.class,
	FromDavMoveFileAbsoluteFolderRecursiveTest.class,
	FromDavMoveFileTest.class,
	FromDavMoveFilePostfixTest.class,
	FromDavMoveFilePrefixTest.class,
	FromDavMoveFileRecursiveTest.class,
	FromDavMoveFileToHiddenFolderRecursiveTest.class,
	FromDavNoEndpointPathRelativeMoveToAbsoluteTest.class,
	FromDavNoFilesTest.class,
	FromDavNoopTest.class,
	FromDavNotDownloadTest.class,
	FromDavPollFileOnlyTest.class,
	FromDavPreMoveDeleteTest.class
	})
public class AllTests {

}
