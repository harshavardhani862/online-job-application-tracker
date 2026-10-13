chrome.runtime.onMessage.addListener((message, sender) => {
  if (message?.type !== "JOB_TRACKER_APPLICATION_DETECTED" || !message.application) return;
  (async () => {
    const settings = await chrome.storage.local.get(["apiBaseUrl", "jwtToken", "enabled"]);
    if (settings.enabled !== true) {
      console.info("Job Tracker detector is disabled. Open the extension popup to enable it.");
      return;
    }
    if (!settings.jwtToken) {
      console.warn("Job Tracker detector has no JWT token. Open the extension popup and configure it.");
      return;
    }
    const apiBase = (settings.apiBaseUrl || "http://localhost:8080/api").replace(/\/$/, "");
    try {
      const response = await fetch(`${apiBase}/applications/auto-detected`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${settings.jwtToken}`
        },
        body: JSON.stringify(message.application)
      });
      if (!response.ok) {
        const detail = await response.text();
        console.error("Job Tracker could not save detected application:", response.status, detail);
        return;
      }
      console.info("Job Tracker saved/recognized a detected application:", message.application.companyName, message.application.jobTitle);
    } catch (error) {
      console.error("Could not connect to the Job Tracker API. Is Spring Boot running?", error);
    }
  })();
});
