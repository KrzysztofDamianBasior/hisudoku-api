package org.hisudoku.hisudokuapi.users.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

//@ToString(exclude = {"password", "id"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "email_activation_token")
public class EmailActivationToken {
    @Id
    @Indexed(unique = true)
    private String id;

    @Indexed(unique = true)
    private String token;

    private String email;

    private String principalId;

    private LocalDateTime expiration; // while dateTime could have been a String variable, the best practice is to use date/time-specific JDK classes for date fields. Using String fields to represent dates requires extra effort to ensure the values are formatted correctly.

    @Field(name = "account_usage_info")
    private ZonedDateTime createdAt;
}

// ---------------------------------------------------------------------------------------------------------------------

// An instance of current date can be created from the system clock: LocalDate localDate = LocalDate.now();
// LocalDate.of(2015, 02, 20, 11, 0, 0);
// LocalDateTime.of(2015, Month.FEBRUARY, 20, 06, 30);  ->  LocalDateTime.parse("2015-02-20T06:30:00");
// LocalDate previousMonthSameDay = LocalDate.now().minus(1, ChronoUnit.MONTHS);

// String localDateString = localDateTime.format(DateTimeFormatter.ISO_DATE);
// localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

// LocalDate.ofInstant(dateToConvert.toInstant(), ZoneId.systemDefault());

// ZoneId zoneId = ZoneId.of("Europe/Paris");
// ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
// ZonedDateTime.parse("2015-05-03T10:15:30+01:00[Europe/Paris]");
// LocalDateTime localDateTime = LocalDateTime.of(2015, Month.FEBRUARY, 20, 06, 30);
// ZoneOffset offset = ZoneOffset.of("+02:00");
// OffsetDateTime offSetByTwo = OffsetDateTime.of(localDateTime, offset);

// int twelve = LocalDate.parse("2016-06-12").getDayOfMonth();
// LocalTime now = LocalTime.now();
// int six = LocalTime.parse("06:30").getHour();
// LocalTime sixThirty = LocalTime.of(6, 30);
// LocalTime sevenThirty = LocalTime.parse("06:30").plus(1, ChronoUnit.HOURS);
// boolean isbefore = LocalTime.parse("06:30").isBefore(LocalTime.parse("07:30"));

// Period class is widely used to modify values of given a date: int five = Period.between(initialDate, finalDate).getDays();
// Duration class is used to deal with Time: long thirty = Duration.between(initialTime, finalTime).getSeconds();
// ChronoUnit is an enumeration in java.time package that provides units of time, such as DAYS, HOURS, MINUTES, etc. It provides type safety and better maintainability compared to manual calculations with milliseconds. We could achieve the same result with Duration as follows, but ChronoUnit is cleaner and easier to understand: Instant cutoffDate = Instant.now().minus(Duration.ofDays(30));

// ---------------------------------------------------------------------------------------------------------------------

//For removing documents before Date, your command should be:
//db.collection.deleteMany( { orderExpDate : {"$lt" : new Date(YEAR, MONTH, DATE) } })

//For removing records before 1 October 2017, the command will be:
//db.collection.deleteMany( { orderExpDate : {"$lt" : new Date(2017, 9, 1) } })
//October is the 10th month. If the month field is zero indexed, then we use 9, otherwise use 10.

//This will remove all records older than seven days:
//db.collection.deleteMany( { orderExpDate : {"$lt" : new Date(Date.now() - 7*24*60*60 * 1000) } })

// how to remove old records in MongoDB based on X days
//db.statistic.insertMany([
//  {
//    event: 'pageview',
//    userId: 'user1',
//    createdAt: new Date('July 1, 2022 14:10:00'),
//  }
//  {
//    event: 'pageview',
//    userId: 'user10',
//    createdAt: new Date('July 10, 2022 11:22:00'),
//  },
//]);
//
//db.statistic.remove({
//  createdAt: {
//    $lt: new Date(today - X days)
//  }
//});

// ---------------------------------------------------------------------------------------------------------------------
// For relational db ->

//    @Temporal(TemporalType.DATE)
//    Date publicationDate;
//
//    @Temporal(TemporalType.TIME)
//    Date publicationTime;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    Date creationDateTime;

//    List<Article> findAllByPublicationDate(Date publicationDate);
//    List<Article> findAllByPublicationTimeBetween(Date publicationTimeStart,Date publicationTimeEnd);
//
//    @Query("select a from Article a where a.creationDateTime <= :creationDateTime")
//    List<Article> findAllWithCreationDateTimeBefore(@Param("creationDateTime") Date creationDateTime);

//        List<Article> result = repository.findAllByPublicationDate(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"));
//        List<Article> result = repository.findAllByPublicationTimeBetween(new SimpleDateFormat("HH:mm").parse("15:15"), new SimpleDateFormat("HH:mm").parse("16:30"));
//        List<Article> result = repository.findAllWithCreationDateTimeBefore(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2017-12-15 10:00"));

//    @Column(nullable = false)
//    private Instant createdAt;

//    The @Modifying annotation is required when you are executing update or delete queries using @Query in Spring Data JPA. Without @Modifying, Spring Data JPA assumes the query is a read-only select query and may throw an exception.
//    @Transactional
//    @Modifying
//    @Query("DELETE FROM Order o WHERE o.createdAt < :cutoffDate")
//    int deleteOldOrders(Instant cutoffDate);
//    Database modification operations (like delete, update) should be wrapped inside a transaction to ensure atomicity and consistency. @Transactional ensures that the delete operation is executed within a transaction, meaning that if anything goes wrong, the changes will be rolled back to prevent data corruption. Key reasons for using @Transactional:
//    - Ensures consistency in case of multiple operations.
//    - Handles rollback in case of exceptions.
//    - Helps optimize query execution.

// ---------------------------------------------------------------------------------------------------------------------
