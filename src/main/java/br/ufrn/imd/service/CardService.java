package br.ufrn.imd.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class CardService {

    public boolean cardExists(String cardCode) throws IOException {
        String[] parts = cardCode.split("-");
        if (parts.length != 2) {
            return false; // Invalid card code format
        }

        String collection = parts[0];
        String cardNumber = parts[1];
        String fileName = collection + "-" + cardNumber + ".png";
        String filePath = "static/cards/" + collection + "/" + fileName;

        Resource image = new ClassPathResource(filePath);

        return image.exists();
    }
}
