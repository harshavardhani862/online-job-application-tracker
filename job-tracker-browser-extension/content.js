(() => {
  if (window.__jobTrackerDetectorInstalled) return;
  window.__jobTrackerDetectorInstalled = true;
  let sentForThisPage = false;
  const successPhrases = [
    "application submitted", "application has been submitted", "application was submitted",
    "successfully applied", "thank you for applying", "thanks for applying",
    "application received", "we have received your application", "your application has been received",
    "application successfully sent", "application complete", "you have applied",
    "interview has been scheduled", "interview is scheduled", "interview invitation",
    "your interview is confirmed", "invite you to interview", "interview scheduled for"
  ];

  function clean(s) {
    return (s || "").replace(/\s+/g, " ").replace(/[|•–—-]+$/g, "").trim();
  }
  function detectSuccess() {
    if (sentForThisPage || !document.body) return;
    const bodyText = (document.body.innerText || "").toLowerCase();
    if (!successPhrases.some(p => bodyText.includes(p))) return;

    // A confirmation phrase alone can be ambiguous. Require a job/career context too.
    if (!/(job|career|position|application|candidate|role)/i.test(bodyText)) return;

    const titleCandidates = [
      document.querySelector('[data-testid*="job-title"]')?.textContent,
      document.querySelector('h1')?.textContent,
      document.querySelector('meta[property="og:title"]')?.getAttribute('content'),
      document.title
    ].map(clean).filter(Boolean);
    const genericTitle = t => /^(application (submitted|received|complete|completed)|thank you for applying|successfully applied|careers?|jobs? portal)$/i.test(t.trim());
    let jobTitle = titleCandidates.find(t => t.length >= 3 && t.length <= 120 && !genericTitle(t)) || "Job application (please edit title)";
    const titleLabel = (document.body.innerText || "").match(/(?:job title|position|role)\s*[:\-]\s*([^\n\r]{3,100})/i);
    if (jobTitle === "Job application (please edit title)" && titleLabel) jobTitle = clean(titleLabel[1]);

    const siteName = clean(document.querySelector('meta[property="og:site_name"]')?.getAttribute('content') || "");
    const host = location.hostname.replace(/^www\./, "");
    const parts = location.pathname.split('/').filter(Boolean);
    let companyName = siteName;
    if (!companyName && /greenhouse\.io$/.test(host) && parts.length) companyName = parts[0];
    if (!companyName && /lever\.co$/.test(host) && parts.length) companyName = parts[0];
    if (!companyName && /myworkdayjobs\.com$/.test(host) && parts.length) companyName = parts[0];
    if (!companyName) {
      const root = host.split('.')[0];
      const generic = new Set(['linkedin','indeed','greenhouse','lever','workday','smartrecruiters','icims','taleo','ashbyhq','jobvite','successfactors','recruitee','bamboohr']);
      companyName = generic.has(root.toLowerCase()) ? "Unknown company (please edit)" : root.replace(/[-_]/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
    }

    // Strip common confirmation words from the title where possible.
    jobTitle = jobTitle.replace(/\s*[|–—-]\s*(careers?|jobs?|application received|application submitted).*$/i, '').trim();
    const interviewDetected = /interview (has been |is )?scheduled|invite you to interview|interview invitation|your interview is confirmed/i.test(bodyText);
    let interviewDateTime;
    const isoMatch = bodyText.match(/\b(20\d{2})-(\d{2})-(\d{2})[T ](\d{2}):(\d{2})/);
    if (isoMatch) {
      interviewDateTime = `${isoMatch[1]}-${isoMatch[2]}-${isoMatch[3]}T${isoMatch[4]}:${isoMatch[5]}:00`;
    } else {
      const dateMatch = (document.body.innerText || "").match(/\b(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\s+\d{1,2},?\s+20\d{2}\s+(?:at\s+)?\d{1,2}:\d{2}\s*(?:AM|PM)\b/i);
      if (dateMatch) {
        const parsed = new Date(dateMatch[0].replace(/\s+at\s+/i, " "));
        if (!Number.isNaN(parsed.getTime())) {
          const pad = n => String(n).padStart(2, "0");
          interviewDateTime = `${parsed.getFullYear()}-${pad(parsed.getMonth()+1)}-${pad(parsed.getDate())}T${pad(parsed.getHours())}:${pad(parsed.getMinutes())}:00`;
        }
      }
    }

    sentForThisPage = true;
    chrome.runtime.sendMessage({
      type: "JOB_TRACKER_APPLICATION_DETECTED",
      application: {
        companyName,
        jobTitle,
        jobUrl: location.href,
        applicationDate: new Date().toISOString().slice(0, 10),
        status: interviewDetected ? "INTERVIEW" : "APPLIED",
        ...(interviewDetected && interviewDateTime ? { interviewDateTime } : {}),
        notes: "Detected on a job website confirmation page. Please verify company/title."
      }
    });
  }

  // Confirmation messages may render after the initial page load.
  detectSuccess();
  const observer = new MutationObserver(() => detectSuccess());
  if (document.documentElement) observer.observe(document.documentElement, { childList: true, subtree: true, characterData: true });
  window.addEventListener('pageshow', detectSuccess);
})();
