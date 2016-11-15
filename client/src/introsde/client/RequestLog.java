package introsde.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@NoArgsConstructor
@AllArgsConstructor
@Data
class RequestLog {

    private int requestNumber;
    private String httpMethod;
    private String url;
    private String acceptHeader;
    private String contentTypeHeader;
    private String responseStatus;
    private int statusCode;
    private String body;

    public void log(PrintWriter out) {
        out.println(String.format("Request #%d: %s %s Accept: %s Content-type: %s", requestNumber, httpMethod, url,
                acceptHeader, contentTypeHeader));
        out.println("=> Result: " + responseStatus);
        out.println("=> HTTP STATUS: " + statusCode);
        out.println(body);
    }

    public static String prettify(String mediaType, String rawContent) {
        switch (mediaType) {
            case MediaType.APPLICATION_JSON:
                return jsonToPrettyString(rawContent) + "\n";
            case MediaType.APPLICATION_XML:
                return xmlToPrettyString(rawContent);
            default:
                throw new RuntimeException("Invalid media type: " + mediaType);
        }
    }

    private static String xmlToPrettyString(String xmlString) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes("utf-8"))));

            // Remove whitespaces outside tags
            NodeList nodeList = (NodeList) XPathFactory.newInstance().newXPath()
                    .evaluate("//text()[normalize-space()='']", document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String jsonToPrettyString(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonString, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
