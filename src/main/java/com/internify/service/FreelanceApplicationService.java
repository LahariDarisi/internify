package com.internify.service;

import com.internify.entity.Freelance;
import com.internify.entity.FreelanceApplication;
import com.internify.entity.Student;
import com.internify.exception.BadRequestException;
import com.internify.exception.ResourceNotFoundException;
import com.internify.repository.FreelanceApplicationRepository;
import com.internify.repository.FreelanceRepository;
import com.internify.repository.StudentRepository;
import com.internify.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FreelanceApplicationService {
    private final FreelanceApplicationRepository freelanceApplicationRepository;
    private final StudentRepository studentRepository;
    private final FreelanceService freelanceService;
    private final EmailService emailService;

    public FreelanceApplicationService(FreelanceApplicationRepository freelanceApplicationRepository, StudentRepository studentRepository, FreelanceService freelanceService, EmailService emailService) {
        this.freelanceApplicationRepository = freelanceApplicationRepository;
        this.studentRepository = studentRepository;
        this.freelanceService = freelanceService;
        this.emailService = emailService;
    }

    public FreelanceApplication applyForFreelance(Long freelanceId, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        Freelance freelance = freelanceService.getFreelanceGigById(freelanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Freelance gig not found with ID: " + freelanceId));

        if (freelanceApplicationRepository.findByStudentAndFreelance(student, freelance).isPresent()) {
            throw new BadRequestException("Student has already applied for this freelance gig.");
        }

        FreelanceApplication freelanceApplication = new FreelanceApplication();
        freelanceApplication.setStudent(student);
        freelanceApplication.setFreelance(freelance);
        freelanceApplication.setApplicationDate(LocalDateTime.now());
        freelanceApplication.setStatus("APPLIED");
        
        FreelanceApplication savedApplication = freelanceApplicationRepository.save(freelanceApplication);

        // Notify recruiter of a new application
        String subject = "New Application for Your Freelance Gig";
        String htmlBody = "<h1>New Application</h1>"
                        + "<p>Hello " + freelance.getPostedBy().getEmail() + ",</p>"
                        + "<p>A new student has applied for your freelance gig: <strong>" + freelance.getTitle() + "</strong>.</p>"
                        + "<p>Regards,<br>Internify Team</p>";
        
        emailService.sendHtmlEmail(freelance.getPostedBy().getEmail(), subject, htmlBody);

        return savedApplication;
    }
    
    public List<FreelanceApplication> getMyFreelanceApplications(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        return freelanceApplicationRepository.findByStudent(student);
    }
    
    public List<FreelanceApplication> getApplicationsForFreelanceGig(Long freelanceId) {
        Freelance freelance = freelanceService.getFreelanceGigById(freelanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Freelance gig not found with ID: " + freelanceId));
        return freelanceApplicationRepository.findByFreelance(freelance);
    }

    public FreelanceApplication updateApplicationStatusAsRecruiter(Long applicationId, String newStatus, Long recruiterId) {
        FreelanceApplication application = freelanceApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        Long postedById = application.getFreelance().getPostedBy().getId();

        if (!postedById.equals(recruiterId)) {
            throw new BadRequestException("You are not authorized to update this application's status.");
        }

        application.setStatus(newStatus);
        FreelanceApplication updatedApplication = freelanceApplicationRepository.save(application);

        // Send email notification to student
        Student student = updatedApplication.getStudent();
        String subject = "Your application for " + updatedApplication.getFreelance().getTitle() + " has been updated";
        String htmlBody = "<h1>Application Status Update</h1>"
                        + "<p>Dear " + student.getName() + ",</p>"
                        + "<p>The status of your application for the '" + updatedApplication.getFreelance().getTitle() + "' freelance gig has been updated to: <strong>" + newStatus + "</strong>.</p>"
                        + "<p>Regards,<br>Internify Team</p>";
        
        emailService.sendHtmlEmail(student.getEmail(), subject, htmlBody);

        return updatedApplication;
    }
}