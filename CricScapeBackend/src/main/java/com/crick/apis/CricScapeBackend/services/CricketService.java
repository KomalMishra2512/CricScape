package com.crick.apis.CricScapeBackend.services;

import com.crick.apis.CricScapeBackend.entities.Match;

import java.util.List;
import java.util.Map;

public interface CricketService {

    List<Match> getLiveMatches();
    List<List<String>> getCWC2023PointTable();

    List<Match> getAllMatches();


}