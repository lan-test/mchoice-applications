/*
 *   (C) Copyright 2010-2011 hSenid Software International (Pvt) Limited.
 *   All Rights Reserved.
 *
 *   These materials are unpublished, proprietary, confidential source code of
 *   hSenid Software International (Pvt) Limited and constitute a TRADE SECRET
 *   of hSenid Software International (Pvt) Limited.
 *
 *   hSenid Software International (Pvt) Limited retains all title to and intellectual
 *   property rights in these materials.
 *
 */
package hms.ctap.simulator.ussd;

import com.google.gson.Gson;
import hms.sdp.ussd.MchoiceUssdMessage;
import hms.sdp.ussd.MchoiceUssdResponse;
import hms.sdp.ussd.impl.UssdAoRequestMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * $LastChangedDate$
 * $LastChangedBy$
 * $LastChangedRevision$
 */
public class UssdMessageReceiver extends HttpServlet {

    private static final List<UssdAoRequestMessage> receivedMessages = new ArrayList<UssdAoRequestMessage>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        final UssdAoRequestMessage ussdAoRequestMessage = gson.fromJson(readBody(req.getInputStream()), UssdAoRequestMessage.class);
        final String conversationId = req.getHeader(MchoiceUssdMessage.CONVERSATION);
        ussdAoRequestMessage.setConversationId(conversationId);
        System.out.println("New USSD Message Received [" + ussdAoRequestMessage + "]");
        receivedMessages.add(ussdAoRequestMessage);
        final MchoiceUssdResponse mchoiceUssdResponse = new MchoiceUssdResponse();
        mchoiceUssdResponse.setStatusCode("SBL-USSD-2000");
        mchoiceUssdResponse.setStatusDescription("Success");
        mchoiceUssdResponse.setCorrelationId(String.valueOf(Math.random()));
        final String jsonResponse = gson.toJson(mchoiceUssdResponse);
        resp.getWriter().write(jsonResponse);
        resp.getWriter().flush();
    }

    private String readBody(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder line = new StringBuilder();
        String li;
        while ((li = in.readLine()) != null) {
            line.append(li);
        }
        return line.toString();
    }

    public static List<UssdAoRequestMessage> getReceivedMessages() {
        return receivedMessages;
    }
}