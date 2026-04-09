# IBANify

Monorepo: **Spring Boot**-API (`ibanify-core/`) und **React/Vite**-SPA (`ibanify-frontend/`).

## Lokal starten

1. Backend: in `ibanify-core/` mit `./gradlew bootRun` (Standard oft `http://localhost:8080`).
2. Frontend: in `ibanify-frontend/` mit `npm install` und `npm run dev` (Vite-Proxy auf `/api` → Backend, siehe `vite.config.ts`).

Umgebungsvariablen: jeweils `ibanify-frontend/.env.example` und Backend `application.properties` / Env.

## Git (ein Repository)

Commits immer vom **Repository-Root** (`ibanify/`). Husky/Lint-Staged für das Frontend:

```bash
git config core.hooksPath ibanify-frontend/.husky
```

(Einmalig nach `git clone`.)

## Struktur

| Verzeichnis        | Inhalt                    |
| ------------------ | ------------------------- |
| `ibanify-core/`    | REST-API, OpenIBAN, usw. |
| `ibanify-frontend/` | SPA, Vite, React         |
