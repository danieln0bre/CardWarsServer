package br.ufrn.imd.controller;

import br.ufrn.imd.model.Event;
import br.ufrn.imd.model.EventResult;
import br.ufrn.imd.model.Manager;
import br.ufrn.imd.model.Player;
import br.ufrn.imd.model.PlayerResult;
import br.ufrn.imd.repository.EventResultRepository;
import br.ufrn.imd.service.EventRankingService;
import br.ufrn.imd.service.EventService;
import br.ufrn.imd.service.ManagerService;
import br.ufrn.imd.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final PlayerService playerService;
    private final EventService eventService;
    private final ManagerService managerService;
    private final EventResultRepository eventResultRepository;

    @Autowired
    public EventController(PlayerService playerService, EventService eventService, ManagerService managerService, EventResultRepository eventResultRepository) {
        this.playerService = playerService;
        this.eventService = eventService;
        this.managerService = managerService;
        this.eventResultRepository = eventResultRepository;
    }

    @PostMapping("/createEvent")
    public ResponseEntity<Event> createEvent(@RequestParam String managerId, @RequestBody Event event) {
        if (event.getNumberOfRounds() < 0) {
            return ResponseEntity.badRequest().body(null);
        }
        event.setManagerId(managerId);
        Event savedEvent = eventService.saveEvent(event);
        updateManagerEvents(savedEvent);
        return ResponseEntity.ok(savedEvent);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody Event eventDetails) {
        return eventService.getEventById(id)
                           .map(existingEvent -> {
                               existingEvent.updateDetailsFrom(eventDetails);
                               Event updatedEvent = eventService.saveEvent(existingEvent);
                               updateManagerEvents(updatedEvent);
                               return ResponseEntity.ok(updatedEvent);
                           })
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Event> updateAndSaveEvent(Event existingEvent, Event eventDetails) {
        if (eventDetails.getNumberOfRounds() < 0) {
            return ResponseEntity.badRequest().body(null);
        }
        existingEvent.updateDetailsFrom(eventDetails);
        Event updatedEvent = eventService.saveEvent(existingEvent);
        updateManagerEvents(updatedEvent);
        return ResponseEntity.ok(updatedEvent);
    }

    private void updateManagerEvents(Event event) {
        Manager manager = managerService.getManagerById(event.getManagerId())
                                        .orElseThrow(() -> new IllegalArgumentException("Manager not found with ID: " + event.getManagerId()));
        List<Event> events = manager.getEvents();
        events.removeIf(e -> e.getId().equals(event.getId())); // Remove the old event
        events.add(event); // Add the updated event
        manager.setEvents(events);
        managerService.saveManager(manager);
    }

    @GetMapping("/")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Event> getEventByName(@PathVariable String name) {
        return eventService.getEventByName(name.replace("-", " "))
               .map(ResponseEntity::ok)
               .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/get")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        return eventService.getEventById(id)
                           .map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        return eventService.getEventById(id)
                           .map(event -> {
                               eventService.deleteEvent(id);
                               return ResponseEntity.ok().<Void>build();
                           })
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/rankings")
    public ResponseEntity<List<Player>> getEventRankings(@PathVariable String id) {
        return eventService.getEventById(id)
                           .map(event -> {
                               List<Player> players = playerService.getPlayersByIds(event.getPlayerIds());
                               return ResponseEntity.ok(EventRankingService.sortByEventPoints(players));
                           })
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{eventId}/finalize")
    public ResponseEntity<?> finalizeEvent(@PathVariable String eventId) {
        try {
            Event event = eventService.finalizeEvent(eventId);
            return ResponseEntity.ok("Event finalized successfully with ID: " + event.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal Server Error: Unable to finalize event.");
        }
    }
    
    
    @GetMapping("/{id}/results")
    public ResponseEntity<EventResult> getEventResults(@PathVariable String id) {
        try {
            EventResult eventResult = eventService.getEventResultByEventId(id);
            return ResponseEntity.ok(eventResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}/result-ranking")
    public ResponseEntity<List<PlayerResult>> getEventResultRanking(@PathVariable String id) {
        try {
            List<PlayerResult> ranking = eventService.getEventResultRanking(id);
            return ResponseEntity.ok(ranking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}/deck-matchups")
    public ResponseEntity<Map<String, Map<String, Double>>> getDeckMatchups(@PathVariable String id) {
        try {
            Map<String, Map<String, Double>> matchups = eventService.getDeckMatchupStatistics(id);
            return ResponseEntity.ok(matchups);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    
    @GetMapping("/manager/{managerId}/events")
    public ResponseEntity<List<Event>> getEventsByManagerId(@PathVariable String managerId) {
        try {
            List<Event> events = managerService.getManagerEvents(managerId);
            return ResponseEntity.ok(events);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
