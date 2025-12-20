package org.flightsearch.service;

import org.flightsearch.model.Flight;
import org.flightsearch.model.SearchRequest;
import org.flightsearch.model.SearchResponse;
import org.flightsearch.service.client.ProviderAClient;
import org.flightsearch.service.client.ProviderBClient;
import org.flightsearch.service.helper.SearchWorker;
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


    // 2.1: Provider A ve B’den gelen tüm uçuşları birleştirir

    public SearchResponse getAllFlights(SearchRequest request) {
        List<Flight> allFlights = new ArrayList<>();

        // 1. İşçileri oluştur
        SearchWorker workerA = new SearchWorker(providerAClient, request);
        SearchWorker workerB = new SearchWorker(providerBClient, request);

        // 2. Thread'leri tanımla ve başlat
        Thread threadA = new Thread(workerA);
        Thread threadB = new Thread(workerB);

        threadA.start();
        threadB.start();

        try {
            // 3. Main thread'in (bu metodun) devam etmesi için ikisinin de bitmesini bekle
            threadA.join();
            threadB.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Thread kesilmesi durumunda
            return new SearchResponse(true, "Thread interrupted", allFlights);
        }
        Boolean isError = Boolean.FALSE;
        String errorMessage = null;

        // 4. Sonuçları topla
        if (!workerA.getResponse().isHasError()){
            combineResults(allFlights, workerA.getResponse());
        }else {
            isError = true;
            errorMessage = workerA.getResponse().getErrorMessage();
        }

        if (!workerB.getResponse().isHasError()){
            combineResults(allFlights, workerB.getResponse());
        }else {
            isError = true;
            errorMessage = workerB.getResponse().getErrorMessage();
        }
        return new SearchResponse(isError, errorMessage, allFlights);
    }

    /**
     * 2.2: Provider A ve B’den gelen uçuşları birleştirir, gruplayıp en ucuz uçuşu seçer
     */
    public SearchResponse getCheapestGroupedFlights(SearchRequest request) {
        List<Flight> allFlights = new ArrayList<>();
        Boolean isError = Boolean.FALSE;
        String errorMessage = null;

        SearchResponse responseA = providerAClient.availabilitySearch(request);
        if (!responseA.isHasError()) {
            allFlights.addAll(responseA.getFlights());
        }else {
            isError = true;
            errorMessage = responseA.getErrorMessage();
        }

        SearchResponse responseB = providerBClient.availabilitySearch(request);
        if (!responseB.isHasError()) {
            allFlights.addAll(responseB.getFlights());
        }else {
            isError = true;
            errorMessage = responseB.getErrorMessage();
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

        return new SearchResponse(isError, errorMessage, cheapestFlights);
    }

    // Yardımcı metod: Listeye ekleme yaparken hata kontrolü yapar
    private void combineResults(List<Flight> masterList, SearchResponse subResponse) {
        if (subResponse != null && !subResponse.isHasError() && subResponse.getFlights() != null) {
            masterList.addAll(subResponse.getFlights());
        }
    }
}
