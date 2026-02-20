# Nexus

A personal command center dashboard that aggregates your GitHub activity, weather, news, Spotify, and Google Calendar into a single unified interface — with a built-in REST API.

## Features
- JWT authentication (register, login, protected routes)
- GitHub integration — contribution streak and recent activity
- Weather integration — current conditions and daily forecast
- News integration — headlines filtered by your chosen topics
- Spotify integration — currently playing and recently listened (OAuth 2.0)
- Google Calendar integration — today's events and upcoming deadlines (OAuth 2.0)
- Background polling that automatically refreshes data every 30 minutes
- Personal API key generation to access your aggregated data programmatically

## Tech Stack
**Backend:** Java, Spring Boot, Spring Security, PostgreSQL, Spring Data JPA  
**Frontend:** React, Vite, Axios, React Router

## Integrations
| Integration | Auth Method | Data |
|---|---|---|
| GitHub | Username (public API) | Streak, commits, open PRs |
| OpenWeatherMap | API Key | Current weather, forecast |
| NewsAPI | API Key | Headlines by topic |
| Spotify | OAuth 2.0 | Now playing, recent tracks |
| Google Calendar | OAuth 2.0 | Today's events, upcoming deadlines |


- Java 21
- Maven
- PostgreSQL
- Node.js


## Project Structure
```
nexus/
├── backend/          # Spring Boot REST API
│   └── src/
│       └── main/
│           ├── java/ # Application code
│           └── resources/
│               ├── application.properties.example
└── frontend/         # React dashboard
    └── src/
        ├── pages/    # Login, Register, Dashboard, Settings
        └── components/
            └── cards/ # Weather, GitHub, News, Spotify, Calendar
```

## Roadmap
- [x] Project setup and file structure
- [ ] Database models and repositories
- [ ] JWT authentication (backend)
- [ ] Auth pages (frontend)
- [ ] Weather integration
- [ ] GitHub integration
- [ ] News integration
- [ ] Dashboard frontend (cards)
- [ ] API key generation
- [ ] Spotify OAuth integration
- [ ] Google Calendar OAuth integration
- [ ] Background polling scheduler
- [ ] Deployment
