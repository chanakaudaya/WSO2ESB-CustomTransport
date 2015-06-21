package org.wso2.transport.iso8583;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.MessageFormatter;
import org.apache.axis2.transport.OutTransportInfo;
import org.apache.axis2.transport.base.AbstractTransportSender;
import org.apache.axis2.transport.base.BaseUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;
import org.wso2.transport.iso8583.message.ISO8583MessageFormatter;

public class ISO8583TransportSender extends AbstractTransportSender {

	@Override
	public void sendMessage(MessageContext msgCtx, String targetEPR,
			OutTransportInfo outTransportInfo) throws AxisFault {

		try {
			URI isoURL = new URI(targetEPR);
			ISOPackager packager = new GenericPackager(this.getClass()
					.getResourceAsStream("jposdef.xml"));

			ASCIIChannel chl = new ASCIIChannel(isoURL.getHost(),
					isoURL.getPort(), packager);

            writeMessageOut(msgCtx, chl);

		} catch (Exception e) {
			throw new AxisFault(
					"An exception occured in sending the ISO message");
		}

	}


    /**
     * Writting the message to the output channel after applying correct message formatter
     * @param msgContext
     * @param chl
     * @throws org.apache.axis2.AxisFault
     * @throws java.io.IOException
     */
    private void writeMessageOut(MessageContext msgContext,
                                 ASCIIChannel chl) throws AxisFault, IOException {
        ISO8583MessageFormatter messageFormatter = (ISO8583MessageFormatter)BaseUtils.getMessageFormatter(msgContext);
        OMOutputFormat format = BaseUtils.getOMOutputFormat(msgContext);
        messageFormatter.setAsciiChannel(chl);
        messageFormatter.writeTo(msgContext, format, null, true);
    }

}
