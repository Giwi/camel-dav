package org.giwi.camel.dav;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Sardine producer.
 */
public class DavProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(DavProducer.class);
    private DavEndpoint endpoint;

    public DavProducer(DavEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
