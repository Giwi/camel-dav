package org.giwi.camel.dav;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class DavComponentTest extends CamelTestSupport {

	@Test
	public void testSardine() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMinimumMessageCount(10);

		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() {
				// from("dav://foo").to("dav://bar").to("log:result");
				from("file:/home/xavier/tmp/input").to("dav:localhost/webdav1?autoCreate=false").to("log:result");
			}
		};
	}
}
