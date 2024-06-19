package br.ufrn.imd.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/decks")
public class CardImageController {

    @GetMapping("/{deckId}/images")
    public ResponseEntity<List<Resource>> getDeckImages(@PathVariable String deckId, @RequestParam String deckList) throws IOException {
        List<Resource> images = new ArrayList<>();
        
        // Split the deckList string into individual card codes
		String[] cardCodes = StringUtils.commaDelimitedListToStringArray(deckList);

		for (String cardCode : cardCodes) {
		    // Extract the collection and card number from the card code
		    String[] parts = cardCode.split("-");
		    if (parts.length != 2) {
		        continue; // Skip invalid card codes
		    }

		    String collection = parts[0];
		    String cardNumber = parts[1];
		    String fileName = collection + "-" + cardNumber + ".png";
		    String filePath = "static/images/Cards/" + collection + "/" + fileName;

		    // Create a ClassPathResource pointing to the image
		    Resource image = new ClassPathResource(filePath);

		    // Check if the image exists and add to the list
		    if (image.exists()) {
		        images.add(image);
		    }
		}

		// Check if any images were found
		if (images.isEmpty()) {
		    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		// Return the list of images
		return new ResponseEntity<>(images, HttpStatus.OK);
    }
}
