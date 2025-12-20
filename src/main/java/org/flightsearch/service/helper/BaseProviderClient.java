package org.flightsearch.service.helper;

import org.flightsearch.model.SearchRequest;
import org.flightsearch.model.SearchResponse;

public interface BaseProviderClient {
    SearchResponse availabilitySearch(SearchRequest request);
}
