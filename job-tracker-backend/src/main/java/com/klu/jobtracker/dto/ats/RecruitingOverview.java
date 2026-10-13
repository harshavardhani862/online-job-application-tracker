package com.klu.jobtracker.dto.ats;

import java.util.List;

public record RecruitingOverview(String organizationName, long openJobs, long totalCandidates,
                                 long newCandidates, long screeningCandidates, long interviews,
                                 long offers, long hires, List<JobResponse> jobs,
                                 List<CandidateResponse> recentCandidates) {}
