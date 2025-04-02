package org.hisudoku.hisudokuapi.users.repositories;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.users.entities.AccountUsageInfo;
import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.models.UserFeedModel;
import org.hisudoku.hisudokuapi.users.services.HSUserUtils;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HSUserComplexQueriesRepository {
    private final MongoTemplate mongoTemplate;

    public Optional<HSUser> findOneById(String userId) {
        return Optional.ofNullable(mongoTemplate.findById(userId, HSUser.class));
    }
    public List<HSUser> findManyByIds(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids)); //  _id: { $in: ids } }
        return mongoTemplate.find(query, HSUser.class);
    }

    public Optional<HSUser> findOneByName(String name) {
        // Criteria.where("name").regex("^A")
        // Criteria.where("status").regex("^" + pattern, "i"))
        // In this example, the "i" flag makes the regex case-insensitive.

        Query query = new Query();
        // this query will return all fields including mongodb id field,
        // if you do not write include id, it will written all fields except id
        // if you include any other actual field from collection, then that field ONLY will be returned
        //  query.fields().include("_id");

        // this query will return all fields excluding mongodb id field, and the field specified in excluded
        // query.fields().exclude("some_field_name");

        // query.fields().exclude("_id").include("account_usage_info.enrollmentDate");

        // List<String> fieldsToExclude
        // query.fields().exclude(fieldsToExclude);

        query.addCriteria(Criteria.where("name").is(name));
        return Optional.ofNullable(mongoTemplate.findOne(query, HSUser.class));
    }

    public Optional<HSUser> findOneByEmail(String email) {
        Query query = new Query();
        // List<String> fieldsToExclude
        // query.fields().exclude(fieldsToExclude);
        query.addCriteria(Criteria.where("email").is(email));
        //To find users where the status field is null:
        // Criteria.where("status").is(null)

        //To find users where the status field is not null:
        // Criteria.where("status").ne(null)

        return Optional.ofNullable(mongoTemplate.findOne(query, HSUser.class));
    }

    public boolean doesNameExist(String name) {
        Query query = new Query(Criteria.where("name").is(name));

        //mongoTemplate.exists determine if result of given Query contains at least one element, returns true if the query yields a result.
        return mongoTemplate.exists(query, HSUser.class);
    }

    public boolean doesEmailExist(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.exists(query, HSUser.class);
    }

//    public boolean doesEmailOrInactiveEmailExist(String email) {
//        // Criteria criteria = Criteria.where("field").in(listOfOptions);
//        Criteria criteria = new Criteria();
//        criteria.orOperator(
//                Criteria.where("inactive_email").is(email),
//                Criteria.where("email").is(email)
//        );
//        Query query = new Query(criteria);
//        return mongoTemplate.exists(query, HSUser.class);
//    }

    public Optional<HSUser> addOne(HSUser user) {
        // The difference between mongoTemplate.save and mongoTemplate.insert
        //If you attempt to use "insert" with an ID that was previously used in the same collection you will get a duplicate key error. If you use "save" with an ID that is already in the same collection, it will get updated/overwritten.
        // If a document does not exist with the specified _id value, the save() method performs an insert with the specified fields in the document. If a document exists with the specified _id value, the save() method performs an update, replacing all field in the existing record with the fields from the document. Save doesn't allow any query-params, if _id exists and there is a matching doc with the same _id, it replaces it. When no _id specified/no matching document, it inserts the document as a new one.
        // Update modifies an existing document matched with your query params. If there is no such matching document, that's when upsert comes in picture. Upsert false - nothing happens when no such document exist. Upsert true - new doc gets created with contents equal to query params and update params.

        return Optional.ofNullable(mongoTemplate.insert(user));
    }

    public Optional<HSUser> addOne(String name, String hashedPassword, String role, String email) {
        HSUser userToSave = new HSUser();
        userToSave.setName(name);
        userToSave.setPassword(hashedPassword);
        userToSave.setRole(role);
        userToSave.setEmail(email);
        userToSave.setAccountUsageInfo(new AccountUsageInfo(LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC)));
        return addOne(userToSave);
    }

    public Optional<HSUser> addOne(String name, String hashedPassword, String role) {
        HSUser userToSave = new HSUser();
        userToSave.setName(name);
        userToSave.setPassword(hashedPassword);
        userToSave.setRole(role);
        userToSave.setEmail(null);
        userToSave.setAccountUsageInfo(new AccountUsageInfo(LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC)));
        return addOne(userToSave);
    }

    public Optional<HSUser> removeOneById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        // In Spring data for MongoDB, you can use remove() and findAndRemove() to delete documents from MongoDB. remove() – delete single or multiple documents. findAndRemove() – delete single document, and returns the deleted document
        // Don’t use findAndRemove() to perform a batch delete (remove multiple documents), only the first document that matches the query will be removed.

        return Optional.ofNullable(mongoTemplate.findAndRemove(query, HSUser.class));
    }

    public Optional<HSUser> removeOneByName(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));

        return Optional.ofNullable(mongoTemplate.findAndRemove(query, HSUser.class));
    }

    // updateResetPasswordLink(userId, newResetPasswordLink) findById(userId).resetPasswordLink = newResetPasswordLink
    // updateOnePasswordAndRemoveResetPasswordLink(resetPasswordLink, newPassword) findOne({ resetPasswordLink }).password = newPassword, .resetPasswordLink = ''

    //  findOneOpenDetailsByEmail
    //  findOnePublicDetailsByUsername
    //  findOnePublicDetailsByResetPasswordLink
    //  PublicUserDetails & ProtectedUserDetails

    public Optional<HSUser> updateOneUsername(String userId, String newName) {
        //  Update update = new Update().set("name", name).setOnInsert("id", authorId).setOnInsert("active", true); // $setOnInsert inserts a new record (and then executes $set)
        Update update = new Update().set("name", newName);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));
        // findById(userId)
        // save()
        // UpdateResult updateResult = mongoTemplate.upsert(query, updateDefinition, HSUser.class);
        // UpdateResult updateResult = mongoTemplate.updateFirst(query, updateDefinition, HSUser.class);
        // UpdateResult updateResult = mongoTemplate.updateMulti(query, updateDefinition, HSUser.class);

        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);

        HSUser result = mongoTemplate.findAndModify(query, update, options, HSUser.class, "users");
        // mongoTemplate.findAndReplace

        return Optional.ofNullable(result);
    }

//    public Optional<HSUser> findOneByIdUpdateEmailSetInactiveEmailToNull(String userId, String newEmail) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id").is(userId));
//        Update update = new Update().set("email", newEmail).set("inactive_email", null);
//        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);
//        HSUser result = mongoTemplate.findAndModify(query, update, options, HSUser.class, "users");
//        return Optional.ofNullable(result);
//    }

    public Optional<HSUser> findOneByIdUpdateEmail(String userId, String newEmail) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));

        Update update = new Update().set("email", newEmail);
        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);

        HSUser result = mongoTemplate.findAndModify(query, update, options, HSUser.class);

        return Optional.ofNullable(result);
    }

    public Optional<HSUser> findOneByIdUpdatePassword(String userId, String newHashedPassword) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));

        Update update = new Update().set("password", newHashedPassword);
        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);

        HSUser result = mongoTemplate.findAndModify(query, update, options, HSUser.class, "users");

        return Optional.ofNullable(result);
    }

    public Optional<HSUser> findOneByNameUpdatePassword(String name, String newHashedPassword) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));

        Update update = new Update().set("password", newHashedPassword);

        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);

        HSUser result = mongoTemplate.findAndModify(query, update, options, HSUser.class, "users");

        return Optional.ofNullable(result);
    }

    public Optional<HSUser> findOneByIdUpdateRole(String userId, String newRole) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));

        Update update = new Update().set("role", newRole);

        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(false).returnNew(true);

        HSUser result = mongoTemplate.findAndModify(query, update, options, HSUser.class, "users");

        return Optional.ofNullable(result);
    }

    public List<HSUser> findManyByRole(String role, Integer offset, Integer perPage) {
        Query query = new Query();
        query.addCriteria(Criteria.where("role").is(role));
        query.skip(offset);
        query.limit(perPage);
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

        // final Pageable pageableRequest = PageRequest.of(page, perPage);
        // query.with(pageableRequest);

        return mongoTemplate.find(query, HSUser.class);
    }

    public List<HSUser> findManyByEnrollmentDateBetween(LocalDateTime earlierDate, LocalDateTime laterDate) {
        // If I want to check if a date falls between a date range (between earlierDate and laterDate).
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(
                Criteria.where("account_usage_info.enrollmentDate").lte(earlierDate),
                Criteria.where("account_usage_info.enrollmentDate").gte(laterDate))
        );
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

        return mongoTemplate.find(query, HSUser.class);
    }

    public UserFeedModel findMany(int limit, String cursor) {
        Query query = new Query();

        query.addCriteria(Criteria.where("_id").lt(cursor));
        query.limit(limit + 1);
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

        return findMany(query, limit);
    }

    public UserFeedModel findMany(int limit) {
        Query query = new Query();
        query.limit(limit + 1);
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

        return findMany(query, limit);
    }

    public UserFeedModel findMany(Query query, int limit) {
        List<HSUser> users = mongoTemplate.find(query, HSUser.class);
        ArrayList<HSUser> usersCollection = new ArrayList<>(users);

        boolean hasNextPage = false;
        String newCursor = null;

        if (usersCollection.size() > limit) {
            newCursor = usersCollection.get(usersCollection.size() - 1).getId();
            hasNextPage = true;
            usersCollection.remove(usersCollection.size() - 1);
        }

        return new UserFeedModel(HSUserUtils.mapToUserModelDTOs(usersCollection), hasNextPage, newCursor);
    }

    public Object getAllUserSettings(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));
        // Query query = new Query(Criteria.where("article_count").lte(10));
        HSUser user = mongoTemplate.findOne(query, HSUser.class);
        return user != null ? user.getUserSettings() : "User not found.";
    }

    public String getUserSetting(String userId, String key) {
        Query query = new Query();
        query.fields().include("userSettings");
        query.addCriteria(Criteria.where("_id").is(userId).andOperator(Criteria.where("userSettings." + key).exists(true)));
        HSUser user = mongoTemplate.findOne(query, HSUser.class);
        return user != null ? user.getUserSettings().get(key) : "Not found.";
    }

    public String addUserSetting(String userId, String key, String value) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));
        HSUser user = mongoTemplate.findOne(query, HSUser.class);
        if (user != null) {
            user.getUserSettings().put(key, value);
            mongoTemplate.save(user);
            return "Key added.";
        } else {
            return "User not found.";
        }
    }
}

// ---------------------------------------------------------------------------------------------------------------------
// ref: https://godwin-pinto.medium.com/spring-boot-mongodb-quick-guide-9f43c5f7b952

// Step 1: Create a list of aggregations you want to perform in the pipeline in an order and choose the base collection where aggregation will start from.
// List<AggregationOperation> lstAggregationOperation = new ArrayList<>();

//Step 2: Perform operation that should take place and attach to the above list. Examples below;
//    MatchOperation: Filter results; more like WHERE clause in SQL
//    ProjectionOperation: To shorten the list of fields you want to pass in the pipeline
//    LookupOperation: To join two collections with a common unique field
//    SortOperation: To sort the result
//    GroupOperation: To generate grouped field value, like SUM, COUNT, etc
//    AddFieldsOperation: To add a calculated field to your output
//    ConditionalOperators: Comparing fields and adding custom value
//    UnwindOperation: Used to deconstruct an array field in a document and create separate output documents for each item in the array
//
//    Note: Only one operation is performed at a time in the pipeline and the order of execution is decided by Ordered List lstAggregationOperation variable we defined above.
//
//public List<Document> getAggregatedData() {
//  //base collection where conditions
//  MatchOperation matchFilterData = Aggregation.match(new Criteria("field1").is("value"));
//
//  //join another collection to base collection
//  LookupOperation lookupIssues = LookupOperation.newLookup()
//    .from("collection_to_be_joined")
//    .localField("fieldname_from_base_collection")
//    .foreignField("fieldname_from_foreign_collection")
//    .as("custom_name_field");
//
//  //unwinding, refer unwinding description
//  UnwindOperation unwindIssuesNode = Aggregation.unwind("custom_name_field");
//
//  //create a custom condition
//  Cond condOperation = ConditionalOperators
//    .when(Criteria.where("$fieldx").is("$fieldy"))
//    .then("valueXYZ").otherwise("valuePQR");
//
//  //add a new field to the output with custom logic
//  AddFieldsOperation addOwnerField = Aggregation.addFields()
//    .addFieldWithValue("owner", condOperation)
//    .build();
//
//  //narrow results with selected fields only
//  ProjectionOperation projectColumns = Aggregation.project()
//    .and("owner").as("owner") //keep column name same
//    .and("time").as("new_time") //renaming this column
//    .andExclude("_id");
//
//  //perform group operations
//  GroupOperation groupfields = Aggregation
//    .group("field1", "field2")
//    .sum("field3")
//    .as("new_custom_field_name");
//
//  //last perform sort operation
//  SortOperation sortBy = Aggregation.sort(Direction.ASC, "field_name1")
//    .and(Direction.ASC, "field_name2");
//
//  /**
//  * create List<AggregationOperation> lstAggregationOperation = new ArrayList<>();
//  * at start and add at every step or another way is as below. Note order is important
//  */
//  Aggregation aggregation = Aggregation.newAggregation(matchFilterData,
//    lookupIssues, unwindIssuesNode, addOwnerField, projectColumns,
//    groupfields,sortBy);
//
//  //Finally this is where the actual query is executed
//  List<Document> result = mongoTemplate
//    .aggregate(aggregation, "base_collection", Document.class).getMappedResults();
//  }
//}

// ---------------------------------------------------------------------------------------------------------------------

//ref: https://medium.com/geekculture/types-of-update-operations-in-mongodb-using-spring-boot-11d5d4ce88cf
//save() method does not accept any parameters or criteria to find the document that has to be updated. It by default tries to find the document using ‘_id’ if provided with the document object. If the document is found, the save() method updates the document else creates a new one. save() method returns the updated/newly created document object. The disadvantage with the save() method is that we always have to provide the whole Document object as an input parameter even if we want to update only 1 or 2 fields out of 20 fields. Meaning the save() method does not allow delta updates

//saveAll() method is exactly similar to the save() method except for two things. Instead of one document object, it takes a collection of document objects like List<City> as an input parameter and returns a collection of updated/inserted document objects like List<City>

//upsert() method is similar to the save() method in the way that it updates the document if found, else creates a new one based on the data provided. Unlike the save() method, upsert() accepts criteria on other fields apart from ‘_id’ to find the document to be updated. upsert() returns an acknowledgment object containing details of update operation like ‘upsertedId’, ‘matchedCount’, ‘modifiedCount’. upsert() does not accept the entire Document object but only the UpdateDefinition containing the details of which all fields have to be updated. The disadvantage here is that if no document is found for a given criterion, then upsert() will create an object with only the fields provided in the UpdateDefinition. This may result in adding incomplete/corrupted data into the database.

//findAndModify() brings the best of both upsert() and save() method method. Like the upsert() method, the findAndModify() method also accepts criteria on other fields apart from ‘_id’ to find the document to be updated. findAndModify() does not accept the entire Document object but only the UpdateDefinition containing the details of which all fields have to be updated Like save() method, findAndModify() method returns the entire document object. Additionally, we get the flexibility to mention if we want the old, pre-update document object or the new, post-update document object as the return value. We can choose if we want to insert the new document or not if no document is found matching to search query by providing the ‘upsert(true)’ option to findAndModify() method

//findAndReplace() allows us to find a document using a query over any field. Once the document is found, it replaces that document using the new document that we have provided in the request. We can choose if we want to insert the new document or not if no document is found matching to search query by providing the ‘upsert()’ option to findAndReplace() method. We can also choose if we want the updated document as the return value or not by providing the ‘returnNew()’ option to findAndReplace() method. Delta update is not possible since we need to provide the entire document which is used to replace the existing document.

//updateFirst() accepts criteria on other fields apart from ‘_id’ to find the document to be updated. We can also do delta updates here, like the upsert() method. Like upsert() method, the updateFirst() returns an acknowledgement object containing details of update operation like ‘upsertedId’, ‘matchedCount’, ‘modifiedCount’. But, if no matching document is found, it will not create a new document in the database based on the fields provided in the UpdateDefinition. The only difference between the updateFirst() and other delta update operations is that it will update only the first document out of all the documents that match the update criteria.

//updateMulti() is exactly similar to the updateFirst() except for one thing. Unlike the updateFirst(), it will update all the documents that match the update criteria.
//UpdateResult updateResult = mongoTemplate.updateMulti(query, updateDefinition, City.class);
