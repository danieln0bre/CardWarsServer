package br.ufrn.imd.service;

import br.ufrn.imd.model.*;
import br.ufrn.imd.repository.EventRepository;
import br.ufrn.imd.repository.EventResultRepository;
import br.ufrn.imd.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventResultRepository eventResultRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;
    private final MatchService matchService;
    private final PairingService pairingService;
    private final RankingService rankingService;

    @Autowired
    public EventService(EventRepository eventRepository, EventResultRepository eventResultRepository, PlayerRepository playerRepository,
                        PlayerService playerService, MatchService matchService, PairingService pairingService, RankingService rankingService) {
        this.eventRepository = eventRepository;
        this.eventResultRepository = eventResultRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.matchService = matchService;
        this.pairingService = pairingService;
        this.rankingService = rankingService;
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(id);
    }

    public Optional<Event> getEventByName(String name) {
        return eventRepository.findByName(name);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }
    
    public Event addPlayerToEvent(String eventId, String playerId) {
        Event event = getEventById(eventId).orElseThrow(new java.util.function.Supplier<IllegalArgumentException>() {
            @Override
            public IllegalArgumentException get() {
                return new IllegalArgumentException("Event not found with ID: " + eventId);
            }
        });

        if (event.getPlayerIds().contains(playerId)) {
            throw new IllegalArgumentException("Player already added to the event.");
        }

        event.addPlayerId(playerId);
        return eventRepository.save(event);
    }

    public Event finalizeEvent(String eventId) {
        Event event = getEventById(eventId).orElseThrow(new java.util.function.Supplier<IllegalArgumentException>() {
            @Override
            public IllegalArgumentException get() {
                return new IllegalArgumentException("Event not found with ID: " + eventId);
            }
        });

        event.setFinished(true);
        event = eventRepository.save(event);

        List<Player> players = playerService.getPlayersByIds(event.getPlayerIds());
        if (players.isEmpty()) {
            throw new IllegalStateException("No players found for the event.");
        }

        List<PlayerResult> playerResults = createPlayerResults(players, eventId);
        resetPlayerAttributes(players, eventId);

        saveEventResults(eventId, playerResults);

        return event;
    }

    private List<PlayerResult> createPlayerResults(List<Player> players, String eventId) {
        List<PlayerResult> playerResults = new ArrayList<>();
        for (Player player : players) {
            PlayerResult result = new PlayerResult();
            result.setPlayerId(player.getId());
            result.setEventPoints(player.getEventPoints());
            result.setWinrate(player.getWinrate());
            result.setOpponentIds(player.getOpponentIds());
            result.setDeckId(player.getDeckId());
            playerResults.add(result);
        }
        return playerResults;
    }

    private void resetPlayerAttributes(List<Player> players, String eventId) {
        for (Player player : players) {
            player.setRankPoints(player.getRankPoints() + player.getEventPoints());
            player.setEventPoints(0);
            player.setWinrate(0);
            player.setOpponentsMatchWinrate(0);
            player.clearOpponents();
            player.getAppliedEventsId().remove(eventId);
            player.addEventId(eventId);
            playerRepository.save(player);
        }
    }

    private void saveEventResults(String eventId, List<PlayerResult> playerResults) {
        EventResult eventResult = new EventResult();
        eventResult.setEventId(eventId);
        eventResult.setPlayerResults(playerResults);
        eventResultRepository.save(eventResult);
    }

    public Event finalizeRound(String eventId) {
        Event event = getEventById(eventId).orElseThrow(new java.util.function.Supplier<IllegalArgumentException>() {
            @Override
            public IllegalArgumentException get() {
                return new IllegalArgumentException("Event not found with ID: " + eventId);
            }
        });

        if (event.getCurrentRound() >= event.getNumberOfRounds() + 1) {
            throw new IllegalStateException("All rounds already completed for this event.");
        }

        for (Pairing pairing : event.getPairings()) {
            matchService.updateMatchResult(pairing);
        }
        if (event.getCurrentRound() < event.getNumberOfRounds()) {
            List<Player> players = playerService.getPlayersByIds(event.getPlayerIds());
            List<Pairing> newPairings = pairingService.createPairings(players);
            event.setPairings(newPairings);
        }
        if (event.getCurrentRound() < event.getNumberOfRounds()) {
            event.setCurrentRound(event.getCurrentRound() + 1);
        }

        eventRepository.save(event);
        matchService.updateDeckMatchups(eventId, event.getPairings());

        return event;
    }

    public EventResult getEventResultByEventId(String eventId) {
        return eventResultRepository.findByEventId(eventId)
                .orElseThrow(new java.util.function.Supplier<IllegalArgumentException>() {
                    @Override
                    public IllegalArgumentException get() {
                        return new IllegalArgumentException("Event result not found for ID: " + eventId);
                    }
                });
    }

    public List<PlayerResult> getEventResultRanking(String eventId) {
        EventResult eventResult = getEventResultByEventId(eventId);
        return rankingService.sortByResultEventPoints(eventResult.getPlayerResults());
    }

    public Map<String, Map<String, Double>> getDeckMatchupStatistics(String eventId) {
        EventResult eventResult = getEventResultByEventId(eventId);
        return matchService.getDeckMatchupStatistics(eventResult);
    }
}
