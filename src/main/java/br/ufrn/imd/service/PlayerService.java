package br.ufrn.imd.service;

import br.ufrn.imd.model.Player;
import br.ufrn.imd.repository.PlayerRepository;
import br.ufrn.imd.util.PlayerValidationUtil;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
//        System.out.println("Validating event ID: " + eventId);
        validateId(eventId, "Event ID");
//        System.out.println("Fetching player by ID: " + playerId);
        Player player = getPlayerById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player not found with ID: " + playerId));
        
//        System.out.println("Player found: " + player);
//        System.out.println("Player ID: " + player.getId());
//        if (player.getDeck() != null) {
//            System.out.println("Player's Deck ID: " + player.getDeck().getId());
//        } else {
//            System.out.println("Player has no deck.");
//        }
        
//        System.out.println("Adding event ID: " + eventId + " to player: " + player);
        player.addEventId(eventId);
//        System.out.println("Player's applied events before save: " + player.getAppliedEventsId());

        Player savedPlayer = playerRepository.save(player);
//        System.out.println("Player saved: " + savedPlayer);
//        System.out.println("Player's applied events after save: " + savedPlayer.getAppliedEventsId());
        return savedPlayer;
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