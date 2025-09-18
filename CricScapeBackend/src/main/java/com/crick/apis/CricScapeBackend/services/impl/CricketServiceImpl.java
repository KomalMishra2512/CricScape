package com.crick.apis.CricScapeBackend.services.impl;

import com.crick.apis.CricScapeBackend.entities.Match;
import com.crick.apis.CricScapeBackend.repositories.MatchRepo;
import com.crick.apis.CricScapeBackend.services.CricketService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class CricketServiceImpl implements CricketService {

    private final MatchRepo matchRepo;

    public CricketServiceImpl(MatchRepo matchRepo) {
        this.matchRepo = matchRepo;
    }

   @Override
public List<Match> getLiveMatches() {
    List<Match> matches = new ArrayList<>();

    try {
        String url = "https://www.cricbuzz.com/cricket-match/live-scores";
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115 Safari/537.36")
                .timeout(10000)
                .get();

        // Each match card is inside this container
        Elements liveScoreElements = document.select("div.cb-mtch-lst.cb-col.cb-col-100.cb-tms-itm");

        for (Element matchElement : liveScoreElements) {
            // Match heading (like "Hungary vs Serbia, 4th Match")
            String teamsHeading = matchElement.select("h3 a").text();

            // Match number + venue (like "4th Match • Szodliget, GB Oval")
            String matchNumberVenue = matchElement.select("span.text-gray").first() != null
                    ? matchElement.select("span.text-gray").first().text()
                    : "";

            // Batting team + score
            Elements matchBatTeamInfo = matchElement.select("div.cb-hmscg-bat-txt");
            String battingTeam = matchBatTeamInfo.select("div.cb-hmscg-tm-nm").text();
            String score = matchBatTeamInfo.select("div.cb-hmscg-tm-nm+div").text();

            // Bowling team + score
            Elements bowlTeamInfo = matchElement.select("div.cb-hmscg-bwl-txt");
            String bowlTeam = bowlTeamInfo.select("div.cb-hmscg-tm-nm").text();
            String bowlTeamScore = bowlTeamInfo.select("div.cb-hmscg-tm-nm+div").text();

            // Status texts
            String textLive = matchElement.select("div.cb-text-live").text();
            String textComplete = matchElement.select("div.cb-text-complete").text();

            // Match link
            String matchLink = matchElement.select("a.cb-lv-scrs-well").attr("href");
            if (!matchLink.startsWith("http")) {
                matchLink = "https://www.cricbuzz.com" + matchLink;
            }

            // Populate model
            Match match = new Match();
            match.setTeamHeading(teamsHeading);
            match.setMatchNumberVenue(matchNumberVenue);
            match.setBattingTeam(battingTeam);
            match.setBattingTeamScore(score);
            match.setBowlTeam(bowlTeam);
            match.setBowlTeamScore(bowlTeamScore);
            match.setLiveText(textLive);
            match.setTextComplete(textComplete);
            match.setMatchLink(matchLink);

            matches.add(match);

            // Save or update in DB
            updateMatch(match);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }

    return matches;
}

private void updateMatch(Match match1) {
    Match existingMatch = this.matchRepo.findByTeamHeading(match1.getTeamHeading()).orElse(null);

    if (existingMatch == null) {
        this.matchRepo.save(match1);
    } else {
        match1.setMatchId(existingMatch.getMatchId());
        this.matchRepo.save(match1);
    }
}



    @Override
    public List<List<String>> getCWC2023PointTable() {
        List<List<String>> pointTable = new ArrayList<>();
        String tableURL = "https://www.cricbuzz.com/cricket-series/6732/icc-cricket-world-cup-2023/points-table";

        try {
            Document document = Jsoup.connect(tableURL).get();
            Elements table = document.select("table.cb-srs-pnts");

            // Extract table headers
            Elements tableHeads = table.select("thead > tr > *");
            List<String> headers = new ArrayList<>();
            for (Element head : tableHeads) {
                headers.add(head.text());
            }
            pointTable.add(headers);

            // Extract table body rows
            Elements bodyTrs = table.select("tbody > tr");
            for (Element tr : bodyTrs) {
                if (tr.hasAttr("class")) {
                    List<String> points = new ArrayList<>();
                    Elements tds = tr.select("td");

                    String team = tds.get(0).select("div.cb-col-84").text();
                    points.add(team);

                    for (Element td : tds) {
                        if (!td.hasClass("cb-srs-pnts-name")) {
                            points.add(td.text());
                        }
                    }

                    pointTable.add(points);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pointTable;
    }

    @Override
    public List<Match> getAllMatches() {
        return this.matchRepo.findAll();
    }
}