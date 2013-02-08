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
@Suite.SuiteClasses({ FileToDavTempFileNameTest.class, FromDavAsyncProcessTest.class, FromDavDeleteFileTest.class, FromDavDoNotDeleteFileIfProcessFailsTest.class,
		FromDavExclusiveReadNoneStrategyTest.class, FromDavFilterTest.class, FromFileToDavDefaultRootRenameStrategyTest.class, FromFileToDavDeleteTest.class, FromFileToDavTest.class,
		FromDavKeepLastModifiedTest.class, FromDavMoveFileAbsoluteFolderRecursiveTest.class, FromDavMoveFileTest.class, FromDavMoveFilePostfixTest.class, FromDavMoveFilePrefixTest.class,
		FromDavMoveFileRecursiveTest.class, FromDavMoveFileToHiddenFolderRecursiveTest.class, FromDavNoEndpointPathRelativeMoveToAbsoluteTest.class, FromDavNoFilesTest.class, FromDavNoopTest.class,
		FromDavNotDownloadTest.class, FromDavPollFileOnlyTest.class, FromDavPreMoveDeleteTest.class, FromDavPreMoveFileExpressionTest.class, FromDavPreMoveFilePostfixTest.class,
		FromDavPreMoveFilePrefixTest.class, FromDavPreMoveNoopTest.class, FromDavRecursiveNoopTest.class, FromDavRegexPatternTest.class, FromDavRemoteFileFilterDirectoryTest.class,
		FromDavRemoteFileFilterTest.class, FromDavRemoteFileSortByExpressionTest.class, FromDavRemoteFileSortByIgnoreCaseExpressionTest.class, FromDavRemoteFileSortByNestedExpressionTest.class,
		FromDavRemoteFileSorterTest.class, FromDavSimpleNoEndpointPathRelativeMoveToAbsoluteTest.class, FromDavSimpleNoEndpointPathRelativeMoveToRelativeTest.class,
		FromDavSimpleRelativeMoveToRelativeTest.class, FromDavSimulateNetworkIssueRecoverTest.class, FromDavStartingDirAndFileNameClashTest.class, FromDavThirdPoolOkTest.class,
		FromDavToAsciiFileNoBodyConversionTest.class, FromDavToAsciiFileTest.class, FromDavToBinaryFileTest.class, FromDavToBinarySampleTest.class, FromDavToFileNoFileNameHeaderTest.class,
		FromDavToMockTest.class, FromDavTwoSlashesIssueTest.class, FromQueueThenConsumeDavToMockTest.class, DavBrowsableEndpointTest.class, DavChangedZeroLengthReadLockTest.class,
		DavConsumerAbsolutePathTest.class, DavConsumerAsyncStressTest.class, DavConsumerBodyAsStringTest.class, DavConsumerDirectoriesNotMatchedTest.class, DavConsumerDoneFileNameFixedTest.class,
		DavConsumerDoneFileNameTest.class, DavConsumerDualDoneFileNameTest.class, DavConsumerExcludeNameTest.class, DavConsumerIdempotentRefTest.class, DavConsumerIdempotentTest.class,
		DavConsumerIncludeNameTest.class, DavConsumerMaxMessagesPerPollTest.class, DavConsumerMoveExpressionTest.class, DavConsumerMultipleDirectoriesTest.class, DavChangedReadLockTest.class,
		DavChangedReadLockFastExistCheckTest.class, DavChangedReadLockTimeoutTest.class, DavConsumerLocalWorkDirectoryTest.class, DavConsumerLocalWorkDirectoryWorkOnPayloadTest.class,
		FromDavSetNamesWithMultiDirectoriesTest.class, FromDavSimpleRelativeMoveToAbsoluteTest.class, FromDavToBinaryFilesTest.class, DavConsumerLocalWorkDirectoryAsAbsolutePathTest.class,
		DavConsumerLocalWorkDirectoryDirectTest.class, DavConsumerNotEagerMaxMessagesPerPollTest.class, DavConsumerRelativeFileNameTest.class })
public class AllTests {

}
