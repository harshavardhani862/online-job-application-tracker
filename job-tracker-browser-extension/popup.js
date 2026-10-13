const apiBaseUrl = document.getElementById("apiBaseUrl");
const jwtToken = document.getElementById("jwtToken");
const enabled = document.getElementById("enabled");
const status = document.getElementById("status");
chrome.storage.local.get(["apiBaseUrl", "jwtToken", "enabled"], (v) => {
  if (v.apiBaseUrl) apiBaseUrl.value = v.apiBaseUrl;
  if (v.jwtToken) jwtToken.value = v.jwtToken;
  enabled.checked = v.enabled === true;
});
document.getElementById("save").addEventListener("click", async () => {
  const base = apiBaseUrl.value.trim().replace(/\/$/, "");
  const token = jwtToken.value.trim();
  if (!/^https?:\/\//i.test(base)) { status.textContent = "Enter a valid API URL."; return; }
  if (enabled.checked && !token) { status.textContent = "Paste your Job Tracker JWT token first."; return; }
  await chrome.storage.local.set({ apiBaseUrl: base, jwtToken: token, enabled: enabled.checked });
  status.textContent = "Settings saved.";
});
