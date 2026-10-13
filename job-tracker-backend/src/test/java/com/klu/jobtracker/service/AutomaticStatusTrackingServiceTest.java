package com.klu.jobtracker.service;

import com.klu.jobtracker.entity.ApplicationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AutomaticStatusTrackingServiceTest {

    private final AutomaticStatusTrackingService tracker =
            new AutomaticStatusTrackingService(null, null, null, null);

    @Test
    void detectsSelectionWithoutConfusingItWithShortlisting() {
        assertEquals(ApplicationStatus.SELECTED,
                tracker.detectStatus("Congratulations, you have been selected for this position."));
        assertEquals(ApplicationStatus.SHORTLISTED,
                tracker.detectStatus("You have been selected for the next round."));
    }

    @Test
    void detectsInterviewAndRejectionEmails() {
        assertEquals(ApplicationStatus.INTERVIEW,
                tracker.detectStatus("Your interview has been scheduled."));
        assertEquals(ApplicationStatus.REJECTED,
                tracker.detectStatus("We regret to inform you that we will not be proceeding."));
    }

    @Test
    void ignoresConditionalRejectionLanguageInApplicationEmails() {
        assertNull(tracker.detectStatus(
                "Thank you for applying. If you are not selected, we will contact you."));
        assertNull(tracker.detectStatus(
                "If you are selected for this position, our team will contact you."));
    }
}
