package br.ufrn.imd.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a player with rankings, events, and performance statistics.
 */
@Document(collection = "players")
public class Player extends User {

    private int rankPoints;
    private int eventPoints;
    private double winrate;
    private String deckId;  // Changed from Deck object to String deckId
    private List<String> appliedEventsId;
    private List<String> opponentIds;
    private List<Event> historicoEventos;
    private double opponentsMatchWinrate;

    public Player(String name, String username, String email, String password) {
        super(name, username, email, password, Role.ROLE_PLAYER);
        this.rankPoints = 0;
        this.eventPoints = 0;
        this.winrate = 0.0;
        this.deckId = null;  // Initialize with null
        this.appliedEventsId = new ArrayList<>();
        this.opponentIds = new ArrayList<>();
        this.historicoEventos = new ArrayList<>();
        this.opponentsMatchWinrate = 0.0;
    }

    // Auxiliary methods to manipulate player data.

    public void addEventPoints(int points) {
        this.eventPoints += points;
    }

    public void addEventId(String eventId) {
        appliedEventsId.add(eventId);
    }

    public void addOpponentId(String opponentId) {
        opponentIds.add(opponentId);
    }

    public void removeOpponentId(String opponentId) {
        opponentIds.remove(opponentId);
    }

    // Getters and setters.

    public int getRankPoints() {
        return rankPoints;
    }

    public void setRankPoints(int rankPoints) {
        this.rankPoints = rankPoints;
    }

    public int getEventPoints() {
        return eventPoints;
    }

    public void setEventPoints(int eventPoints) {
        this.eventPoints = eventPoints;
    }

    public double getWinrate() {
        return winrate;
    }

    public void setWinrate(double winrate) {
        this.winrate = winrate;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public boolean hasDeck() {
        return deckId != null && !deckId.trim().isEmpty();
    }

    public List<String> getAppliedEventsId() {
        return new ArrayList<>(appliedEventsId);
    }

    public List<String> getOpponentIds() {
        return new ArrayList<>(opponentIds);
    }

    public List<Event> getHistoricoEventos() {
        return new ArrayList<>(historicoEventos);
    }

    public void addHistoricoEvento(Event event) {
        historicoEventos.add(event);
    }

    public double getOpponentsMatchWinrate() {
        return opponentsMatchWinrate;
    }

    public void setOpponentsMatchWinrate(double opponentsMatchWinrate) {
        this.opponentsMatchWinrate = opponentsMatchWinrate;
    }

    // Clear methods for lists

    public void clearOpponents() {
        opponentIds.clear();
    }

    public void clearAppliedEvents() {
        appliedEventsId.clear();
    }
}
