package br.ufrn.imd.service;

import br.ufrn.imd.model.Player;
import br.ufrn.imd.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerWinrateService {
    
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerWinrateService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player calculateWinRates(Player player) {
        validatePlayer(player);
        player = calculateWinrate(player);
        return calculateOpponentsMatchWinrate(player);
    }

    private Player calculateWinrate(Player player) {
        int totalMatches = player.getEventPoints();
        double winrate = totalMatches > 0 ? (double) totalMatches / player.getOpponentIds().size() * 100 : 0.0;
        player.setWinrate(winrate);
        return playerRepository.save(player);
    }

    private Player calculateOpponentsMatchWinrate(Player player) {
        List<String> opponentIds = player.getOpponentIds();
        if (opponentIds.isEmpty()) {
            player.setOpponentsMatchWinrate(0.0);
            return playerRepository.save(player);
        }

        List<Player> opponents = fetchPlayers(opponentIds);
        double averageWinrate = calculateAverageWinrate(opponents);
        player.setOpponentsMatchWinrate(averageWinrate);
        return playerRepository.save(player);
    }

    private List<Player> fetchPlayers(List<String> ids) {
        return ids.stream()
                  .map(playerRepository::findById)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .collect(Collectors.toList());
    }

    private double calculateAverageWinrate(List<Player> players) {
        return players.stream()
                      .mapToDouble(Player::getWinrate)
                      .average()
                      .orElse(0.0);
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
    }
}
