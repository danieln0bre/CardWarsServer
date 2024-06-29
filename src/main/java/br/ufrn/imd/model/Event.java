package br.ufrn.imd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an event stored in MongoDB. Each event includes details such as
 * name, date, location, number of rounds, and lists of player IDs and pairings.
 */
@Document(collection = "event")
public class Event {
    @Id
    private String id;
    private String name;
    private String date;
    private String location;
    private String description;
    private String rules;
    private ArrayList<String> tags;
    private String imagePath;
    private int numberOfParticipants;
    private int numberOfRounds;
    private int currentRound;
    private boolean hasStarted;
    private boolean finished;
    private List<String> playerIds;
    private List<Pairing> pairings;
    private String managerId; 

    /**
     * Constructor for creating a new Event with initial details.
     *
     * @param name            the name of the event
     * @param date            the date of the event
     * @param location        the location of the event
     * @param numberOfRounds  the total number of rounds in the event
     */
    public Event(String name, String date, String location, int numberOfRounds, String managerId) {

        this.name = name;
        this.date = date;
        this.location = location;
        this.description = "Sem descrição.";
        this.rules = "Sem regras.";
        this.tags = new ArrayList<>();
        this.imagePath = "resources/static.images/P/P-051.png";
        this.numberOfParticipants = 0;
        this.numberOfRounds = numberOfRounds;
        this.currentRound = 0;
        this.finished = false;
        this.hasStarted = false;
        this.playerIds = new ArrayList<>();
        this.pairings = new ArrayList<>();
        this.managerId = managerId;
    }

    public Event(String date, String description, String imagePath, String location, String managerId, String name, int numberOfRounds, String rules, ArrayList<String> tags) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.rules = rules;
        this.tags = tags;
        this.imagePath = imagePath;
        this.numberOfParticipants = 0;
        this.numberOfRounds = numberOfRounds;
        this.currentRound = 0;
        this.finished = false;
        this.hasStarted = false;
        this.playerIds = new ArrayList<>();
        this.pairings = new ArrayList<>();
        this.managerId = managerId;
    }

    /**
     * Updates event details from another event object.
     * This method copies all properties from the source event into this event.
     *
     * @param source the source event to copy properties from
     */
    public void updateDetailsFrom(Event source) {
        this.name = source.name;
        this.date = source.date;
        this.location = source.location;
        this.numberOfRounds = source.numberOfRounds;
        this.currentRound = source.currentRound;
        this.finished = source.finished;
        this.hasStarted = source.hasStarted;
        setPlayerIds(new ArrayList<>(source.playerIds));
        setPairings(new ArrayList<>(source.pairings));
    }

    /**
     * Adds a player ID to the event if it is not already included.
     *
     * @param playerId the ID of the player to add
     */
    public void addPlayerId(String playerId) {
        if (!playerIds.contains(playerId)) {
            playerIds.add(playerId);
        }
    }

    // Standard getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean getHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public List<String> getPlayerIds() {
        return Collections.unmodifiableList(playerIds);
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = new ArrayList<>(playerIds);
    }
    
    public List<Pairing> getPairings() {
        return Collections.unmodifiableList(pairings);
    }
    
    public void setPairings(List<Pairing> pairings) {
        this.pairings = new ArrayList<>(pairings);
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
}