package com.docusign.controller.eSignature.services;

import com.docusign.DSConfiguration;
import com.docusign.controller.eSignature.examples.EnvelopeHelpers;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;

import java.io.IOException;
import java.util.Collections;

public final class EmbeddedSigningService {
    public static ViewUrl embeddedSigning(
            ApiClient apiClient,
            String accountId,
            String envelopeId,
            RecipientViewRequest viewRequest
    ) throws ApiException {
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);

        return envelopesApi.createRecipientView(accountId, envelopeId, viewRequest);
    }

    public static String createEnvelope(
            ApiClient apiClient,
            String accountId,
            EnvelopeDefinition envelope
    ) throws ApiException {
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envelope);

        return envelopeSummary.getEnvelopeId();
    }
    //ds-snippet-start:eSign1Step4
    public static RecipientViewRequest makeRecipientViewRequest(
            String signerEmail,
            String signerName,
            String clientUserId,
            String dsReturnURL,
            String pingURL
    ) {
        RecipientViewRequest viewRequest = new RecipientViewRequest();
      
        String stateValue = "?state=123";
        viewRequest.setReturnUrl(dsReturnURL + stateValue);

        
        String authenticationMethod = "none";
        viewRequest.setAuthenticationMethod(authenticationMethod);

        
        viewRequest.setEmail(signerEmail);
        viewRequest.setUserName(signerName);
        viewRequest.setClientUserId(clientUserId);

        
        String pingFrequency = "600";
        viewRequest.setPingFrequency(pingFrequency); // seconds
        viewRequest.setPingUrl(pingURL);

        return viewRequest;
    }
   
    public static EnvelopeDefinition makeEnvelope(
            String signerEmail,
            String signerName,
            String signerClientId,
            Integer anchorOffsetY,
            Integer anchorOffsetX,
            String documentFileName,
            String documentName
    ) throws IOException {
        
        Signer signer = new Signer();
        signer.setEmail(signerEmail);
        signer.setName(signerName);
        signer.clientUserId(signerClientId);
        signer.recipientId("1");
        signer.setTabs(EnvelopeHelpers.createSingleSignerTab("/sn1/", anchorOffsetY, anchorOffsetX));

        
        Recipients recipients = new Recipients();
        recipients.setSigners(Collections.singletonList(signer));

        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        envelopeDefinition.setRecipients(recipients);
        Document doc = EnvelopeHelpers.createDocumentFromFile(documentFileName, documentName, "3");
        envelopeDefinition.setDocuments(Collections.singletonList(doc));
        
        envelopeDefinition.setStatus(EnvelopeHelpers.ENVELOPE_STATUS_SENT);

        return envelopeDefinition;
    }
    
}
