package br.ufrn.imd.service;

import br.ufrn.imd.model.Deck;
import br.ufrn.imd.repository.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;

    @Autowired
    public DeckService(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    public Deck getDeckById(String deckId) {
        return deckRepository.findById(deckId).orElse(null);
    }

    public List<Deck> getAllWinningDecks() {
        return deckRepository.findAll(); // Update this if there's a specific query for winning decks
    }

    public Deck saveDeck(Deck deck) {
        return deckRepository.save(deck);
    }

    public void deleteDeck(String id) {
        if (deckRepository.existsById(id)) {
            deckRepository.deleteById(id);
        } else {
            throw new RuntimeException("Deck not found");
        }
    }
}
