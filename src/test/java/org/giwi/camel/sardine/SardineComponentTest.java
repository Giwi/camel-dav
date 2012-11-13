package org.giwi.camel.sardine;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SardineComponentTest extends CamelTestSupport {

	@Test
	public void testSardine() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMinimumMessageCount(1);

		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() {
				from("sardine://foo").to("sardine://bar").to("mock:result");
			}
		};
	}
}
