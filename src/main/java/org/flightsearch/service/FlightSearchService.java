package org.flightsearch.service;

import org.flightsearch.model.Flight;
import org.flightsearch.model.SearchRequest;
import org.flightsearch.model.SearchResponse;
import org.flightsearch.service.client.ProviderAClient;
import org.flightsearch.service.client.ProviderBClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightSearchService {

    private final ProviderAClient providerAClient;
    private final ProviderBClient providerBClient;

    public FlightSearchService(ProviderAClient providerAClient, ProviderBClient providerBClient) {
        this.providerAClient = providerAClient;
        this.providerBClient = providerBClient;
    }

    /**
     * 2.1: Provider A ve B’den gelen tüm uçuşları birleştirir
     */
    public SearchResponse getAllFlights(SearchRequest request) {
        List<Flight> allFlights = new ArrayList<>();

        SearchResponse responseA = providerAClient.availabilitySearch(request);
        if (!responseA.isHasError()) {
            allFlights.addAll(responseA.getFlights());
        }

        SearchResponse responseB = providerBClient.availabilitySearch(request);
        if (!responseB.isHasError()) {
            allFlights.addAll(responseB.getFlights());
        }

        return new SearchResponse(false, null, allFlights);
    }

    /**
     * 2.2: Provider A ve B’den gelen uçuşları birleştirir, gruplayıp en ucuz uçuşu seçer
     */
    public SearchResponse getCheapestGroupedFlights(SearchRequest request) {
        List<Flight> allFlights = new ArrayList<>();

        SearchResponse responseA = providerAClient.availabilitySearch(request);
        if (!responseA.isHasError()) {
            allFlights.addAll(responseA.getFlights());
        }

        SearchResponse responseB = providerBClient.availabilitySearch(request);
        if (!responseB.isHasError()) {
            allFlights.addAll(responseB.getFlights());
        }

        // --- Grup Key: flightNo + origin + destination + departuredatetime + arrivaldatetime ---
        Map<String, Flight> cheapestMap = allFlights.stream()
                .collect(Collectors.toMap(
                        flight -> flight.getFlightNo() + "|" +
                                flight.getOrigin() + "|" +
                                flight.getDestination() + "|",
//                                flight.getDepartureDateTime() + "|" +
//                                flight.getArrivalDateTime(),
                        flight -> flight,
                        (f1, f2) -> f1.getPrice().compareTo(f2.getPrice()) <= 0 ? f1 : f2
                ));

        List<Flight> cheapestFlights = new ArrayList<>(cheapestMap.values());

        return new SearchResponse(false, null, cheapestFlights);
    }
}
