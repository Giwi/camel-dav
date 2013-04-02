/**
 *  Copyright 2013 Giwi Softwares (http://giwi.free.fr)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.giwi.camel.dav.test;

import org.giwi.camel.dav.test.auth.DavConsumerDeleteNoWritePermissionTest;
import org.giwi.camel.dav.test.auth.DavConsumerThrowExceptionOnLoginFailedTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * The Class AllTests.
 * 
 * @author xavier
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DavBrowsableEndpointTest.class,
	DavChangedReadLockFastExistCheckTest.class,
	DavChangedReadLockTest.class,
	DavChangedReadLockTimeoutTest.class,
	DavChangedZeroLengthReadLockTest.class,
	DavConnectTimeoutTest.class,
	DavConsumerAbsolutePathTest.class,
	DavConsumerAsyncStressTest.class,
	DavConsumerBodyAsStringTest.class,
	DavConsumerDirectoriesNotMatchedTest.class,
	DavConsumerDoneFileNameFixedTest.class,
	DavConsumerDoneFileNameTest.class,
	DavConsumerDualDoneFileNameTest.class,
	DavConsumerExcludeNameTest.class,
	DavConsumerIdempotentRefTest.class,
	DavConsumerIdempotentTest.class,
	DavConsumerIncludeNameTest.class,
	DavConsumerLocalWorkDirectoryAsAbsolutePathTest.class,
	DavConsumerLocalWorkDirectoryDirectTest.class,
	DavConsumerLocalWorkDirectoryTest.class,
	DavConsumerLocalWorkDirectoryWorkOnPayloadTest.class,
	DavConsumerMaxMessagesPerPollTest.class,
	DavConsumerMoveExpressionTest.class,
	DavConsumerMultipleDirectoriesTest.class,
	DavConsumerNotEagerMaxMessagesPerPollTest.class,
	DavConsumerRelativeFileNameTest.class,
	DavConsumerSkipDotFilesTest.class,
	DavConsumerTemplateTest.class,
	DavConsumerWithNoFileOptionTest.class,
	DavEndpointURISanitizedTest.class,
	DavIllegalOptionsTest.class,
	DavLoginNoRetryTest.class,
	DavLoginTest.class,
	DavNoReconnectAttemptUnknownHostTest.class,
	DavPollingConsumerIdleMessageTest.class,
	DavPollingConsumerTest.class,
	DavProducerAllowNullBodyFileAlreadyExistTest.class,
	DavProducerAllowNullBodyTest.class,
	DavProducerBuildDirectoryTest.class,
	DavProducerBuildPartOfDirectoryTest.class,
	DavProducerConcurrentTest.class,
	DavProducerDoneFileNameTest.class,
	DavProducerExpressionTest.class,
	DavProducerFileExistAppendNoFileBeforeTest.class,
	DavProducerFileExistAppendTest.class,
	DavProducerFileExistFailTest.class,
	DavProducerFileExistIgnoreTest.class,
	DavProducerFileExistOverrideNoFileBeforeTest.class,
	DavProducerFileExistOverrideNotEagerDeleteTargetFileTwoUploadTest.class,
	DavProducerFileExistOverrideTest.class,
	DavProducerFileExistOverrideTwoUploadTest.class,
	DavProducerFileFastExistFailTest.class,
	DavProducerFileWithPathTest.class, DavProducerMoveExistingTest.class,
	DavProducerRecipientListParallelTimeoutTest.class,
	DavProducerRecipientListTest.class,
	DavProducerRootFileExistFailTest.class,
	DavProducerTempFileExistIssueTest.class,
	DavProducerTempPrefixTest.class,
	DavReconnectAttemptServerStoppedTest.class,
	DavReconnectAttemptUnknownHostTest.class, DavRecursiveDepth2Test.class,
	DavRecursiveDepth3Test.class, DavRecursiveDepthTest.class,
	DavShutdownCompleteAllTasksTest.class,
	DavShutdownCompleteCurrentTaskOnlyTest.class,
	DavSimpleConsumeAbsoluteTest.class,
	DavThrowExceptionOnConnectionFailedTest.class,
	FileToDavTempFileNameTest.class, FromDavAsyncProcessTest.class,
	FromDavDeleteFileTest.class,
	FromDavDoNotDeleteFileIfProcessFailsTest.class,
	FromDavExclusiveReadNoneStrategyTest.class, FromDavFilterTest.class,
	FromDavKeepLastModifiedTest.class,
	FromDavMoveFileAbsoluteFolderRecursiveTest.class,
	FromDavMoveFilePostfixTest.class, FromDavMoveFilePrefixTest.class,
	FromDavMoveFileRecursiveTest.class, FromDavMoveFileTest.class,
	FromDavMoveFileToHiddenFolderRecursiveTest.class,
	FromDavNoEndpointPathRelativeMoveToAbsoluteTest.class,
	FromDavNoFilesTest.class, FromDavNoopTest.class,
	FromDavNotDownloadTest.class, FromDavPollFileOnlyTest.class,
	FromDavPreMoveDeleteTest.class, FromDavPreMoveFileExpressionTest.class,
	FromDavPreMoveFilePostfixTest.class,
	FromDavPreMoveFilePrefixTest.class, FromDavPreMoveNoopTest.class,
	FromDavRecursiveNoopTest.class, FromDavRegexPatternTest.class,
	FromDavRemoteFileFilterDirectoryTest.class,
	FromDavRemoteFileFilterTest.class,
	FromDavRemoteFileSortByExpressionTest.class,
	FromDavRemoteFileSortByIgnoreCaseExpressionTest.class,
	FromDavRemoteFileSortByNestedExpressionTest.class,
	FromDavRemoteFileSorterTest.class,
	FromDavSetNamesWithMultiDirectoriesTest.class,
	FromDavSimpleNoEndpointPathRelativeMoveToAbsoluteTest.class,
	FromDavSimpleNoEndpointPathRelativeMoveToRelativeTest.class,
	FromDavSimpleRelativeMoveToAbsoluteTest.class,
	FromDavSimpleRelativeMoveToRelativeTest.class,
	FromDavSimulateNetworkIssueRecoverTest.class,
	FromDavStartingDirAndFileNameClashTest.class,
	FromDavThirdPoolOkTest.class,
	FromDavToAsciiFileNoBodyConversionTest.class,
	FromDavToAsciiFileTest.class, FromDavToBinaryFilesTest.class,
	FromDavToBinaryFileTest.class, FromDavToBinarySampleTest.class,
	FromDavToFileNoFileNameHeaderTest.class, FromDavToMockTest.class,
	FromDavTwoSlashesIssueTest.class,
	FromFileToDavDefaultRootRenameStrategyTest.class,
	FromFileToDavDeleteTest.class, FromFileToDavTest.class,
	FromQueueThenConsumeDavToMockTest.class, PaddyRouteTest.class,
	RecipientListErrorHandlingIssueTest.class,
	ToDavTempFileTargetFileExistTest.class, UriConfigurationTest.class,
	DavConsumerDeleteNoWritePermissionTest.class,
	DavConsumerThrowExceptionOnLoginFailedTest.class })
public class AllTests {

}
