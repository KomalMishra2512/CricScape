package com.crick.apis.CricScapeBackend.controllers;

import com.crick.apis.CricScapeBackend.entities.Match;
import com.crick.apis.CricScapeBackend.services.CricketService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cricket")
@CrossOrigin("*")
public class CricketController {

    private CricketService cricketService;

    public CricketController(CricketService cricketService) {
        this.cricketService = cricketService;
    }

    // api for getting live matches

    @GetMapping("/live")
    public ResponseEntity<List<Match>> getLiveMatches() {
        return new ResponseEntity<>(this.cricketService.getLiveMatches(), HttpStatus.OK);
    }
    //public ResponseEntity<?>getLiveMatches() throws InterruptedException {
     //   System.out.println("getting live match");
     //   return new ResponseEntity<>(this.cricketService.getLiveMatches(), HttpStatus.OK);
   // }

    @GetMapping("/point-table")
    public ResponseEntity<?> getCWC2023PointTable() {
        return new ResponseEntity<>(this.cricketService.getCWC2023PointTable(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return new ResponseEntity<>(this.cricketService.getAllMatches(), HttpStatus.OK);
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debugCricbuzz() throws IOException {
    String url = "https://www.cricbuzz.com/cricket-match/live-scores";
        Document document = Jsoup.connect(url)
            .userAgent("Mozilla/5.0")
            .timeout(10000)
            .get();
    return ResponseEntity.ok(document.outerHtml());
}

}