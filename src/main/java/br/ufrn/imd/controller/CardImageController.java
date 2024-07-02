package br.ufrn.imd.controller;

import br.ufrn.imd.model.Deck;
import br.ufrn.imd.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/decks")
public class CardImageController {

    @Autowired
    private DeckService deckService;

    @GetMapping("/{deckId}/images")
    public ResponseEntity<List<String>> getDeckImages(@PathVariable String deckId) throws IOException {
        System.out.println("Received request for deck ID: " + deckId);

        Deck deck = deckService.getDeckById(deckId);
        if (deck == null) {
            System.out.println("Deck not found with ID: " + deckId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String deckList = deck.getDeckList();
        System.out.println("Deck list: " + deckList);

        List<String> imageUrls = new ArrayList<>();

        // Split the deckList string into individual card codes
        String[] cardCodes = StringUtils.commaDelimitedListToStringArray(deckList);
        System.out.println("Card codes: " + java.util.Arrays.toString(cardCodes));

        for (String cardCode : cardCodes) {
            // Remove spaces from the card code
            cardCode = cardCode.trim();
            System.out.println("Processing card code: " + cardCode);

            // Extract the collection and card number from the card code
            String[] parts = cardCode.split("-");
            if (parts.length != 2) {
                System.out.println("Invalid card code format: " + cardCode);
                continue; // Skip invalid card codes
            }

            String collection = parts[0];
            String fileName = cardCode + ".png";
            String filePath = "static/images/Cards/" + collection + "/" + fileName;

            // Create a ClassPathResource pointing to the image
            Resource image = new ClassPathResource(filePath);

            // Check if the image exists and add the URL to the list
            if (image.exists()) {
                System.out.println("Image found: " + filePath);
                String imageUrl = "serve-images/Cards/" + collection + "/" + fileName;
                imageUrls.add(imageUrl);
            } else {
                System.out.println("Image not found: " + filePath);
            }
        }

        // Check if any images were found
        if (imageUrls.isEmpty()) {
            System.out.println("No images found for deck ID: " + deckId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Return the list of image URLs
        System.out.println("Returning " + imageUrls.size() + " images for deck ID: " + deckId);
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }
}
