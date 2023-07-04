package com.example.demo.info_project;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class InfoProjectController {

    @GetMapping("/about-project")
    public ResponseEntity<String> getAboutProject() {

        return ResponseEntity.ok("About Project Page from new controller");
    }
}
