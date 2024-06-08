package br.ufrn.imd.model;

/**
 * Represents a pairing between two players in a competition, along with the result of their match.
 */
public class Pairing {
    private final String playerOneId;
    private final String playerTwoId;
    private int result;

    public static final int NO_RESULT = -1; // Indicates that no result has been set yet.

    /**
     * Constructs a new Pairing with specified player IDs.
     *
     * @param playerOneId the unique identifier for the first player
     * @param playerTwoId the unique identifier for the second player
     */
    public Pairing(String playerOneId, String playerTwoId) {
        this.playerOneId = playerOneId;
        this.playerTwoId = playerTwoId;
        this.result = NO_RESULT;
    }

    // Getter methods

    /**
     * Returns the player ID for player one.
     * @return player one's ID
     */
    public String getPlayerOneId() {
        return playerOneId;
    }

    /**
     * Returns the player ID for player two.
     * @return player two's ID
     */
    public String getPlayerTwoId() {
        return playerTwoId;
    }

    /**
     * Returns the result of the pairing.
     * @return the result as an integer
     */
    public int getResult() {
        return result;
    }

    // Setter methods

    /**
     * Sets the result of the pairing.
     * @param result the result of the match, must be non-negative.
     */
    public void setResult(int result) {
        if (result < 0) {
            throw new IllegalArgumentException("Result cannot be negative.");
        }
        this.result = result;
    }
}
