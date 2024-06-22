package br.ufrn.imd.service;

import br.ufrn.imd.model.Player;
import br.ufrn.imd.repository.PlayerRepository;
import br.ufrn.imd.util.PlayerValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerWinrateService winrateService;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PlayerWinrateService winrateService) {
        this.playerRepository = playerRepository;
        this.winrateService = winrateService;
    }

    public Player createPlayer(Player player) {
        PlayerValidationUtil.validatePlayer(player);
        player = winrateService.calculateWinRates(player);
        return playerRepository.save(player);
    }

    public Player updatePlayer(String id, Player playerDetails) {
        PlayerValidationUtil.validatePlayer(playerDetails);
        playerDetails.setId(id);
        return createPlayer(playerDetails);
    }

    public void updatePlayerOpponents(String playerId, String opponentId) {
        validateId(opponentId, "Opponent ID");
        Player player = getPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));
        
        player.addOpponentId(opponentId);
        playerRepository.save(player);
    }

    public Optional<Player> getPlayerById(String id) {
        return playerRepository.findById(id);
    }

    public List<Player> getPlayersByIds(List<String> playerIds) {
        validatePlayerIds(playerIds);
        return playerRepository.findAllById(playerIds);
    }

    public Player addEventToPlayer(String playerId, String eventId) {
        validateId(eventId, "Event ID");
        Player player = getPlayerById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player not found with ID: " + playerId));
        
        player.addEventId(eventId);
        return playerRepository.save(player);
    }

    public boolean allPlayersHaveDecks(List<String> playerIds) {
        validatePlayerIds(playerIds);
        List<Player> players = getPlayersByIds(playerIds);
        return players.stream().allMatch(Player::hasDeck);
    }

    public List<Player> saveAll(List<Player> players) {
        validatePlayerIdsList(players);
        return playerRepository.saveAll(players);
    }

    private void validateId(String id, String description) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(description + " cannot be null or empty.");
        }
    }

    private void validatePlayerIds(List<String> playerIds) {
        if (playerIds == null || playerIds.isEmpty()) {
            throw new IllegalArgumentException("Player IDs list cannot be null or empty.");
        }
    }

    private void validatePlayerIdsList(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("The list of players cannot be empty.");
        }
    }

    public Player recalculateWinrates(String playerId) {
        Player player = getPlayerById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player not found with ID: " + playerId));
        player = winrateService.calculateWinRates(player);
        return savePlayer(player);
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }
}
