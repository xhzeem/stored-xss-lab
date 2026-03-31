package com.xsslab.hrportal;

import com.xsslab.hrportal.model.*;
import com.xsslab.hrportal.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder {
    private final JdbcTemplate jdbc;
    private final UserRepository userRepo;
    private final ApplicantRepository applicantRepo;
    private final InterviewNoteRepository interviewRepo;
    private final EmployeeBioRepository bioRepo;
    private final JobPostingRepository jobRepo;
    private final AnnouncementRepository announcementRepo;
    private final CalendarEventRepository calendarRepo;
    private final HrTicketRepository ticketRepo;
    private final AuditLogRepository auditRepo;
    private final OnboardingTaskRepository onboardingRepo;
    private final WikiPageRepository wikiRepo;

    public DatabaseSeeder(JdbcTemplate jdbc,
                          UserRepository userRepo,
                          ApplicantRepository applicantRepo,
                          InterviewNoteRepository interviewRepo,
                          EmployeeBioRepository bioRepo,
                          JobPostingRepository jobRepo,
                          AnnouncementRepository announcementRepo,
                          CalendarEventRepository calendarRepo,
                          HrTicketRepository ticketRepo,
                          AuditLogRepository auditRepo,
                          OnboardingTaskRepository onboardingRepo,
                          WikiPageRepository wikiRepo) {
        this.jdbc = jdbc;
        this.userRepo = userRepo;
        this.applicantRepo = applicantRepo;
        this.interviewRepo = interviewRepo;
        this.bioRepo = bioRepo;
        this.jobRepo = jobRepo;
        this.announcementRepo = announcementRepo;
        this.calendarRepo = calendarRepo;
        this.ticketRepo = ticketRepo;
        this.auditRepo = auditRepo;
        this.onboardingRepo = onboardingRepo;
        this.wikiRepo = wikiRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        seedUsers();
        seedApplicants();
        seedInterviewNotes();
        seedEmployeeBios();
        seedJobPostings();
        seedAnnouncements();
        seedCalendarEvents();
        seedHrTickets();
        seedAuditLogs();
        seedOnboardingTasks();
        seedWikiPages();
    }

    private void seedUsers() {
        userRepo.save(new User("user", "user123", "user"));
        userRepo.save(new User("mod", "mod123", "moderator"));
        userRepo.save(new User("admin", "admin123", "admin"));
        userRepo.save(new User("hr1", "hr123", "hr"));
        userRepo.save(new User("hr2", "hr123", "hr"));
    }

    private void seedApplicants() {
        applicantRepo.save(new Applicant("Alice Johnson", "alice@example.com", "Senior Developer", "Experienced full-stack developer with 8 years of Java and React experience.", "new"));
        applicantRepo.save(new Applicant("Bob Williams", "bob@example.com", "DevOps Engineer", "Skilled in AWS, Docker, Kubernetes, and CI/CD pipelines.", "reviewing"));
        applicantRepo.save(new Applicant("Carol Martinez", "carol@example.com", "UI/UX Designer", "Creative designer with a portfolio of enterprise applications.", "interview"));
        applicantRepo.save(new Applicant("David Chen", "david@example.com", "Data Analyst", "Strong background in SQL, Python, and data visualization tools.", "new"));
        applicantRepo.save(new Applicant("Emma Davis", "emma@example.com", "Project Manager", "PMP certified with experience managing agile teams of 10+ people.", "hired"));
        applicantRepo.save(new Applicant("Frank Miller", "frank@example.com", "QA Engineer", "Automated testing specialist with Selenium and Cypress expertise.", "rejected"));
    }

    private void seedInterviewNotes() {
        interviewRepo.save(new InterviewNote(1, "hr1", "Strong technical skills. Good communication. Would be a great addition to the backend team.", 4));
        interviewRepo.save(new InterviewNote(2, "hr2", "Excellent knowledge of cloud infrastructure. Needs improvement on networking concepts.", 3));
        interviewRepo.save(new InterviewNote(3, "admin", "Very creative portfolio. Strong understanding of user-centered design principles.", 5));
        interviewRepo.save(new InterviewNote(4, "hr1", "Good analytical mindset. Could improve on advanced SQL queries.", 3));
        interviewRepo.save(new InterviewNote(5, "hr2", "Outstanding leadership experience. Very organized and detail-oriented.", 5));
        interviewRepo.save(new InterviewNote(6, "mod", "Decent automation skills but lacks experience with performance testing.", 2));
    }

    private void seedEmployeeBios() {
        bioRepo.save(new EmployeeBio("Sarah Thompson", "Engineering", "Full-stack developer passionate about clean code and mentoring junior developers. Previously worked at Google and Amazon.", "Rock climbing, chess, reading sci-fi novels"));
        bioRepo.save(new EmployeeBio("Mike Rodriguez", "Marketing", "Digital marketing specialist with expertise in SEO and content strategy. MBA from Stanford.", "Surfing, photography, cooking"));
        bioRepo.save(new EmployeeBio("Lisa Park", "HR", "HR generalist focused on employee engagement and talent acquisition. 10 years of experience.", "Yoga, painting, traveling"));
        bioRepo.save(new EmployeeBio("James Wilson", "Finance", "Financial analyst with CPA certification. Specializes in budget forecasting and risk assessment.", "Golf, fishing, woodworking"));
        bioRepo.save(new EmployeeBio("Anna Kowalski", "Engineering", "Backend engineer specializing in distributed systems and microservices architecture.", "Running, board games, podcasting"));
        bioRepo.save(new EmployeeBio("Tom Chang", "Sales", "Senior sales executive with a track record of exceeding targets by 150% year over year.", "Basketball, cooking, stand-up comedy"));
    }

    private void seedJobPostings() {
        jobRepo.save(new JobPosting("Senior Java Developer", "Engineering", "We are looking for an experienced Java developer to join our backend team. You will work on building scalable microservices.", "5+ years Java experience, Spring Boot, PostgreSQL, AWS"));
        jobRepo.save(new JobPosting("Product Manager", "Product", "Lead product development initiatives and work closely with engineering and design teams.", "3+ years PM experience, Agile methodology, strong analytical skills"));
        jobRepo.save(new JobPosting("UX Designer", "Design", "Create intuitive user experiences for our enterprise products.", "Figma expertise, user research, prototyping"));
        jobRepo.save(new JobPosting("Data Engineer", "Data", "Build and maintain data pipelines and ETL processes.", "Python, Spark, Airflow, SQL"));
        jobRepo.save(new JobPosting("DevOps Engineer", "Infrastructure", "Manage our cloud infrastructure and CI/CD pipelines.", "AWS, Terraform, Kubernetes, Jenkins"));
        jobRepo.save(new JobPosting("Technical Writer", "Documentation", "Create and maintain technical documentation for internal and external use.", "Excellent writing skills, API documentation, Markdown"));
    }

    private void seedAnnouncements() {
        announcementRepo.save(new Announcement("admin", "Q4 All-Hands Meeting", "The quarterly all-hands meeting is scheduled for next Friday at 2 PM. All employees are encouraged to attend."));
        announcementRepo.save(new Announcement("hr1", "New Benefits Package", "We are excited to announce an updated benefits package starting next month, including enhanced dental coverage."));
        announcementRepo.save(new Announcement("admin", "Office Closure - Holiday", "The office will be closed on December 25th and January 1st for the holidays. Enjoy the break!"));
        announcementRepo.save(new Announcement("hr2", "Employee Wellness Program", "Introducing our new wellness program with gym membership subsidies and mental health support."));
        announcementRepo.save(new Announcement("mod", "Security Training Reminder", "All employees must complete the annual security awareness training by end of this month."));
        announcementRepo.save(new Announcement("admin", "New Parking Arrangements", "Due to construction, parking lot B will be unavailable. Please use lot C starting Monday."));
    }

    private void seedCalendarEvents() {
        calendarRepo.save(new CalendarEvent("Sprint Planning", "Plan the upcoming 2-week sprint for the engineering team.", "2026-04-01", "Sarah Thompson"));
        calendarRepo.save(new CalendarEvent("HR Interview - Alice Johnson", "Second round interview for Senior Developer position.", "2026-04-02", "Lisa Park"));
        calendarRepo.save(new CalendarEvent("Team Lunch", "Monthly team building lunch at the downtown restaurant.", "2026-04-05", "Mike Rodriguez"));
        calendarRepo.save(new CalendarEvent("Budget Review Meeting", "Quarterly review of department budgets with finance.", "2026-04-08", "James Wilson"));
        calendarRepo.save(new CalendarEvent("Product Demo", "Demo of new features to stakeholders and clients.", "2026-04-10", "Anna Kowalski"));
        calendarRepo.save(new CalendarEvent("Onboarding Session", "New employee orientation and system setup.", "2026-04-12", "Lisa Park"));
    }

    private void seedHrTickets() {
        ticketRepo.save(new HrTicket("Sarah Thompson", "Payroll discrepancy", "I noticed a discrepancy in my last paycheck. The overtime hours don't match my timesheet.", "open"));
        ticketRepo.save(new HrTicket("Mike Rodriguez", "Vacation request", "Requesting time off from April 15-22 for family vacation.", "approved"));
        ticketRepo.save(new HrTicket("Lisa Park", "IT equipment upgrade", "My laptop is running slow and needs to be replaced.", "in_progress"));
        ticketRepo.save(new HrTicket("James Wilson", "Expense reimbursement", "Need to submit receipts from last month's business trip.", "open"));
        ticketRepo.save(new HrTicket("Anna Kowalski", "Remote work setup", "Requesting approval for permanent remote work arrangement.", "pending"));
        ticketRepo.save(new HrTicket("Tom Chang", "Training budget", "Inquiry about available training budget for cloud certification.", "open"));
    }

    private void seedAuditLogs() {
        auditRepo.save(new AuditLog("admin", "user_created", "Created new user account for newhire1"));
        auditRepo.save(new AuditLog("hr1", "applicant_status_changed", "Changed applicant Alice Johnson status from 'new' to 'interview'"));
        auditRepo.save(new AuditLog("mod", "announcement_posted", "Posted new announcement: Security Training Reminder"));
        auditRepo.save(new AuditLog("admin", "system_backup", "Initiated daily database backup process"));
        auditRepo.save(new AuditLog("hr2", "interview_scheduled", "Scheduled interview for candidate Bob Williams on April 3rd"));
        auditRepo.save(new AuditLog("user", "password_change", "User changed their password successfully"));
        auditRepo.save(new AuditLog("admin", "role_updated", "Updated role for user jdoe from 'user' to 'moderator'"));
    }

    private void seedOnboardingTasks() {
        onboardingRepo.save(new OnboardingTask("Alice Johnson", "Complete HR paperwork", "Fill out tax forms, emergency contacts, and benefits enrollment.", false));
        onboardingRepo.save(new OnboardingTask("Alice Johnson", "IT setup", "Receive laptop, set up email, and configure development environment.", false));
        onboardingRepo.save(new OnboardingTask("Alice Johnson", "Team introduction", "Meet with team lead and attend department meeting.", true));
        onboardingRepo.save(new OnboardingTask("Bob Williams", "Security training", "Complete mandatory security awareness training module.", true));
        onboardingRepo.save(new OnboardingTask("Bob Williams", "Access permissions", "Get access to JIRA, Confluence, and relevant repositories.", false));
        onboardingRepo.save(new OnboardingTask("Carol Martinez", "Design tools setup", "Install Figma, Adobe Creative Suite, and set up design system access.", false));
        onboardingRepo.save(new OnboardingTask("David Chen", "Data access", "Request read-only access to analytics databases.", false));
    }

    private void seedWikiPages() {
        wikiRepo.save(new WikiPage("Employee Handbook", "hr1", "Welcome to the company! This handbook covers all policies and procedures you need to know."));
        wikiRepo.save(new WikiPage("Development Setup Guide", "Sarah Thompson", "Step-by-step guide to setting up your local development environment with all required tools."));
        wikiRepo.save(new WikiPage("VPN Configuration", "IT Support", "Instructions for configuring VPN access for remote work."));
        wikiRepo.save(new WikiPage("Expense Policy", "finance", "Guidelines for submitting expense reports and reimbursement procedures."));
        wikiRepo.save(new WikiPage("Code Review Guidelines", "Anna Kowalski", "Best practices for conducting code reviews and providing constructive feedback."));
        wikiRepo.save(new WikiPage("Onboarding Checklist", "hr2", "Complete checklist for new employee onboarding process."));
    }
}
