package io.tracker.coronavirus.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.tracker.coronavirus.model.LocationStat;

@Service
public class CoronavirusDataServiceImpl implements CoronavirusDataService {

    private static final Logger log = LoggerFactory.getLogger(CoronavirusDataServiceImpl.class);

    private static final String CORONA_VIRUS_DATA = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    private List<LocationStat> allLocationStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "0 0 */1 * * *")
    public void fetchCoronavirusData() throws URISyntaxException, IOException, InterruptedException {
	List<LocationStat> currentLocationStats = new ArrayList<>();
	HttpClient httpClient = HttpClient.newHttpClient();
	HttpRequest request = HttpRequest.newBuilder(new URI(CORONA_VIRUS_DATA)).build();
	HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	Reader in = new StringReader(response.body());
	Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
	for (CSVRecord record : records) {
	    LocationStat locationStat = new LocationStat();
	    locationStat.setState(record.get("Province/State"));
	    locationStat.setCountry(record.get("Country/Region"));
	    int latestTotolCases = Integer.parseInt(record.get(record.size() - 1));
	    int previousDayCases = Integer.parseInt(record.get(record.size() - 2));
	    locationStat.setLatestTotalCases(latestTotolCases);
	    locationStat.setDiffFromPreviousDay(latestTotolCases - previousDayCases);
	    currentLocationStats.add(locationStat);
	}
	this.allLocationStats = currentLocationStats;
	log.info("---[ Data refreshed ]---");
    }

    @Override
    public List<LocationStat> getAllLocationStats() {
	return allLocationStats;
    }
}
