package com.internify.controller;

import com.internify.entity.Internship;
import com.internify.entity.User;
import com.internify.service.AdminService;
import com.internify.service.ApplicationService;
import com.internify.service.InternshipService;
import com.internify.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final InternshipService internshipService;
    private final ApplicationService applicationService;
    private final AdminService adminService;

    public AdminController(UserRepository userRepository, InternshipService internshipService, ApplicationService applicationService, AdminService adminService) {
        this.userRepository = userRepository;
        this.internshipService = internshipService;
        this.applicationService = applicationService;
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @GetMapping("/internships/pending")
    public ResponseEntity<List<Internship>> getPendingInternships() {
        return ResponseEntity.ok(internshipService.findByStatus("PENDING_APPROVAL"));
    }

    @DeleteMapping("/internships/{internshipId}")
    public ResponseEntity<String> deleteInternship(@PathVariable Long internshipId) {
        internshipService.deleteInternship(internshipId);
        return ResponseEntity.ok("Internship deleted successfully.");
    }

    // Admin can still manually update an application's status for cleanup, but it's not the primary operation.
//    @PutMapping("/applications/{applicationId}/status")
//    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long applicationId, @RequestParam String status) {
//        return ResponseEntity.ok(applicationService.updateApplicationStatus(applicationId, status, null)); // Null indicates admin override
//    }
    
//    // Admin can still manually update an internship's status for cleanup.
//    @PutMapping("/internships/{internshipId}/status")
//    public ResponseEntity<Internship> updateInternshipStatus(@PathVariable Long internshipId, @RequestParam String status) {
//        return ResponseEntity.ok(internshipService.updateInternshipStatus(internshipId, status, null)); // Null indicates admin override
//    }
}