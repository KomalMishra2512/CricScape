package com.crick.apis.CricScapeBackend.repositories;

import com.crick.apis.CricScapeBackend.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepo extends JpaRepository<com.crick.apis.CricScapeBackend.entities.Match,Integer> {

    Optional<Match> findByTeamHeading(String teamHeading);

}
