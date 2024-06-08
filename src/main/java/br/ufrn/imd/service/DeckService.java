package br.ufrn.imd.service;

import br.ufrn.imd.model.Deck;
import br.ufrn.imd.repository.DeckRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
