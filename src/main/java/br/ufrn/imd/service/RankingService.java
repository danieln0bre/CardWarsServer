package br.ufrn.imd.service;

import br.ufrn.imd.model.Player;
import br.ufrn.imd.model.PlayerResult;
import br.ufrn.imd.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final PlayerRepository playerRepository;

    @Autowired
    public RankingService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getRankedPlayersByRankPoints() {
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            throw new IllegalArgumentException("No players found in the database.");
        }
        players.sort(Collections.reverseOrder(Comparator.comparingInt(Player::getRankPoints)));
        return players;
    }

    public List<PlayerResult> sortByResultEventPoints(List<PlayerResult> playerResults) {
        return playerResults.stream()
                .sorted(Comparator.comparingInt(PlayerResult::getEventPoints).reversed())
                .collect(Collectors.toList());
    }
    
    public List<Player> sortByEventPoints(List<Player> players) {
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                int eventPointsComparison = Integer.compare(p2.getEventPoints(), p1.getEventPoints());
                if (eventPointsComparison != 0) {
                    return eventPointsComparison;
                }
                return Double.compare(p2.getOpponentsMatchWinrate(), p1.getOpponentsMatchWinrate());
            }
        });
        return players;
    }
}
