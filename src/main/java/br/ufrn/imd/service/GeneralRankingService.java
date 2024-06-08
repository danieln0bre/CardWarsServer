package br.ufrn.imd.service;

import br.ufrn.imd.model.Player;
import br.ufrn.imd.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class GeneralRankingService {

    private final PlayerRepository playerRepository;

    @Autowired
    public GeneralRankingService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Retrieves all players from the database and sorts them by rank points in descending order.
     *
     * @return a list of players sorted by rank points
     * @throws IllegalArgumentException if no players are found
     */
    public List<Player> getRankedPlayersByRankPoints() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            throw new IllegalArgumentException("No players found in the database.");
        }
        players.sort(Collections.reverseOrder(Comparator.comparingInt(Player::getRankPoints)));
        return players;
    }
}
