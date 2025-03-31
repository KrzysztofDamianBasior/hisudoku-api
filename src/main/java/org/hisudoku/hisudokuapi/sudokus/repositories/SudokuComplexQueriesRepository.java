package org.hisudoku.hisudokuapi.sudokus.repositories;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.general.exceptions.OperationNotAllowedException;
import org.hisudoku.hisudokuapi.general.exceptions.SudokuNotFoundException;
import org.hisudoku.hisudokuapi.sudokus.entities.Sudoku;
import org.hisudoku.hisudokuapi.sudokus.models.SudokuFeedModel;
import org.hisudoku.hisudokuapi.sudokus.models.SudokuModel;
import org.hisudoku.hisudokuapi.sudokus.services.SudokuUtils;
import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.models.UserFeedModel;
import org.hisudoku.hisudokuapi.users.models.UserModel;
import org.hisudoku.hisudokuapi.users.repositories.HSUserComplexQueriesRepository;
import org.hisudoku.hisudokuapi.users.services.HSUserUtils;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SudokuComplexQueriesRepository {
    private final MongoTemplate mongoTemplate;
    private final HSUserComplexQueriesRepository hsUserComplexQueriesRepository;

    public Sudoku addOne(String content, String authorId) {
        Sudoku sudoku = new Sudoku();
        sudoku.setAuthorId(authorId);
        sudoku.setContent(content);
        sudoku.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        sudoku.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        sudoku.setFavouriteCount(0);
//        sudoku.setFavouritedBy(Collections.emptySet());
        sudoku.setFavouritedBy(new HashSet<>());

        return mongoTemplate.insert(sudoku);
    }

    public Optional<Sudoku> removeOneById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        // In Spring data for MongoDB, you can use remove() and findAndRemove() to delete documents from MongoDB. remove() – delete single or multiple documents. findAndRemove() – delete single document, and returns the deleted document. Don’t use findAndRemove() to perform a batch delete (remove multiple documents), only the first document that matches the query will be removed.

        return Optional.ofNullable(mongoTemplate.findAndRemove(query, Sudoku.class));
    }

    public SudokuFeedModel findMany(int limit, String cursor) {
        Query query = new Query();

        query.addCriteria(Criteria.where("_id").lt(cursor)); // take younger than cursor
        query.limit(limit + 1);
        // By utilizing the sort() method, we can organize our query results in either ascending (1) or descending (-1) order based on one or more fields.
        // db.topics.find().sort({ bump_date: -1 })
        query.with(Sort.by(Sort.Direction.DESC, "_id")); // from the oldest to the youngest

        return findMany(query, limit);
    }

    public SudokuFeedModel findMany(int limit) {
        // final Pageable pageableRequest = PageRequest.of(0, limit);
        // query.with(pageableRequest);

        Query query = new Query();
        query.limit(limit + 1);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        return findMany(query, limit);
    }

    public SudokuFeedModel findManyByAuthor(String authorId, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("authorId").is(authorId)); //  _id: { $in: ids } }
        query.limit(limit + 1);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        return findMany(query, limit);
    }

    public SudokuFeedModel findManyByAuthor(String authorId, int limit, String cursor) {
        Query query = new Query();
        query.addCriteria(
                new Criteria().andOperator(Criteria.where("authorId").is(authorId), Criteria.where("_id").lt(cursor))
        );
        query.limit(limit + 1);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        return findMany(query, limit);
    }

    private SudokuFeedModel findMany(Query query, int limit) {
        List<Sudoku> sudokus = mongoTemplate.find(query, Sudoku.class);
        ArrayList<Sudoku> sudokusCollection = new ArrayList<>(sudokus);

        boolean hasNextPage = false;
        String newCursor = null;

        if (sudokusCollection.size() > limit) {
            newCursor = sudokusCollection.get(sudokusCollection.size() - 1).getId();
            hasNextPage = true;
            sudokusCollection.remove(sudokusCollection.size() - 1);
        }

        return new SudokuFeedModel(SudokuUtils.mapToSudokuModelFavouritedByNullAuthorNullDTOs(sudokus), hasNextPage, newCursor);
    }

    public Optional<Sudoku> findOneById(String sudokuId) { // List<String> fieldsToExclude
        //You can project specific fields using the fields() method of Query.
        //    query.addCriteria(Criteria.where("age").gt(age));
        //    query.fields().include("name").include("city").exclude("_id");
        //Here, only name and city fields will be returned, and the _id field will be excluded.

        return Optional.ofNullable(mongoTemplate.findById(sudokuId, Sudoku.class));
    }

    public Optional<HSUser> findSudokuAuthor(String sudokuId) {
        Sudoku sudoku = findOneById(sudokuId).orElseThrow(() -> new SudokuNotFoundException(sudokuId));

        return hsUserComplexQueriesRepository.findOneById(sudoku.getAuthorId());
    }

    public Map<SudokuModel, UserModel> findSudokusAuthors(List<SudokuModel> sudokusModels) {
        List<String> sudokusIds = sudokusModels.stream().map(SudokuModel::getId).toList();

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(sudokusIds)); //  _id: { $in: ids } }
        List<Sudoku> sudokusEntities = mongoTemplate.find(query, Sudoku.class);

        List<String> authorsIds = sudokusEntities.stream().map(Sudoku::getAuthorId).toList();

        List<HSUser> authors = hsUserComplexQueriesRepository.findManyByIds(authorsIds);

        return sudokusEntities.stream()
                .collect(Collectors.toMap(
                        sudokuEntity -> extractSudokuModel(sudokusModels, sudokuEntity.getId()),
                        sudokuEntity -> HSUserUtils.mapToUserModelDTO(extractAuthor(authors, sudokuEntity.getAuthorId()
                                )
                        )));
    }

    private HSUser extractAuthor(List<HSUser> authors, String authorId) {
        // findAny() method allows us to find any element from a Stream. We use it when we’re looking for an element without paying an attention to the encounter order. The findFirst() method finds the first element in a Stream. Streams may or may not have a defined encounter order. It depends on the source and the intermediate operations.
        return authors.stream()
                .filter(user -> Objects.equals(user.getId(), authorId))
                .findAny()
                .orElseThrow(() -> new OperationNotAllowedException("extract author"));
    }

    private SudokuModel extractSudokuModel(List<SudokuModel> models, String sudokuId) {
        return models.stream()
                .filter(model -> Objects.equals(model.getId(), sudokuId))
                .findAny()
                .orElseThrow(() -> new OperationNotAllowedException("extract sudoku model"));
    }

    public UserFeedModel findUsersWhoLikeSudoku(String sudokuId, int limit, String cursor) {
        Sudoku sudoku = findOneById(sudokuId).orElseThrow(() -> new SudokuNotFoundException(sudokuId));

        Query query = new Query();

        // Criteria.where("_id").in(ids) //  _id: { $in: ids } }
        // Criteria.where("_id").nin(ids)

        // new Criteria().orOperator(
        //      new Criteria().andOperator(Criteria.where("category").is(category1), Criteria.where("price").gt(price1)),
        //      new Criteria().andOperator(Criteria.where("name").is(name1), Criteria.where("available").is(available1))
        // )

        query.addCriteria(
                new Criteria().andOperator(Criteria.where("_id").in(sudoku.getFavouritedBy()), Criteria.where("_id").lt(cursor))
        );
        query.limit(limit + 1);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        return hsUserComplexQueriesRepository.findMany(query, limit);
    }

    public UserFeedModel findUsersWhoLikeSudoku(String sudokuId, int limit) {
        Sudoku sudoku = findOneById(sudokuId).orElseThrow(() -> new SudokuNotFoundException(sudokuId));

        Query query = new Query();

        query.addCriteria(Criteria.where("_id").in(sudoku.getFavouritedBy()));
        query.limit(limit + 1);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        return hsUserComplexQueriesRepository.findMany(query, limit);
    }

    public Optional<Sudoku> updateOneContent(String sudokuId, String newContent) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(sudokuId));

        Update update = new Update().set("content", newContent);
        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);
        Sudoku result = mongoTemplate.findAndModify(query, update, options, Sudoku.class);

        return Optional.ofNullable(result);
    }

    public Optional<Sudoku> toggleLike(String sudokuId, String userId) {
        Sudoku sudoku = findOneById(sudokuId).orElseThrow(() -> new SudokuNotFoundException(sudokuId));
        boolean hasUser = sudoku.getFavouritedBy().stream().anyMatch((id) -> id.equals(userId)); //indexOf

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(sudokuId));
        if (hasUser) {
            //        {
            //          $pull: { favoritedBy: new mongoose.Types.ObjectId(userId) },
            //          $inc: { favoriteCount: -1 },
            //        },
            Update update = new Update().pull("favourited_by", userId).inc("favourite_count", -1);
            FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);
            Sudoku result = mongoTemplate.findAndModify(query, update, options, Sudoku.class);

            return Optional.ofNullable(result);
        } else {
            //        {
            //          $push: { favoritedBy: new mongoose.Types.ObjectId(userId) },
            //          $inc: { favoriteCount: 1 },
            //        },
            Update update = new Update().addToSet("favourited_by", userId).inc("favourite_count", 1);
            FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);
            Sudoku result = mongoTemplate.findAndModify(query, update, options, Sudoku.class);

            return Optional.ofNullable(result);
        }
        // update.addToSet("targetField", "newValue");
        // update.addToSet("targetField").each("value1", "value2");         //    Add items to a Set (non-duplicatable)
        // update.push("targetField").each("value1", "value2");             //    Append items to an Array (duplicatable)
        // update.pop("targetField", position);                             //    Remove item at position
        // update.pull("targetField", "value");                             //    Remove item by value
        // update.pullAll("targetField", new Object[]{"value1", "value2"}); //    Remove multiple items
    }
}
