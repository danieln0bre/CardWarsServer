package br.ufrn.imd.model;

import java.util.List;

public class PlayerResult {
    private String playerId;
    private int eventPoints;
    private double winrate;
    private List<String> opponentIds;
    private String deckId;

    // Getters and Setters
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
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

    public List<String> getOpponentIds() {
        return opponentIds;
    }

    public void setOpponentIds(List<String> opponentIds) {
        this.opponentIds = opponentIds;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }
}
