package br.ufrn.imd.controller;

import br.ufrn.imd.model.Deck;
import br.ufrn.imd.model.Event;
import br.ufrn.imd.model.Player;
import br.ufrn.imd.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// RestController for managing Player-related operations.
@RestController
@RequestMapping("/api/players")
public class PlayerController {

    // Service dependencies for various player-related operations.
    private final RankingService rankingService;
    private final PlayerService playerService;
    private final EventService eventService;
    private final PlayerWinrateService winrateService;
    private final DeckService deckService;
    private final CardService cardService;

    // Autowired constructor for dependency injection.
    @Autowired
    public PlayerController(RankingService rankingService, PlayerService playerService,
                            EventService eventService, PlayerWinrateService winrateService, DeckService deckService, CardService cardService) {
        this.rankingService = rankingService;
        this.playerService = playerService;
        this.eventService = eventService;
        this.winrateService = winrateService;
        this.deckService = deckService;
        this.cardService = cardService;
    }

    // Updates a player's information.
    @PutMapping("/{id}/update")
    public ResponseEntity<Player> updatePlayerInfo(@PathVariable String id, @RequestBody Player userDetails) {
        return playerService.getPlayerById(id)
                .map(player -> {
                    updatePlayerFields(player, userDetails);
                    return ResponseEntity.ok(playerService.updatePlayer(id, player));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Helper method to update player fields if new values are provided.
    private void updatePlayerFields(Player player, Player userDetails) {
        Optional.ofNullable(userDetails.getEmail()).ifPresent(player::setEmail);
        Optional.ofNullable(userDetails.getUsername()).ifPresent(player::setUsername);
        Optional.ofNullable(userDetails.getPassword()).ifPresent(player::setPassword);
    }

    // Retrieves a player by their ID.
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Recalculates a player's win rates.
    @PostMapping("/{id}/recalculateWinrates")
    public ResponseEntity<Player> recalculateWinrates(@PathVariable String id) {
        return playerService.getPlayerById(id)
                .map(player -> ResponseEntity.ok(playerService.recalculateWinrates(id)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Retrieves the events a player is associated with.
    @GetMapping("/{id}/events")
    public ResponseEntity<List<Event>> getPlayerEvents(@PathVariable String id) {
        return playerService.getPlayerById(id)
                .map(player -> ResponseEntity.ok(fetchEvents(player.getAppliedEventsId())))
                .orElse(ResponseEntity.notFound().build());
    }

    // Helper method to fetch events based on event IDs.
    private List<Event> fetchEvents(List<String> eventIds) {
        return eventIds.stream()
                .map(eventId -> eventService.getEventById(eventId)
                        .orElseThrow(() -> new RuntimeException("Event not found for ID: " + eventId)))
                .collect(Collectors.toList());
    }
    
    @PutMapping("/{id}/events/add")
    public synchronized ResponseEntity<String> addEventToPlayer(@PathVariable String id, @RequestBody String eventId) {
        eventId = eventId.replaceAll("\"", "");

        System.out.println("Request received to add event with ID: " + eventId + " to player with ID: " + id);
        String finalEventId = eventId;
        return eventService.getEventById(eventId.trim())
                .map(event -> {
                    System.out.println("Event found: " + event);
                    try {
                        checkAndAddEventToPlayer(id, event);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao registrar o evento");
                    }
                    return ResponseEntity.ok("Player and Event updated successfully!");
                })
                .orElseGet(() -> {
                    System.out.println("Event not found for ID: " + finalEventId);
                    return ResponseEntity.badRequest().body("Event not found.");
                });
    }

    // Helper method to check if an event can be added to a player and update both player and event.
    private void checkAndAddEventToPlayer(String playerId, Event event) {
        System.out.println("Checking if player with ID: " + playerId + " is already registered for event: " + event);
        if (event.getPlayerIds().contains(playerId)) {
            throw new IllegalArgumentException("Player is already registered for this event.");
        }
        if (event.isFull()) {
            throw new IllegalArgumentException("Event is capped.");
        }
        System.out.println("Adding event to player...");
        playerService.addEventToPlayer(playerId, event.getId());
        System.out.println("Adding player to event...");
        eventService.addPlayerToEvent(event.getId(), playerId);
    }

    // Retrieves the general rankings of players.
    @GetMapping("/rankings")
    public ResponseEntity<List<Player>> getGeneralRankings() {
        List<Player> rankedPlayers = rankingService.getRankedPlayersByRankPoints();
        return rankedPlayers.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(rankedPlayers);
    }
    
    @GetMapping("/decks/{deckId}")
    public ResponseEntity<Deck> getDeckById(@PathVariable String deckId) {
        Deck deck = deckService.getDeckById(deckId);
        if (deck != null) {
            return ResponseEntity.ok(deck);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/winning-decks")
    public ResponseEntity<List<Deck>> getWinningDecks() {
        List<Deck> decks = deckService.getAllWinningDecks();
        return ResponseEntity.ok(decks);
    }

    @PostMapping("/{id}/registerDeck")
    public ResponseEntity<String> registerDeck(@PathVariable String id, @RequestParam String deckName, @RequestParam String deckList) {
        return playerService.getPlayerById(id)
                .map(player -> {
                    try {
                        // Clean deckName and deckList by removing all quotes
                        String deckNameFixed = deckName.replace("\"", "").replace("'", "");
                        String deckListFixed = deckList.replace("\"", "").replace("'", "");

                        // Validate if all card codes exist
                        String[] cardCodes = StringUtils.commaDelimitedListToStringArray(deckList);
                        for (String cardCode : cardCodes) {
                            if (!cardService.cardExists(cardCode)) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid card code: " + cardCode);
                            }
                        }

                        // Create and save the deck
                        Deck deck = new Deck(deckNameFixed, new HashMap<>());
                        deck.setDeckList(deckListFixed);
                        deckService.saveDeck(deck);

                        // Update player with new deck
                        player.setDeckId(deck.getId());
                        playerService.savePlayer(player);

                        return ResponseEntity.status(HttpStatus.CREATED).body("Deck registered successfully");
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while registering the deck");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    
    @DeleteMapping("/{id}/deleteDeck")
    public ResponseEntity<String> deleteDeck(@PathVariable String id) {
        return playerService.getPlayerById(id)
                .map(player -> {
                    String deckId = player.getDeckId();
                    if (deckId != null) {
                        deckService.deleteDeck(deckId);
                        player.setDeckId(null);
                        playerService.savePlayer(player);
                        return ResponseEntity.ok("Deck deleted successfully");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No deck to delete");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
