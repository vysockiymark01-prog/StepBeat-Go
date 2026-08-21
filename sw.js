// Minimal offline app-shell cache for the StepBeat Go PWA.
// Everything this app does (route parsing, tempo math) already runs
// entirely client-side with no network calls - once this shell is cached,
// the whole app keeps working with zero connection.
//
// IMPORTANT: bump CACHE_NAME every time this file changes, so old installs
// (including the Play Store TWA) drop their stale cache automatically
// instead of showing an outdated version forever.
const CACHE_NAME = "stepbeat-go-v3";
const ASSETS = [
  "./",
  "./index.html",
  "./manifest.json",
  "./privacy.html",
  "./icons/icon-192.png",
  "./icons/icon-512.png",
  "./icons/icon-maskable-512.png",
  "./icons/apple-touch-icon.png",
  "./icons/favicon-32.png"
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) =>
      // Cache each asset independently instead of cache.addAll(), which
      // fails ALL-OR-NOTHING - a single missing/failed file (e.g. one bad
      // icon path) would otherwise silently wipe out offline support
      // entirely, even though the rest of the app works fine online.
      Promise.all(
        ASSETS.map((url) =>
          cache.add(url).catch((err) => {
            console.warn("[sw] failed to precache", url, err);
          })
        )
      )
    )
  );
  self.skipWaiting();
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE_NAME).map((k) => caches.delete(k)))
    )
  );
  self.clients.claim();
});

// Network-first for the HTML page itself, so a fresh deploy is picked up
// on the very next load instead of waiting for a manual cache bust.
// Cache-first for everything else (icons, manifest) since those rarely
// change and it's fine for them to load instantly from cache.
self.addEventListener("fetch", (event) => {
  if (event.request.method !== "GET") return;
  // Only handle same-origin requests - this app makes zero external calls
  // by design, so anything else (e.g. a mailto/vk link's browser-level
  // navigation) is just left alone.
  if (new URL(event.request.url).origin !== self.location.origin) return;

  const isNavigation = event.request.mode === "navigate" ||
    event.request.destination === "document";

  if (isNavigation) {
    event.respondWith(
      fetch(event.request)
        .then((response) => {
          const copy = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(event.request, copy));
          return response;
        })
        .catch(() =>
          caches.match(event.request).then((cached) => cached || caches.match("./index.html"))
        )
    );
    return;
  }

  event.respondWith(
    caches.match(event.request).then((cached) => {
      if (cached) return cached;
      return fetch(event.request)
        .then((response) => {
          const copy = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(event.request, copy));
          return response;
        })
        .catch(() => caches.match("./index.html"));
    })
  );
});
