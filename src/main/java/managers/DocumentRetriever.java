package managers;

import exceptions.ParserException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static exceptions.ErrorCodes.ERROR_RETREIVING_XML;


/**
 * Created by Ameer on 3/3/18.
 */
@Log4j
public class DocumentRetriever {


    public Document downloadXML(@NonNull final String url) throws ParserException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(url);
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Error getting xml from url: " + url);
            throw new ParserException(ERROR_RETREIVING_XML, "Error getting xml from url: " + url);
        }
    }

}
