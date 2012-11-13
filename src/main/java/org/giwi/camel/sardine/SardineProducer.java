package org.giwi.camel.sardine;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Sardine producer.
 */
public class SardineProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(SardineProducer.class);
    private SardineEndpoint endpoint;

    public SardineProducer(SardineEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
