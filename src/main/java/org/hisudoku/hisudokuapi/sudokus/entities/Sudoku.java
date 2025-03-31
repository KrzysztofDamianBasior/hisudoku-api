package org.hisudoku.hisudokuapi.sudokus.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sudokus")
public class Sudoku {
    @MongoId(FieldType.OBJECT_ID)
    private String id;

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @Field(name = "updated_at")
    private LocalDateTime updatedAt;

    @Field(name = "author_id")
    private String authorId; // ObjectId, ref: 'User'

    private String content;

    // stats
    @Field(name = "favourite_count")
    private Integer favouriteCount;

    @Field(name = "favourited_by")
    private Set<String> favouritedBy; // ObjectId, ref: 'User'
}

// -------------------------------------------------------------------------------------------------------------------
//    //aggregation example
//    @Aggregation("{ $group : { _id : null, averageCgpa : { $avg : $cgpa} } }")
//    Long avgCgpa();

//MongoDB enables you to perform aggregation operations through the mechanism called aggregation pipelines. These are built as a sequential series of declarative data processing operations known as stages. Each stage inspects and transforms the documents as they pass through the pipeline, feeding the transformed results into the subsequent stages for further processing. Documents from a chosen collection enter the pipeline and go through each stage, where the output coming from one stage forms the input for the next one and the final result comes at the end of the pipeline.
// Stages can perform operations on data such as:
//    filtering: This resembles queries, where the list of documents is narrowed down through a set of criteria
//    sorting: You can reorder documents based on a chosen field
//    transforming: The ability to change the structure of documents means you can remove or rename certain fields, or perhaps rename or group fields within an embedded document for legibility
//    grouping: You can also process multiple documents together to form a summarized result

// $match can be used to narrow down the list of documents at any given step of a pipeline
//The $group aggregation stage is responsible for grouping and summarizing documents. It takes in multiple documents and arranges them into several separate batches based on grouping expression values and outputs a single document for each distinct batch. The output documents hold information about the group and can contain additional computed fields like sums or averages across the list of documents from the group.
// You can use the $project stage to construct new document structures in an aggregation pipeline, thereby altering the way resulting documents appear in the result set.

//db.cities.aggregate([
//    {
//        $match: {
//            "continent": { $in: ["North America", "Asia"] }
//        }
//    },
//    {
//        $sort: { "population": -1 }
//    },
//    {
//        $group: {
//            "_id": {
//                "continent": "$continent",
//                "country": "$country"
//            },
//            "first_city": { $first: "$name" },
//            "highest_population": { $max: "$population" }
//        }
//    },
//    {
//        $match: {
//            "highest_population": { $gt: 20.0 }
//        }
//    },
//    {
//        $sort: { "highest_population": -1 }
//    },
//    {
//        $project: {
//            "_id": 0,
//            "location": {
//                "country": "$_id.country",
//                "continent": "$_id.continent",
//            },
//            "most_populated_city": {
//                "name": "$first_city",
//                "population": "$highest_population"
//            }
//        }
//    }
//])
