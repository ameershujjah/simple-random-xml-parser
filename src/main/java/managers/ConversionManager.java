package managers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ParserException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import model.Episode;
import model.Show;
import model.Shows;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static exceptions.ErrorCodes.ERROR_SERIALIZING_SHOWS;
import static utils.Constants.*;

/**
 * Created by Ameer on 3/3/18.
 */
@Log4j
public class ConversionManager {
    final private DocumentRetriever documentRetriever = new DocumentRetriever();

    private String outputJSON;

    final private String ID_FORMAT = "%s-%s";

    final private ObjectMapper objectMapper = new ObjectMapper();

    public ConversionManager() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void convertAll(@NonNull final List<String> urls) throws ParserException {
        final Shows shows = new Shows();
        final List<Show> showsList = new ArrayList<>();
        for (String url : urls) {
            log.debug("Attempting to parse values from xml url " + url);
            final Document xml = documentRetriever.downloadXML(url);
            showsList.add(populateShow(xml, url));
        }
        shows.setShows(showsList);
        final File resultFile = new File("output.json");
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(resultFile, shows);
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(shows));
        } catch (IOException e) {
            log.error("Error converting shows POJO to json string");
            throw new ParserException(ERROR_SERIALIZING_SHOWS, "Error converting shows POJO to json string");
        }
    }

    private Show populateShow(@NonNull final Document xml, @NonNull final String url) throws ParserException{
        final Show show = new Show();
        final NodeList nodeList = xml.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                final String name = node.getNodeName();
                final String textContent =
                        node.getTextContent()
                                .replaceAll("\\<.*?>","")
                                .replace("\n", "").trim();
                switch (name) {
                    case TITLE:
                        show.setTitle(textContent);
                        break;
                    case AUTHOR:
                        show.setAuthor(textContent);
                        break;
                    case SUMMARY:
                        show.setSummary(textContent);
                        break;
                    case WEBSITE:
                        show.setWebsite(textContent);
                        break;
                    case IMAGE:
                        final NamedNodeMap map = node.getAttributes();
                        show.setArtworkUrl(map.getNamedItem(IMAGE_LINK).getNodeValue());
                        break;
                    default:
                        break;
                }
            }
        }
        show.setShowRSSUrl(url);
        show.setId(getId(show.getTitle().replace(" ","")));
        show.setEpisodes(populateEpisodes(xml, show.getId()));
        return show;
    }

    private String getId(@NonNull final String prefix){
        return String.format(ID_FORMAT, prefix, System.currentTimeMillis());
    }

    private List<Episode> populateEpisodes(@NonNull final Document xml, @NonNull final String showId) {
        final NodeList nodeList = xml.getElementsByTagName(ITEM);
        final List<Episode> episodes = new ArrayList<>();
        for (int i=0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            episodes.add(populateEpisode(node, showId));
        }
        return episodes;
    }

    private Episode populateEpisode(@NonNull final Node node, @NonNull final String showId){
        final Episode episode = new Episode();
        final NodeList childNodes = node.getChildNodes();
        for (int j=0; j < childNodes.getLength(); j++ ) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                // do something with the current element
                final String name = childNode.getNodeName();
                final String textContent =
                        childNode.getTextContent()
                                .replaceAll("\\<.*?>","")
                                .replace("\n", "").trim();

                switch (name) {
                    case TITLE:
                        episode.setTitle(textContent);
                        break;
                    case SUMMARY:
                        episode.setSummary(textContent);
                        break;
                    case AUTHOR:
                        episode.setAuthor(textContent);
                        break;
                    case SEASON:
                        episode.setSeasonNumber(new Integer(textContent));
                        break;
                    case EPISODE:
                        episode.setEpisodeNumber(new Integer(textContent));
                        break;
                    case ENCLOSURE:
                        final NamedNodeMap map = childNode.getAttributes();
                        episode.setUrl(map.getNamedItem(URL).getNodeValue());
                        break;
                    default:
                        break;

                }

            }
        }
        episode.setShowId(showId);
        episode.setId(getId(episode.getTitle().replace(" ", "")));
        return episode;
    }

    public static void main(String[] args) throws ParserException{
        final ConversionManager conversionManager = new ConversionManager();
        final List<String> urls = new ArrayList<String>();
        urls.add("http://feeds.feedburner.com/themessagepodcast.json");
        urls.add("http://whitevault.libsyn.com/rss");
        conversionManager.convertAll(urls);
    }


}
