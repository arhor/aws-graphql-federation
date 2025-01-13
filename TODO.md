- migrate from Spring AWS integration to JMS via Spring-Messaging (easier to change infra in future)
- add scheduled tasks service (single responsibility - store/fire events without actual processing)
    - NOTE:
      ```
      * separate service with its own DB to store events
      * events coming through the application event bus
      * there is a scheduled job trying to 
        fetch/release/delete scheduled tasks
      * responsible services listen for scheduled task to
        process it
      ```
- adjust build caches on CI to work properly
