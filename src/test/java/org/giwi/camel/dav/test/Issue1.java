/**
 * 
 */
package org.giwi.camel.dav.test;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

/**
 * @author xavier
 * 
 */
public class Issue1 extends AbstractDavTest {

    @Test
    public void testFromFileToDav() throws Exception {
	MockEndpoint mock = getMockEndpoint("mock:result");
	mock.expectedMessageCount(2);
	template.sendBodyAndHeader("file:src/main/data", "Hello World",
		Exchange.FILE_NAME, "hello.txt");
	Thread.sleep(1000);
	template.sendBodyAndHeader("file:src/main/data", "Hello World",
		Exchange.FILE_NAME, "hello2.txt");
	assertMockEndpointsSatisfied();
	Thread.sleep(1000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.camel.test.junit4.CamelTestSupport#createRouteBuilder()
     */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
	return new RouteBuilder() {
	    @Override
	    public void configure() throws Exception {
		from(
			"file:"
				+ DAV_ROOT_DIR
				+ "/20130403?consumer.initialDelay=5000&autoCreate=false")
			.to("mock:result");
		from("file:src/main/data")
			.to(DAV_URL
				+ "?fileName=${date:now:yyyyMMdd}/${file:onlyname}");
	    }
	};
    }

}
