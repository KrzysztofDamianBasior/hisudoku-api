package org.hisudoku.hisudokuapi.general.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hisudoku.hisudokuapi.users.repositories.EmailActivationTokenComplexQueriesRepository;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfiguration {
    private final EmailActivationTokenComplexQueriesRepository emailActivationTokenComplexQueriesRepository;

    // Cron is a basic utility available on Unix-based systems. It enables users to schedule tasks to run periodically at a specified date/time.
    // A Spring Scheduled tasks is like this:
    //
    //1 2 3 4 5 6 Index
    //- - - - - -
    //* * * * * * command to be executed
    //- - - - - -
    //| | | | | |
    //| | | | | ------- Day of week (MON - SUN) (0 - 7) (Sunday=0 or 7)
    //| | | | --------- Month (1 - 12)
    //| | | ----------- Day of month (1 - 31)
    //| |-------------- Hour (0 - 23)
    //| --------------- Minute (0 - 59)
    //----------------- Seconds (0 - 59)
    @Scheduled(cron = "0 0 2 * * ?")  // Runs daily at 2 AM
    public void cleanupOldTokens() {
        long deletedRecords = emailActivationTokenComplexQueriesRepository.removeExpired();

        // Instant cutoffDate = Instant.now().minus(30, ChronoUnit.DAYS);
        log.info("Deleted {} stale records", deletedRecords);
    }
}



