package org.flightsearch.service.helper;

import org.flightsearch.model.SearchRequest;
import org.flightsearch.model.SearchResponse;

import java.util.ArrayList;

public class SearchWorker implements Runnable {
    private final BaseProviderClient client;
    private final SearchRequest request;
    private SearchResponse response;

    public SearchWorker(BaseProviderClient client, SearchRequest request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            // Servis çağrısını yap ve sonucu değişkene ata
            this.response = client.availabilitySearch(request);
        } catch (Exception e) {
            // Hata durumunda boş veya error loglu bir response dönmesi için
            this.response = new SearchResponse(true, e.getMessage(), new ArrayList<>());
        }
    }

    public SearchResponse getResponse() {
        return response;
    }
}
