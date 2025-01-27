package com.etat_charge.controller;

import com.etat_charge.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("upload-file")
@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {

        this.fileService = fileService;
    }


    @GetMapping
    public ResponseEntity<String> readFile() throws IOException {
        String result = this.fileService.readFile();

        return ResponseEntity.ok(result);
    }

}
