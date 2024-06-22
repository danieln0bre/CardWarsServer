package br.ufrn.imd.service;

import br.ufrn.imd.model.Player;
import br.ufrn.imd.model.Pairing;
import br.ufrn.imd.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PairingService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PairingService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Pairing> createPairings(List<Player> players) {
        validatePlayers(players);
        Collections.sort(players, getRankComparator());

        List<Pairing> pairings = new ArrayList<>();
        Set<String> pairedPlayerIds = new HashSet<>();

        for (Player player1 : players) {
            if (!pairedPlayerIds.contains(player1.getId())) {
                Pairing pairing = createPairForPlayer(player1, players, pairedPlayerIds);
                pairings.add(pairing);
                pairedPlayerIds.add(player1.getId());
                if (!"Bye".equals(pairing.getPlayerTwoId())) {
                    pairedPlayerIds.add(pairing.getPlayerTwoId());
                }
            }
        }
        return pairings;
    }

    private void validatePlayers(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("List of players cannot be null or empty.");
        }
    }

    private Pairing createPairForPlayer(Player player1, List<Player> players, Set<String> pairedPlayerIds) {
        Player player2 = findMatchingPlayer(player1, players, pairedPlayerIds, players.indexOf(player1) + 1);
        if (player2 != null) {
            addOpponentAndSave(player1, player2);
            return new Pairing(player1.getId(), player2.getId());
        } else {
            updatePlayerForBye(player1.getId());
            return new Pairing(player1.getId(), "Bye");
        }
    }

    private Player findMatchingPlayer(Player player, List<Player> players, Set<String> pairedPlayerIds, int startIndex) {
        for (int i = startIndex; i < players.size(); i++) {
            Player potentialOpponent = players.get(i);
            if (!pairedPlayerIds.contains(potentialOpponent.getId()) && !player.getOpponentIds().contains(potentialOpponent.getId())) {
                return potentialOpponent;
            }
        }
        return null;
    }

    private static Comparator<Player> getRankComparator() {
        return Comparator.comparing(Player::getEventPoints, Comparator.reverseOrder())
                         .thenComparing(Player::getRankPoints);
    }

    private void updatePlayerForBye(String playerId) {
        playerRepository.findById(playerId).ifPresent(player -> {
            player.addEventPoints(1);  // Assumes 1 point for a bye.
            playerRepository.save(player);
        });
    }

    private void addOpponentAndSave(Player player1, Player player2) {
        player1.addOpponentId(player2.getId());
        player2.addOpponentId(player1.getId());
        playerRepository.save(player1);
        playerRepository.save(player2);
    }
}
