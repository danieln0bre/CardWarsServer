package br.ufrn.imd.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/serve-images")
public class TestImageController {

    @GetMapping("/Cards/{collection}/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String collection, @PathVariable String imageName) throws IOException {
        String filePath = "static/images/Cards/" + collection + "/" + imageName;
        Resource image = new ClassPathResource(filePath);

        if (!image.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        String contentType = Files.probeContentType(image.getFile().toPath());
        headers.add("Content-Type", contentType);

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
