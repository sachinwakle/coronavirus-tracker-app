package io.tracker.coronavirus.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.tracker.coronavirus.model.LocationStat;
import io.tracker.coronavirus.service.CoronavirusDataService;

@Controller
public class CoronavirusResource {
    @Autowired
    CoronavirusDataService coronavirusDataService;

    @GetMapping("/")
    public String fetchCoronavirusReport(Model model) {
	List<LocationStat> locationStats = coronavirusDataService.getAllLocationStats();
	int totalReportedCases = locationStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
	int totalNewCasesReported = locationStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();

	Optional<LocationStat> statForIndia = locationStats.stream()
		.filter(stat -> stat.getCountry().equalsIgnoreCase("India")).findFirst();

	model.addAttribute("locationStats", locationStats);
	model.addAttribute("totalReportedCases", totalReportedCases);
	model.addAttribute("totalNewCasesReported", totalNewCasesReported);
	model.addAttribute("statForIndia", statForIndia.get());
	return "home";
    }
}
