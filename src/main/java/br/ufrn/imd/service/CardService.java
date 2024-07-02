package br.ufrn.imd.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CardService {

    public boolean cardExists(String cardCode) throws IOException {
        // Remove any spaces and quotes from the card code
        cardCode = cardCode.replace(" ", "").replace("\"", "").replace("'", "");

        String[] parts = cardCode.split("-");
        if (parts.length != 2) {
            return false; // Invalid card code format
        }

        // Remove quotes from collection and fileName
        String collection = parts[0].replace("\"", "").replace("'", "");
        String fileName = (cardCode + ".png").replace("\"", "").replace("'", "");
        String filePath = "static/images/Cards/" + collection + "/" + fileName;

        Resource image = new ClassPathResource(filePath);

        return image.exists();
    }
}
