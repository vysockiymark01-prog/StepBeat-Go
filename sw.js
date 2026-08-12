// Minimal offline app-shell cache for the StepBeat Go PWA.
// Everything this app does (route parsing, tempo math) already runs
// entirely client-side with no network calls - this just lets the shell
// itself load when there's no connection, which is what PWABuilder /
// Android's PWA install checks expect to see.
//
// IMPORTANT: bump CACHE_NAME every time this file changes, so old installs
// (including the Play Store TWA) drop their stale cache automatically
// instead of showing an outdated version forever.
const CACHE_NAME = "stepbeat-go-v2";
const ASSETS = [
  "./",
  "./index.html",
  "./manifest.json",
  "./icons/icon-192.png",
  "./icons/icon-512.png",
  "./icons/icon-maskable-512.png"
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(ASSETS))
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

  const isNavigation = event.request.mode === "navigate" ||
    (event.request.destination === "document");

  if (isNavigation) {
    event.respondWith(
      fetch(event.request)
        .then((response) => {
          const copy = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(event.request, copy));
          return response;
        })
        .catch(() => caches.match(event.request).then((cached) => cached || caches.match("./index.html")))
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
