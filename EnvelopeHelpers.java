package com.docusign.controller.eSignature.examples;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.docusign.esign.model.CarbonCopy;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;



public final class EnvelopeHelpers {

    public static final String ENVELOPE_STATUS_SENT = "sent";
    public static final String ENVELOPE_STATUS_CREATED = "created";
    public static final String SIGNER_STATUS_CREATED = "Created";
    public static final String DELIVERY_METHOD_EMAIL = "Email";
    public static final String SIGNER_ROLE_NAME = "signer";
    public static final String CC_ROLE_NAME = "cc";
    public static final String WORKFLOW_STEP_ACTION_PAUSE = "pause_before";
    public static final String WORKFLOW_TRIGGER_ROUTING_ORDER = "routing_order";
    public static final String WORKFLOW_STATUS_IN_PROGRESS = "in_progress";


    private EnvelopeHelpers() {}

    
    public static byte[] readFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToByteArray(resource.getInputStream());
    }

    
    static Template loadHtmlTemplate(String path) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        cfg.setLocale(Locale.US);
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
        ClassPathResource resource = new ClassPathResource(path);
        String source = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return new Template(path, source, cfg);
    }

    
    static String createHtmlFromTemplate(Template template, String objectName, Object value) throws IOException {
        Map<String, Object> input = new HashMap<>();
        input.put(objectName, value);
        StringWriter stringWriter = new StringWriter();
        try {
            template.process(input, stringWriter);
            return stringWriter.toString();
        } catch (TemplateException exception) {
            throw new ExampleException("Can't process html template " + template.getName(), exception);
        }
    }

    
    public static byte[] createHtmlFromTemplateFileInByte(String path, String objectName, Object value) throws IOException {
        Template template = loadHtmlTemplate(path);
        return createHtmlFromTemplate(template, objectName, value).getBytes(StandardCharsets.UTF_8);
    }

  
    public static String createHtmlFromTemplateFile(String path, String objectName, Object value) throws IOException {
        Template template = loadHtmlTemplate(path);
        return createHtmlFromTemplate(template, objectName, value);
    }

   
    public static Document createDocumentFromFile(String fileName, String docName, String docId) throws IOException {
        byte[] buffer = readFile(fileName);
        String extention = FilenameUtils.getExtension(fileName);
        return createDocument(buffer, docName, extention, docId);
    }

    
    public static Document createDocument(byte[] data, String documentName, String fileExtention, String documentId) {
        Document document = new Document();
        document.setDocumentBase64(Base64.getEncoder().encodeToString(data));
        document.setName(documentName);
        document.setFileExtension(fileExtention);
        document.setDocumentId(documentId);
        return document;
    }

  
    public static SignHere createSignHere(String anchorString, int yOffsetPixels, int xOffsetPixels) {
        SignHere signHere = new SignHere();
        signHere.setAnchorString(anchorString);
        signHere.setAnchorUnits("pixels");
        signHere.setAnchorYOffset(String.valueOf(yOffsetPixels));
        signHere.setAnchorXOffset(String.valueOf(xOffsetPixels));
        return signHere;
    }

    
    public static Tabs createSingleSignerTab(String anchorString, int yOffsetPixels, int xOffsetPixels) {
        SignHere signHere = createSignHere(anchorString, yOffsetPixels, xOffsetPixels);
        return createSignerTabs(signHere);
    }

  
    public static Tabs createSignerTabs(SignHere... signs) {
        Tabs signerTabs = new Tabs();
        signerTabs.setSignHereTabs(Arrays.asList(signs));
        return signerTabs;
    }

    
    public static Recipients createRecipients(Signer signer, CarbonCopy cc) {
        Recipients recipients = new Recipients();
        recipients.setSigners(Arrays.asList(signer));
        recipients.setCarbonCopies(Arrays.asList(cc));
        return recipients;
    }

    public static Recipients createSingleRecipient(Signer signer) {
        Recipients recipients = new Recipients();
        recipients.setSigners(Arrays.asList(signer));
        return recipients;
    }

    public static Recipients createTwoSigners(Signer signer, Signer signer2) {
        Recipients recipients = new Recipients();
        recipients.setSigners(Arrays.asList(signer, signer2));
        return recipients;
    }
}
