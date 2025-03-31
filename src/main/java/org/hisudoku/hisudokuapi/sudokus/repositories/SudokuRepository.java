package org.hisudoku.hisudokuapi.sudokus.repositories;

import org.hisudoku.hisudokuapi.sudokus.entities.Sudoku;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SudokuRepository extends MongoRepository<Sudoku, String> {
    List<Sudoku> findAllByFavouriteCountGreaterThanEqual(Integer favouriteCount);

    List<Sudoku> findAllByIdIn(List<String> ids);

    List<Sudoku> findAllByAuthorIdIn(List<String> ids);

    Page<Sudoku> findAllByAuthorId(String authorId, Pageable page);

    Page<Sudoku> findAll(Pageable page);

    @Query("{ 'favourite_count': { $gte: ?0, $lte: ?1 } }")
    List<Sudoku> findByFavouriteCountInRange(int min, int max);

    // List<Sudoku> findByCreatedAtBetweenOrderByCreatedAt(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'updated_at': { $gte: ?0, $lte: ?1 } }")
    List<Sudoku> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    // To use dates in your queries, you need to wrap the strings in ISODate. Dates need to be in Y-M-D format
    // db.sudokus.find({
    //   updatedAt: {
    //     $gt: ISODate("2020-09-15T04:07:05.000Z"),
    //     $lt: ISODate("2023-02-09T03:12:15.012Z")
    //   }
    // })

    //    According to documentation first argument in find is filter and second is projection
    //    MongoDB uses Projection to specify or restrict fields to return from a query.
    @Query(value = "{ 'author_id' : ?0 }", fields = "{ '_id' : 0 }")
    List<Sudoku> findAllByAuthorIdExcludeIdFields(String authorId);

    @Query(value = "{ 'author_id' : ?0 }", fields = "{ 'favourited_by' : 0, 'created_at' : 0, 'updated_at' : 0, 'favourite_count': 1, 'content': 1, 'author_id': 1, '_id': 1 }")
    List<Sudoku> findAllByAuthorIdExcludeFavouritedByCreatedAtUpdatedAt(String authorId);
}

// The $month operator extracts the month from a given date field. Here’s an example where the $expr operator is provided with the $eq operator that matches all documents that have a FeeSubmission field with a month value of 1(January).
//db.student.find({ $expr: { $eq: [{ $month: "$FeeSubmission" }, 1] } })

// The $year is an aggregation operator that extracts the year from the specified date field.
// Consider the following example, we have set the filter expression query.  The FeeSubmission field is set with a year value of 2022. The $year operator extracts the year from the FeeSubmission field, and the $eq operator compares it to the value of 2022.
// db.student.find({ $expr: { $eq: [{ $year: "$FeeSubmission" }, 2022] } })

//Furthermore, we can also search the dates in MongoDB by a given range of time. We have time operators like $hour, $minute, and $second here.
//In the following query, we use the $hour operator to check if the FeeSubmission hour is less than 4. We also use the $minute and $second operators to check if the FeeSubmission field has a minute of 12 and a second value of 15.
//db.student.find({ $expr: { $and: [
//    { $lt: [{ $hour: "$FeeSubmission" }, 4] },
//    { $eq: [{ $minute: "$FeeSubmission" }, 12] },
//    { $eq: [{ $second: "$FeeSubmission" }, 15] }
//]}})

// -----------------------------------------------------------------------------------

//    @Query("{ 'name': ?0, 'price': { $gt: ?1 }, 'category': ?2, 'available': ?3 }")
//    List<Product> findProductsByNamePriceCategoryAndAvailability(String name, double minPrice, String category, boolean available);

//    @Query("{ $or: [{ 'category': ?0, 'available': ?1 }, { 'price': { $gt: ?2 } } ] }")
//    List<Product> findProductsByCategoryAndAvailabilityOrPrice(String category, boolean available, double minPrice);
