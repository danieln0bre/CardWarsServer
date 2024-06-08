package br.ufrn.imd.util;

import br.ufrn.imd.model.Player;
import org.springframework.util.StringUtils;

public class PlayerValidationUtil {

    private static final int MAX_LENGTH = 100;

    /**
     * Validates the properties of a Player object to ensure they meet specified criteria.
     * 
     * @param player the Player object to validate.
     * @throws IllegalArgumentException if any properties do not meet the validation criteria.
     */
    public static void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        checkLengthAndPattern(player.getName(), "[a-zA-Z\\s]+", "Player name");
        checkLengthAndPattern(player.getUsername(), "\\w+", "Username");
        checkLengthAndPattern(player.getEmail(), "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}", "Email");
        checkLength(player.getPassword(), "Password");

        validateNonNegative(player.getRankPoints(), "Rank points");
        validateNonNegative(player.getEventPoints(), "Event points");
        validateWinrate(player.getWinrate());
    }

    private static void checkLengthAndPattern(String field, String pattern, String fieldName) {
        if (!StringUtils.hasText(field) || !field.matches(pattern) || field.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(fieldName + " must be non-empty, match pattern " + pattern 
                                               + " and be no more than " + MAX_LENGTH + " characters long.");
        }
    }

    private static void checkLength(String field, String fieldName) {
        if (field != null && field.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(fieldName + " must be no more than " + MAX_LENGTH + " characters long.");
        }
    }

    private static void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
    }

    private static void validateWinrate(double winrate) {
        if (winrate < 0 || winrate > 1) {
            throw new IllegalArgumentException("Winrate must be between 0 and 1.");
        }
    }
}
