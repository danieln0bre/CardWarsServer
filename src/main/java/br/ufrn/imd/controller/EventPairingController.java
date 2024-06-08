package br.ufrn.imd.controller;

import br.ufrn.imd.model.Pairing;
import br.ufrn.imd.model.Player;
import br.ufrn.imd.service.EventService;
import br.ufrn.imd.service.PairingService;
import br.ufrn.imd.service.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// RestController for managing Event-related pairings and operations.
@RestController
@RequestMapping("/api/events")
public class EventPairingController {

    private final EventService eventService;
    private final PlayerService playerService;
    private final PairingService pairingService;

    // Autowired constructor for dependency injection.
    @Autowired
    public EventPairingController(EventService eventService, PlayerService playerService, PairingService pairingService) {
        this.eventService = eventService;
        this.playerService = playerService;
        this.pairingService = pairingService;
    }

    // Starts an event if it has not already started and all players have registered decks.
    @PostMapping("/{eventId}/start")
    public ResponseEntity<String> startEvent(@PathVariable String eventId) {
        return eventService.getEventById(eventId)
                .map(event -> {
                    if (event.getHasStarted()) {
                        return ResponseEntity.badRequest().body("Event has already started.");
                    }

                    if (!playerService.allPlayersHaveDecks(event.getPlayerIds())) {
                        return ResponseEntity.badRequest().body("Not all players have registered decks.");
                    }

                    event.setHasStarted(true);
                    eventService.saveEvent(event);
                    return ResponseEntity.ok("Event started successfully.");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Generates pairings for an event if it has already started.
    @PostMapping("/{eventId}/generatePairings")
    public ResponseEntity<String> generatePairings(@PathVariable String eventId) {
        return eventService.getEventById(eventId)
                .map(event -> {
                    if (!event.getHasStarted()) {
                        return ResponseEntity.badRequest().body("Event has not started yet.");
                    }

                    List<Player> players = playerService.getPlayersByIds(event.getPlayerIds());
                    List<Pairing> pairings = pairingService.createPairings(players);
                    event.setPairings(pairings);
                    eventService.saveEvent(event);
                    return ResponseEntity.ok("Pairings generated successfully.");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Retrieves the pairings for a given event.
    @GetMapping("/{eventId}/pairings")
    public ResponseEntity<List<Pairing>> getEventPairings(@PathVariable String eventId) {
        return eventService.getEventById(eventId)
                .map(event -> ResponseEntity.ok(event.getPairings()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{eventId}/savePairings")
    public ResponseEntity<String> savePairings(@PathVariable String eventId, @RequestBody List<Pairing> pairings) {
        return eventService.getEventById(eventId)
                .map(event -> {
                    event.setPairings(pairings);
                    eventService.saveEvent(event);
                    return ResponseEntity.ok("Pairings saved successfully.");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{eventId}/finalizeRound")
    public ResponseEntity<String> finalizeRound(@PathVariable String eventId) {
        try {
            eventService.finalizeRound(eventId);
            return ResponseEntity.ok("Round finalized and next round prepared for event ID: " + eventId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal Server Error: Unable to finalize round.");
        }
    }
}
