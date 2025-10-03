package com.internify.controller;

import com.internify.service.MatchingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/match")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/student")
    public ResponseEntity<String> matchStudentToJobs(@RequestParam("resume") MultipartFile resumeFile) {
        if (resumeFile.isEmpty()) {
            return new ResponseEntity<>("Please select a resume file to upload.", HttpStatus.BAD_REQUEST);
        }
        try {
            String matches = matchingService.getStudentJobMatches(resumeFile);
            return new ResponseEntity<>(matches, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error processing resume file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/recruiter")
    public ResponseEntity<String> matchRecruiterToResumes(@RequestParam("jd") MultipartFile jdFile) {
        if (jdFile.isEmpty()) {
            return new ResponseEntity<>("Please select a job description file to upload.", HttpStatus.BAD_REQUEST);
        }
        try {
            String matches = matchingService.getRecruiterResumeMatches(jdFile);
            return new ResponseEntity<>(matches, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error processing JD file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}