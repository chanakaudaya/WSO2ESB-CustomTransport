package org.wso2.transport.iso8583.message;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.MessageFormatter;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.wso2.transport.iso8583.ISO8583Constant;

import javax.xml.namespace.QName;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;


public class ISO8583MessageFormatter implements MessageFormatter {

    private ASCIIChannel asciiChannel;
    @Override
    public byte[] getBytes(MessageContext messageContext, OMOutputFormat omOutputFormat) throws AxisFault {
        return new byte[0];
    }

    @Override
    public void writeTo(MessageContext messageContext, OMOutputFormat omOutputFormat, OutputStream outputStream, boolean b) throws AxisFault {
        ISOMsg isoMsg = toISO8583(messageContext);
        ASCIIChannel chl = this.asciiChannel;
        try {
            chl.connect();
            chl.send(isoMsg);
            chl.disconnect();
        } catch (Exception ex) {
            throw new AxisFault(
                    "An exception occured in sending the ISO message");
        }
    }

    @Override
    public String getContentType(MessageContext messageContext, OMOutputFormat omOutputFormat, String s) {
        return null;
    }

    @Override
    public URL getTargetAddress(MessageContext messageContext, OMOutputFormat omOutputFormat, URL url) throws AxisFault {
        return null;
    }

    @Override
    public String formatSOAPAction(MessageContext messageContext, OMOutputFormat omOutputFormat, String s) {
        return null;
    }

    public ISOMsg toISO8583(MessageContext messageContext) throws AxisFault {
        SOAPEnvelope soapEnvelope = messageContext.getEnvelope();
        OMElement isoElements = soapEnvelope.getBody().getFirstElement();

        ISOMsg isoMsg = new ISOMsg();

        @SuppressWarnings("unchecked")
        Iterator<OMElement> fieldItr = isoElements.getFirstChildWithName(
                new QName(ISO8583Constant.TAG_DATA)).getChildrenWithLocalName(
                ISO8583Constant.TAG_FIELD);

        String mtiVal = isoElements
                .getFirstChildWithName(new QName(ISO8583Constant.TAG_CONFIG))
                .getFirstChildWithName(new QName(ISO8583Constant.TAG_MTI))
                .getText();

        try {
            isoMsg.setMTI(mtiVal);

            while (fieldItr.hasNext()) {

                OMElement isoElement = (OMElement) fieldItr.next();

                String isoValue = isoElement.getText();

                int isoTypeID = Integer.parseInt(isoElement.getAttribute(
                        new QName("id")).getAttributeValue());

                isoMsg.set(isoTypeID, isoValue);

            }

            return isoMsg;

        } catch (ISOException ex) {
            throw new AxisFault("Error parsing the ISO8583 payload");
        } catch (Exception e) {

            throw new AxisFault("Error processing stream");
        }

    }


    public ASCIIChannel getAsciiChannel() {
        return asciiChannel;
    }

    public void setAsciiChannel(ASCIIChannel asciiChannel) {
        this.asciiChannel = asciiChannel;
    }

}
