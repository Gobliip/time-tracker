0.1.0
-----
- Moved trackings API to `/trackings`
- Added `GET '/trackings/{trackingId}/moments'` endpoint to fetch a trackings moment
- Added moments API in `/moments`
-  ~~Added `POST '/moments'` to create moments, MEMO moments are the only type currently supported~~
- Decided that `TrackingsService` should be the only way to interact with moments and trackings externally
- Removed Usage of Spring Data JPA repositories in favor of using EntityManager directly.
- Added `HEARTBEAT` moment type to denote keep alive tracking
- Added support for attachments and memos for CREATE, PAUSE, RESUME, STOP tracking actions
- Added support for database stored `Attachement`
- Added endpoint `/attachments/{attachmentId}/raw` to download raw attchment data

0.0.1
-----
- Start/Stop/Pause/Resume tracking time using the trackings api
