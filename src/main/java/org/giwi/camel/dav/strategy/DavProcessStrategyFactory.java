/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright
 * ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.giwi.camel.dav.strategy;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Expression;
import org.apache.camel.component.file.GenericFileExclusiveReadLockStrategy;
import org.apache.camel.component.file.GenericFileProcessStrategy;
import org.apache.camel.component.file.strategy.GenericFileDeleteProcessStrategy;
import org.apache.camel.component.file.strategy.GenericFileExpressionRenamer;
import org.apache.camel.component.file.strategy.GenericFileNoOpProcessStrategy;
import org.apache.camel.component.file.strategy.GenericFileRenameExclusiveReadLockStrategy;
import org.apache.camel.component.file.strategy.GenericFileRenameProcessStrategy;
import org.apache.camel.util.ObjectHelper;

import com.googlecode.sardine.DavResource;

public final class DavProcessStrategyFactory {

	private DavProcessStrategyFactory() {
	}

	public static GenericFileProcessStrategy<DavResource> createGenericFileProcessStrategy(CamelContext context, Map<String, Object> params) {

		// We assume a value is present only if its value not null for String and 'true' for boolean
		Expression moveExpression = (Expression) params.get("move");
		Expression moveFailedExpression = (Expression) params.get("moveFailed");
		Expression preMoveExpression = (Expression) params.get("preMove");
		boolean isNoop = params.get("noop") != null;
		boolean isDelete = params.get("delete") != null;
		boolean isMove = moveExpression != null || preMoveExpression != null || moveFailedExpression != null;

		if (isDelete) {
			GenericFileDeleteProcessStrategy<DavResource> strategy = new GenericFileDeleteProcessStrategy<DavResource>();
			strategy.setExclusiveReadLockStrategy(getExclusiveReadLockStrategy(params));
			if (preMoveExpression != null) {
				GenericFileExpressionRenamer<DavResource> renamer = new GenericFileExpressionRenamer<DavResource>();
				renamer.setExpression(preMoveExpression);
				strategy.setBeginRenamer(renamer);
			}
			if (moveFailedExpression != null) {
				GenericFileExpressionRenamer<DavResource> renamer = new GenericFileExpressionRenamer<DavResource>();
				renamer.setExpression(moveFailedExpression);
				strategy.setFailureRenamer(renamer);
			}
			return strategy;
		} else if (isMove || isNoop) {
			GenericFileRenameProcessStrategy<DavResource> strategy = new GenericFileRenameProcessStrategy<DavResource>();
			strategy.setExclusiveReadLockStrategy(getExclusiveReadLockStrategy(params));
			if (!isNoop && moveExpression != null) {
				// move on commit is only possible if not noop
				GenericFileExpressionRenamer<DavResource> renamer = new GenericFileExpressionRenamer<DavResource>();
				renamer.setExpression(moveExpression);
				strategy.setCommitRenamer(renamer);
			}
			// both move and noop supports pre move
			if (moveFailedExpression != null) {
				GenericFileExpressionRenamer<DavResource> renamer = new GenericFileExpressionRenamer<DavResource>();
				renamer.setExpression(moveFailedExpression);
				strategy.setFailureRenamer(renamer);
			}
			// both move and noop supports pre move
			if (preMoveExpression != null) {
				GenericFileExpressionRenamer<DavResource> renamer = new GenericFileExpressionRenamer<DavResource>();
				renamer.setExpression(preMoveExpression);
				strategy.setBeginRenamer(renamer);
			}
			return strategy;
		} else {
			// default strategy will do nothing
			GenericFileNoOpProcessStrategy<DavResource> strategy = new GenericFileNoOpProcessStrategy<DavResource>();
			strategy.setExclusiveReadLockStrategy(getExclusiveReadLockStrategy(params));
			return strategy;
		}
	}

	@SuppressWarnings("unchecked")
	private static GenericFileExclusiveReadLockStrategy<DavResource> getExclusiveReadLockStrategy(Map<String, Object> params) {
		GenericFileExclusiveReadLockStrategy<DavResource> strategy = (GenericFileExclusiveReadLockStrategy<DavResource>) params.get("exclusiveReadLockStrategy");
		if (strategy != null) {
			return strategy;
		}

		// no explicit strategy set then fallback to readLock option
		String readLock = (String) params.get("readLock");
		if (ObjectHelper.isNotEmpty(readLock)) {
			if ("none".equals(readLock) || "false".equals(readLock)) {
				return null;
			} else if ("rename".equals(readLock)) {
				GenericFileRenameExclusiveReadLockStrategy<DavResource> readLockStrategy = new GenericFileRenameExclusiveReadLockStrategy<DavResource>();
				Long timeout = (Long) params.get("readLockTimeout");
				if (timeout != null) {
					readLockStrategy.setTimeout(timeout);
				}
				Long checkInterval = (Long) params.get("readLockCheckInterval");
				if (checkInterval != null) {
					readLockStrategy.setCheckInterval(checkInterval);
				}
				return readLockStrategy;
			} else if ("changed".equals(readLock)) {
				DavChangedExclusiveReadLockStrategy readLockStrategy = new DavChangedExclusiveReadLockStrategy();
				Long timeout = (Long) params.get("readLockTimeout");
				if (timeout != null) {
					readLockStrategy.setTimeout(timeout);
				}
				Long checkInterval = (Long) params.get("readLockCheckInterval");
				if (checkInterval != null) {
					readLockStrategy.setCheckInterval(checkInterval);
				}
				Long minLength = (Long) params.get("readLockMinLength");
				if (minLength != null) {
					readLockStrategy.setMinLength(minLength);
				}
				Boolean fastExistsCheck = (Boolean) params.get("fastExistsCheck");
				if (fastExistsCheck != null) {
					readLockStrategy.setFastExistsCheck(fastExistsCheck);
				}
				return readLockStrategy;
			}
		}

		return null;
	}
}
