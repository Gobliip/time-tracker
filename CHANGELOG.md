0.1.0
-----
- Moved trackings API to `/trackings`
- Added `GET '/trackings/{trackingId}/moments'` endpoint to fetch a trackings moment
- Added moments API in `/moments`
- Added `POST '/moments'` to create moments, MEMO moments are the only type currently supported
- Removed Usage of Spring Data JPA repositories in favor of using EntityManager directly.

0.0.1
-----
- Start/Stop/Pause/Resume tracking time using the trackings api
