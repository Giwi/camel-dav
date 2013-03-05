/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.giwi.camel.dav;

import java.net.URI;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.component.file.GenericFileEndpoint;

import com.googlecode.sardine.DavResource;

/**
 * Represents the component that manages {@link DavEndpoint}.
 * 
 * @author Giwi Softwares
 * 
 * @param <T>
 */
public class DavComponent<T extends DavResource> extends RemoteFileComponent<DavResource> {
	/**
	 * 
	 */
	public DavComponent() {
	}

	/**
	 * @param context
	 */
	public DavComponent(CamelContext context) {
		super(context);
	}

	@Override
	protected GenericFileEndpoint<DavResource> buildFileEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		String baseUri = getBaseUri(uri);
		// lets make sure we create a new configuration as each endpoint can
		// customize its own version
		// must pass on baseUri to the configuration (see above)
		DavConfiguration config = new DavConfiguration(new URI(baseUri));
		DavEndpoint<DavResource> answer = new DavEndpoint<DavResource>(uri, this, config);
		return answer;
	}

	/**
	 * Get the base uri part before the options as they can be non URI valid
	 * such as the expression using $ chars and the URI constructor will regard
	 * $ as an illegal character and we don't want to enforce end users to to
	 * escape the $ for the expression (file language)
	 */
	protected String getBaseUri(String uri) {
		String baseUri = uri;
		if (uri.indexOf("?") != -1) {
			baseUri = uri.substring(0, uri.indexOf("?"));
		}
		return baseUri;
	}

	@Override
	protected void afterPropertiesSet(GenericFileEndpoint<DavResource> endpoint) throws Exception {
		// noop
	}
}
