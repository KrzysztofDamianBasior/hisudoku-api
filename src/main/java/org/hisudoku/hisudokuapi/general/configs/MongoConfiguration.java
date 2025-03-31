package org.hisudoku.hisudokuapi.general.configs;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfiguration {
    //  @Autowired
    //  private MongoDbFactory dbFactory;
    //  @Autowired
    //  private MongoMappingContext mongoMappingContext;

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(new DateToZonedDateTime(), new ZonedDateTimeToDate()));
    }

    @ReadingConverter
    public class DateToZonedDateTime implements Converter<Date, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(Date date) {
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .truncatedTo(ChronoUnit.MILLIS);
        }
    }

    @WritingConverter
    public class ZonedDateTimeToDate implements Converter<ZonedDateTime, Date> {
        @Override
        public Date convert(ZonedDateTime zonedDateTime) {
            return Date.from(zonedDateTime.toInstant());
        }
    }

    // alternatively
    // ref: https://dev.to/tpbabparn/use-zoneddatetime-in-spring-webflux-mongodb-reactive-1408
    //    @Bean
    //    public MongoCustomConversions mongoCustomConversions() {
    //        List<Converter<?,?>> converters = new ArrayList<>();
    //        converters.add(ZonedDateTimeToDate.INSTANCE);
    //        converters.add(DateToZonedDateTime.INSTANCE);
    //        return new MongoCustomConversions(converters);
    //    }
    //
    //    @ReadingConverter
    //    enum DateToZonedDateTime implements Converter<Date, ZonedDateTime> {
    //        INSTANCE;
    //
    //        @Override
    //        public ZonedDateTime convert(Date date) {
    //            return date.toInstant()
    //                    .atZone(ZoneId.systemDefault())
    //                    .truncatedTo(ChronoUnit.MILLIS);
    //        }
    //    }
    //    @WritingConverter
    //    enum ZonedDateTimeToDate implements Converter<ZonedDateTime, Date> {
    //        INSTANCE;
    //
    //        @Override
    //        public Date convert(ZonedDateTime zonedDateTime) {
    //            return Date.from(zonedDateTime.toInstant());
    //        }
    //    }
}

// ---------------------------------------------------------------------------------------------------------------------

// ref:https://www.baeldung.com/spring-data-mongodb-zoneddatetime
//org.bson.codecs.configuration.CodecConfigurationException: Can't find a codec for class java.time.ZonedDateTime

//We can handle ZonedDateTime objects (across all models) by defining a converter for reading from a MongoDB and one for writing into it. For reading, we’re converting from a Date object into a ZonedDateTime object. In the next example, we use the ZoneOffset.UTC since Date object does not store zone information:

//public class ZonedDateTimeReadConverter implements Converter<Date, ZonedDateTime> {
//    @Override
//    public ZonedDateTime convert(Date date) {
//        return date.toInstant().atZone(ZoneOffset.UTC);
//    }
//}

//Then, we’re converting from a ZonedDateTime object into a Date object. We can add the zone information to another field if needed:

//public class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Date> {
//    @Override
//    public Date convert(ZonedDateTime zonedDateTime) {
//        return Date.from(zonedDateTime.toInstant());
//    }
//}

//Since Date objects do not store a zone offset, we use UTC in our examples. With the ZonedDateTimeReadConverter and the ZonedDateTimeWriteConverter added to the MongoCustomConversions, our tests will now pass. A simple printing of the stored object will look like this:
//Action{id='testId', description='click', time=2018-11-08T08:03:11.257Z}

// ---------------------------------------------------------------------------------------------------------------------

// ref: https://www.baeldung.com/spring-data-mongodb-index-annotations-converter
//
// @Indexed- This annotation marks the field as indexed in MongoDB:
//    @Indexed
//    private String name;
//
//Now that the name field is indexed – let’s have a look at the indexes in MongoDB shell:
//db.user.getIndexes();
//We may be surprised there’s no sign of the name field anywhere! This is because, as of Spring Data MongoDB 3.0, automatic index creation is turned off by default. We can, however, change that behavior by explicitly overriding autoIndexCreation() method in our MongoConfig:
//public class MongoConfig extends AbstractMongoClientConfiguration {
//    @Override
//    protected boolean autoIndexCreation() {
//        return true;
//    }
//}
//Let’s again check out the indexes in MongoDB shell:
//[
//    {
//        "v" : 1,
//        "key" : {
//             "_id" : 1
//         },
//        "name" : "_id_",
//        "ns" : "test.user"
//    },
//    {
//         "v" : 1,
//         "key" : {
//             "name" : 1
//          },
//          "name" : "name",
//          "ns" : "test.user"
//     }
//]
//As we can see, this time, we have two indexes – one of them is _id – which was created by default due to the @Id annotation and the second one is our name field.
//Setting this property in Spring boot spring.data.mongodb.auto-index-creation property to true will enable index creation on application startup.

// Common Annotations
// @Transient As we’d expect, this simple annotation excludes the field from being persisted in the database:
//    @Transient
//    private Integer yearOfBirth;
//
// @Field indicates the key to be used for the field in the JSON document:
// @Field("email")
// private EmailAddress emailAddress;
// Now emailAddress will be saved in the database using the key email:

//Let’s now take a look at another very useful feature in Spring Data MongoDB – converters, and specifically at the MongoConverter. This is used to handle the mapping of all Java types to DBObjects when storing and querying these objects. We have two options – we can either work with MappingMongoConverter – or SimpleMongoConverter in earlier versions (this was deprecated in Spring Data MongoDB M3 and its functionality has been moved into MappingMongoConverter). Or we can write our own custom converter. To do that, we would need to implement the Converter interface and register the implementation in MongoConfig. Let’s look at a quick example. As we’ve seen in some of the JSON output here, all objects saved in a database have the field _class which is saved automatically. If however we’d like to skip that particular field during persistence, we can do that using a MappingMongoConverter.
//@Component
//public class UserWriterConverter implements Converter<User, DBObject> {
//    @Override
//    public DBObject convert(User user) {
//        DBObject dbObject = new BasicDBObject();
//        dbObject.put("name", user.getName());
//        dbObject.put("age", user.getAge());
//        if (user.getEmailAddress() != null) {
//            DBObject emailDbObject = new BasicDBObject();
//            emailDbObject.put("value", user.getEmailAddress().getValue());
//            dbObject.put("email", emailDbObject);
//        }
//        dbObject.removeField("_class");
//        return dbObject;
//    }
//}
//
//Notice how we can easily hit the goal of not persisting _class by specifically removing the field directly here. Now we need to register the custom converter:
//private List<Converter<?,?>> converters = new ArrayList<Converter<?,?>>();
//@Override
//public MongoCustomConversions customConversions() {
//    converters.add(new UserWriterConverter());
//    return new MongoCustomConversions(converters);
//}
//
//Now, when we save a new user:
//User user = new User();
//user.setName("Chris");
//mongoOps.insert(user);
//
//The resulting document in the database no longer contains the class information:
//{
//    "_id" : ObjectId("55cf09790bad4394db84b853"),
//    "name" : "Chris",
//    "age" : null
//}

// ---------------------------------------------------------------------------------------------------------------------

// ref: https://dev.to/tpbabparn/use-zoneddatetime-in-spring-webflux-mongodb-reactive-1408
// Use ZonedDateTime in Spring WebFlux (MongoDB Reactive)
// And if you want to implement ZonedDateTime inside our Spring WebFlux application, you can add ReadingConverter & WritingConverter for our MongoDB. you can see the code that implementation in the below.
//@Configuration
//public class MongoConfig {
//    @Bean
//    public MongoCustomConversions mongoCustomConversions() {
//        List<Converter<?,?>> converters = new ArrayList<>();
//        converters.add(ZonedDateTimeToDate.INSTANCE);
//        converters.add(DateToZonedDateTime.INSTANCE);
//        return new MongoCustomConversions(converters);
//    }
//
//    @ReadingConverter
//    enum DateToZonedDateTime implements Converter<Date, ZonedDateTime> {
//        INSTANCE;
//
//        @Override
//        public ZonedDateTime convert(Date date) {
//            return date.toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .truncatedTo(ChronoUnit.MILLIS);
//        }
//    }
//    @WritingConverter
//    enum ZonedDateTimeToDate implements Converter<ZonedDateTime, Date> {
//        INSTANCE;
//
//        @Override
//        public Date convert(ZonedDateTime zonedDateTime) {
//            return Date.from(zonedDateTime.toInstant());
//        }
//    }
//}
//
//curl --location --request POST 'http://localhost:8080/promotions' \
//--header 'Content-Type: application/json' \
//--data-raw '{
//    "name": "promotion-2",
//    "startDate": "2021-06-01T10:00:00.000+07:00",
//    "endDate": "2021-06-20T18:00:00.000+07:00"
//}'

// ---------------------------------------------------------------------------------------------------------------------

// ref: https://medium.com/@singh.chhaya52/custom-converters-for-incompatible-mongodb-datatypes-in-spring-9ddf23cdc2c
//Have you ever tried to read or write data from MongoDB to your application? It is quite easy to read and write compatible data types, but what about incompatible ones? I recently came across a problem in my “Reactive Spring Data MongoDB” project while reading a Decimal128 type field from MongoDB into my java code. I wanted it as a BigDecimal type but got the below error:-
//org.springframework.core.convert.ConverterNotFoundException: No converter found capable of converting from type [org.bson.types.Decimal128] to type [java.math.BigDecimal]
//
//Here in this post, we will see how I handled this scenario in the project and how to convert Decimal128 to BigDecimal or vice versa. I came across this error and started debugging the code and realized that I had to write custom converters. Fortunately, the spring framework made my task easier for enabling these converters. Let’s see what steps I followed:
//
//First create a read and write `Converter`.
//@WritingConverter
//public static class BigDecimalDecimal128Converter implements Converter<BigDecimal, Decimal128> {  @Override
//  public Decimal128 convert(@NonNull BigDecimal source) {
//    return new Decimal128(source);
//  }
//}
//
//Read Converter
//@ReadingConverter
//public static class Decimal128BigDecimalConverter implements Converter<Decimal128, BigDecimal> {  @Override
//  public BigDecimal convert(@NonNull Decimal128 source) {
//    return source.bigDecimalValue();
//  }
//}
//
//There is one class “MappingMongoConverter” in package “ org.springframework.data.mongodb.core.convert” which will be used for enabling custom conversions logic. I declared a Spring-managed bean for MappingMongoConverter and passed it as an argument for ReactiveMongoTemplate.While setting up the reactive mongo template, I need to tell MongoDB to use the custom conversions.
//@Configuration
//public class MongoConfiguration {
//  @Autowired
//  private MongoDbFactory dbFactory;
//
//  @Autowired
//  private ReactiveMongoDatabaseFactory reactiveDbFactory;
//
//  @Autowired
//  private MongoMappingContext mongoMappingContext;
//
//  @Bean
//  @Primary
//  public ReactiveMongoTemplate reactiveMongoTemplate() {
//    return new ReactiveMongoTemplate(reactiveDbFactory, mongoMappingConverter());
//  }
//  @Bean
//  @Primary
//  public MappingMongoConverter mongoMappingConverter() {
//    MongoMapReadingConverter mappingConverter = new MongoMapReadingConverter(new DefaultDbRefResolver(dbFactory), mongoMappingContext);
//   // Make sure to register your custom converters here
//    mappingConverter.setCustomConversions(customConversions());
//    return mappingConverter;
//  }
//  MongoCustomConversions customConversions() {
//    return new MongoCustomConversions(asList(
//        // writing converter, reader converter
//        new BigDecimalDecimal128Converter(), new Decimal128BigDecimalConverter()
//    ));
//  }
//  @WritingConverter
//  public static class BigDecimalDecimal128Converter implements Converter<BigDecimal, Decimal128> {
//    @Override
//    public Decimal128 convert(@NonNull BigDecimal source) {
//      return new Decimal128(source);
//    }
//  }
//  @ReadingConverter
//  public static class Decimal128BigDecimalConverter implements Converter<Decimal128, BigDecimal> {
//    @Override
//    public BigDecimal convert(@NonNull Decimal128 source) {
//      return source.bigDecimalValue();
//    }
//  }
//}

// ---------------------------------------------------------------------------------------------------------------------

// ref: https://stackoverflow.com/questions/34212545/setup-custom-converters-in-spring-data-mongo
// create a registration bean that registers all of your converters:
//@Configuration
//public class Converters {
//  @Bean
//  public MongoCustomConversions mongoCustomConversions() {
//    return new MongoCustomConversions(Arrays.asList(new MyClassToBytesConverter(), new BytesToMyClassConverter()));
//  }
//}
//
//Then create your converter classes:
//@WritingConverter
//public class MyClassToBytesConverter implements Converter<MyClass, Binary> {
//  @Override
//  public Binary convert(MyClasssource) {
//  // your code
//  }
//}
//@ReadingConverter
//public class BytesToMyClassConverter implements Converter<Binary, MyClass> {
//  @Override
//  public MyClass convert(Binary source) {
//  /// your code
//  }
//}
