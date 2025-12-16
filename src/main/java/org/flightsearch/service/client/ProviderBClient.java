package org.flightsearch.service.client;

import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import org.flightsearch.entity.RequestResponseLog;
import org.flightsearch.model.Flight;
import org.flightsearch.model.SearchRequest;
import org.flightsearch.model.SearchResponse;
import org.flightsearch.repository.RequestResponseLogRepository;
import org.flightsearch.service.cache.CacheService;
import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProviderBClient {

    private static final String PROVIDER_B_WSDL_URL = "http://localhost:8082/providerB?wsdl";
    private static final String NAMESPACE = "http://service.flightproviderb.com/";
    private static final String SERVICE_NAME = "FlightProviderBService";
    private static final String PORT_NAME = "FlightProviderBPort";

    private final RequestResponseLogRepository logRepository;
    private final CacheService cacheService;

    public ProviderBClient(RequestResponseLogRepository logRepository, CacheService cacheService) {
        this.logRepository = logRepository;
        this.cacheService = cacheService;
    }

    public SearchResponse availabilitySearch(SearchRequest request) {
        try {
            // --- Log kaydı ---
            RequestResponseLog log = new RequestResponseLog();
            log.setProvider("ProviderB");
            log.setCreatedAt(LocalDateTime.now());

            // --- SOAP request XML ---
            String requestXml = String.format(
                    "<ser:availabilitySearch xmlns:ser=\"%s\">" +
                            "<request>" +
                            "<departure>%s</departure>" +
                            "<arrival>%s</arrival>" +
                            "<departureDate>%s</departureDate>" +
                            "</request>" +
                            "</ser:availabilitySearch>",
                    NAMESPACE,
                    request.getOrigin(),
                    request.getDestination(),
                    request.getDepartureDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );

            log.setRequest(requestXml);

            // --- SOAP service ve dispatch ---
            Service service = Service.create(
                    new java.net.URL(PROVIDER_B_WSDL_URL),
                    new QName(NAMESPACE, SERVICE_NAME)
            );
            Dispatch<Source> dispatch = service.createDispatch(
                    new QName(NAMESPACE, PORT_NAME),
                    Source.class,
                    Service.Mode.PAYLOAD
            );

            // --- SOAP çağrı ---
            StreamSource source = new StreamSource(new ByteArrayInputStream(requestXml.getBytes(StandardCharsets.UTF_8)));
            Source responseSource = dispatch.invoke(source);

             String responsString = responseSourceToString(responseSource);
            // --- Log response ---
            try {
                log.setResponse(responsString);
                logRepository.save(log);
                cacheService.clearAllCaches();

            } catch (Exception e) {
                System.err.println("Log kaydı sırasında hata: " + e.getMessage());
            }

            // --- Response parse ---
            List<String> errors = new ArrayList<>();
            List<Flight> flights = parseResponse(responsString, errors);

            return new SearchResponse(false, null, flights);

        } catch (WebServiceException e) {
            return new SearchResponse(true, "SOAP error: " + e.getMessage(), new ArrayList<>());
        } catch (Exception e) {
            return new SearchResponse(true, "Unexpected error: " + e.getMessage(), new ArrayList<>());
        }
    }

    private List<Flight> parseResponse(String responseSource, List<String> errors) throws Exception {
        List<Flight> flights = new ArrayList<>();

        // --- Source -> DOM Document ---
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(
                responseSource.getBytes(StandardCharsets.UTF_8)
        ));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // --- Hata mesajlarını kontrol et ---
        NodeList errorNodes = doc.getElementsByTagName("errorMessage");
        for (int i = 0; i < errorNodes.getLength(); i++) {
            String msg = errorNodes.item(i).getTextContent();
            if (msg != null && !msg.isEmpty()) {
                errors.add(msg);
            }
        }

        // --- flightOptions ---
        NodeList flightNodes = doc.getElementsByTagName("flightOptions");
        for (int i = 0; i < flightNodes.getLength(); i++) {
            org.w3c.dom.Element flightElem = (org.w3c.dom.Element) flightNodes.item(i);

            String flightNo = getTextContent(flightElem, "flightNumber");
            String origin = getTextContent(flightElem, "departure");
            String destination = getTextContent(flightElem, "arrival");
            String departStr = getTextContent(flightElem, "departuredatetime");
            String arrivalStr = getTextContent(flightElem, "arrivaldatetime");
            String priceStr = getTextContent(flightElem, "price");

            LocalDateTime departure = null;
            LocalDateTime arrival = null;
            BigDecimal price = null;

            try {
                if (departStr != null && !departStr.isEmpty()) {
                    departure = LocalDateTime.parse(departStr, formatter);
                }
            } catch (Exception e) {
                errors.add("Invalid departure datetime format for flight " + flightNo);
            }

            try {
                if (arrivalStr != null && !arrivalStr.isEmpty()) {
                    arrival = LocalDateTime.parse(arrivalStr, formatter);
                }
            } catch (Exception e) {
                errors.add("Invalid arrival datetime format for flight " + flightNo);
            }

            try {
                if (priceStr != null && !priceStr.isEmpty()) {
                    price = new BigDecimal(priceStr);
                }
            } catch (Exception e) {
                errors.add("Invalid price for flight " + flightNo);
            }

            flights.add(new Flight(flightNo, origin, destination, departure, arrival, price));
        }

        return flights;
    }

    // --- Yardımcı method ---
    private String getTextContent(org.w3c.dom.Element parent, String tagName) {
        if (parent.getElementsByTagName(tagName).getLength() > 0) {
            return parent.getElementsByTagName(tagName).item(0).getTextContent();
        }
        return null;
    }

    // --- Source -> String dönüşümü ---
    private String responseSourceToString(Source source) throws Exception {
        javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = tf.newTransformer();
        java.io.StringWriter writer = new java.io.StringWriter();
        transformer.transform(source, new javax.xml.transform.stream.StreamResult(writer));
        return writer.toString();
    }
}
