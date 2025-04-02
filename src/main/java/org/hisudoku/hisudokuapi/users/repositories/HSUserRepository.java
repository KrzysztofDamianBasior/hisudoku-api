package org.hisudoku.hisudokuapi.users.repositories;

import org.hisudoku.hisudokuapi.users.entities.HSUser;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HSUserRepository extends MongoRepository<HSUser, String> {
    List<HSUser> findByRoleOrderByNameDesc(String role);

    Optional<HSUser> findByEmailIgnoreCase(String email);

    @Query("{'account_usage_info.enrollmentDate': ?0}")
    List<HSUser> findByEnrollmentDate(final LocalDateTime enrollmentDate);

    @Query("{ 'account_usage_info': { 'enrollmentDate': ?0 } }")
    List<HSUser> anotherFindByEnrollmentDate(LocalDateTime enrollmentDate);

    // @Query("{A: {$in: [10, 20]}}")
    // @Query("{ $or : [ {'type':?0}, {'key':?1}, {'username':?2}]}")
    // @Query("{'$or': [ {'A':10}, {'B':20} ] }")
}
