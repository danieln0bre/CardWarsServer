package br.ufrn.imd.service;

import br.ufrn.imd.model.Deck;
import br.ufrn.imd.repository.DeckRepository;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

    public boolean cardExists(String cardCode) throws IOException {
        // Extract the collection and card number from the card code
        String[] parts = cardCode.split("-");
        if (parts.length != 2) {
            return false; // Invalid card code format
        }

        String collection = parts[0];
        String cardNumber = parts[1];
        String fileName = collection + "-" + cardNumber + ".png";
        String filePath = "static/cards/" + collection + "/" + fileName;

        // Create a ClassPathResource pointing to the image
        Resource image = new ClassPathResource(filePath);

        // Check if the image exists
        return image.exists();
    }
}
