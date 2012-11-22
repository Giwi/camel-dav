/**
 * 
 */
package org.giwi.camel.dav;

import org.apache.camel.builder.RouteBuilder;

/**
 * @author xavier
 * 
 */
public class MyRouteBuilder extends RouteBuilder {

	/*
	 * (non-Javadoc)
	 * @see org.apache.camel.builder.RouteBuilder#configure()
	 */
	@Override
	public void configure() throws Exception {
		// from("dav://foo").to("dav://bar").to("log:result");
		// from("file:/home/xavier/tmp/input?recursive=true").to("dav:localhost/webdav1?autoCreate=true").to("log:result");
		// from("dav:localhost/webdav1?autoCreate=false&move=camel&recursive=true").to("file:/home/xavier/tmp/output");

		from("dav:localhost/webdav1?autoCreate=false&idempotent=true&recursive=true").to("file:/home/xavier/tmp/output");
	}

}
