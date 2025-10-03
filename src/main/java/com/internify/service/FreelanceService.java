package com.internify.service;

import com.internify.entity.Freelance;
import com.internify.entity.Recruiter;
import com.internify.exception.BadRequestException;
import com.internify.exception.ResourceNotFoundException;
import com.internify.payload.FreelanceRequest;
import com.internify.repository.FreelanceRepository;
import com.internify.repository.RecruiterRepository;
import com.internify.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FreelanceService {
    private final FreelanceRepository freelanceRepository;
    private final RecruiterRepository recruiterRepository;

    public FreelanceService(FreelanceRepository freelanceRepository, RecruiterRepository recruiterRepository) {
        this.freelanceRepository = freelanceRepository;
        this.recruiterRepository = recruiterRepository;
    }

    public Freelance createFreelanceGig(FreelanceRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recruiter recruiter = recruiterRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + currentUser.getId()));

        Freelance freelance = new Freelance();
        freelance.setTitle(request.getTitle());
        freelance.setDescription(request.getDescription());
        freelance.setRequirements(request.getRequirements());
        freelance.setPostedBy(recruiter);
        freelance.setPostedDate(LocalDateTime.now());
        freelance.setStatus("PENDING_APPROVAL");

        return freelanceRepository.save(freelance);
    }
    
    public List<Freelance> getAllFreelanceGigs() {
        return freelanceRepository.findAll();
    }
    
    // ✅ Method added to get gigs by a specific recruiter
    public List<Freelance> getFreelanceGigsByRecruiter(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + recruiterId));
        return freelanceRepository.findByPostedBy(recruiter);
    }
    
    public List<Freelance> findFreelanceGigsByStatus(String status) {
        return freelanceRepository.findByStatus(status);
    }
    
    public Freelance updateFreelanceGigStatus(Long freelanceId, String status) {
        Freelance freelance = freelanceRepository.findById(freelanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Freelance gig not found with ID: " + freelanceId));
        freelance.setStatus(status);
        return freelanceRepository.save(freelance);
    }
    
    public Optional<Freelance> getFreelanceGigById(Long id) {
        return freelanceRepository.findById(id);
    }
    
    // ✅ Method added to delete a gig
    public void deleteFreelanceGig(Long freelanceId) {
        Freelance freelance = freelanceRepository.findById(freelanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Freelance gig not found with ID: " + freelanceId));
        freelanceRepository.delete(freelance);
    }
}