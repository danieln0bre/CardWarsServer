package br.ufrn.imd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;
import java.util.HashMap;

/**
 * Represents a deck with a name and a map of position frequencies used within a MongoDB collection.
 */
@Document(collection = "decks")
public class Deck {
    @Id
    private String id;

    @Field("deck_name")
    private String deckName;
    
    private String deckList;

    @Field("position_frequencies")
    private Map<Integer, Integer> positionFrequencies;

    /**
     * Constructs a new, empty Deck with no name or position frequencies.
     */
    public Deck() {
        this.deckName = "";
        this.positionFrequencies = new HashMap<>();
    }

    /**
     * Constructs a new Deck with specified name and position frequencies.
     *
     * @param deckName the name of the deck
     * @param positionFrequencies a map of position frequencies for the deck
     */
    public Deck(String deckName, Map<Integer, Integer> positionFrequencies) {
        this.deckName = deckName;
        this.positionFrequencies = new HashMap<>(positionFrequencies);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public Map<Integer, Integer> getPositionFrequencies() {
        return new HashMap<>(positionFrequencies);
    }

    public void setPositionFrequencies(Map<Integer, Integer> positionFrequencies) {
        this.positionFrequencies = new HashMap<>(positionFrequencies);
    }

	public String getDeckList() {
		return deckList;
	}

	public void setDeckList(String deckList) {
		this.deckList = deckList;
	}
}
