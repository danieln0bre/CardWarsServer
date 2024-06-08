package br.ufrn.imd.service;

import br.ufrn.imd.model.Player;
import br.ufrn.imd.model.PlayerResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * Provides functionality to rank players based on event points and opponents' win rates.
 */

@Service
public class EventRankingService {
    
    /**
     * Sorts a list of players based on their event points and opponents' match win rates.
     * 
     * @param players the list of players to sort
     * @return a sorted list of players based on defined criteria
     * @throws IllegalArgumentException if the input list is null
     */
    public static List<Player> sortByEventPoints(List<Player> players) {
        if (players == null) {
            throw new IllegalArgumentException("List of players cannot be null.");
        }

        List<Player> sortedPlayers = new ArrayList<>(players);
        Collections.sort(sortedPlayers, new EventPointsAndOpponentMatchWinrateComparator());
        return sortedPlayers;
    }
    
    public List<PlayerResult> sortByResultEventPoints(List<PlayerResult> playerResults) {
        return playerResults.stream()
                .sorted(Comparator.comparingInt(PlayerResult::getEventPoints).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Comparator for ranking players based on event points in descending order,
     * and if ties occur, by opponents' match win rates in descending order.
     */
    private static class EventPointsAndOpponentMatchWinrateComparator implements Comparator<Player> {
        @Override
        public int compare(Player p1, Player p2) {
            int eventPointsComparison = Integer.compare(p2.getEventPoints(), p1.getEventPoints());
            if (eventPointsComparison != 0) {
                return eventPointsComparison;
            }
            return Double.compare(p2.getOpponentsMatchWinrate(), p1.getOpponentsMatchWinrate());
        }
    }
}
