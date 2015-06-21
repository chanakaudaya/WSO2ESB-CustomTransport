package org.wso2.transport.iso8583.message;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.context.MessageContext;

import java.io.InputStream;

/**
 * Created by chanaka-mac on 15-06-21.
 */
public class ISO8583MessageBuilder implements Builder {
    @Override
    public OMElement processDocument(InputStream inputStream, String s, MessageContext messageContext) throws AxisFault {
        return null;
    }
}
