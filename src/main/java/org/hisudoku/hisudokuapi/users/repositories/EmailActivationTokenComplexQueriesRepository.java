package org.hisudoku.hisudokuapi.users.repositories;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.users.entities.EmailActivationToken;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.mongodb.client.result.DeleteResult;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EmailActivationTokenComplexQueriesRepository {
    private final MongoTemplate mongoTemplate; // uses MongoDB Java Driver to execute criteria queries, such as creating and updating documents, and querying, updating, and deleting documents whose date fields fall within a given range

    // create token
    public Optional<EmailActivationToken> addOne(String email, String principalId) {
        String token = UUID.randomUUID().toString();
        // can also UUID.randomUUID().toString().replace("-", ""); if app do not uses dashes

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiration = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(30);

        EmailActivationToken emailActivationToken = new EmailActivationToken();
        emailActivationToken.setToken(token);
        emailActivationToken.setEmail(email);
        emailActivationToken.setExpiration(expiration);
        emailActivationToken.setCreatedAt(now);
        emailActivationToken.setPrincipalId(principalId);

        return Optional.ofNullable(mongoTemplate.insert(emailActivationToken));
    }

    public Optional<EmailActivationToken> findOneByToken(String token) {
        // Criteria.where("name").regex("^A")
        // Criteria.where("status").regex("^" + pattern, "i"))   // In this example, the "i" flag makes the regex case-insensitive.

        Query query = new Query();

        // this query will return all fields including mongodb id field, if you do not write include id, it will written all fields except id, if you include any other actual field from collection, then that field ONLY will be returned
        //  query.fields().include("_id");

        // this query will return all fields excluding mongodb id field, and the field specified in excluded
        // query.fields().exclude("some_field_name");

        // List<String> fieldsToExclude
        // query.fields().exclude(fieldsToExclude);

        //To find users where the status field is null:
        // Criteria.where("status").is(null)

        //To find users where the status field is not null:
        // Criteria.where("status").ne(null)

        query.addCriteria(Criteria.where("token").is(token));
        return Optional.ofNullable(mongoTemplate.findOne(query, EmailActivationToken.class));
    }

    public Optional<EmailActivationToken> removeOneById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        // In Spring data for MongoDB, you can use remove() and findAndRemove() to delete documents from MongoDB. remove() – delete single or multiple documents. findAndRemove() – delete single document, and returns the deleted document
        // Don’t use findAndRemove() to perform a batch delete (remove multiple documents), only the first document that matches the query will be removed.

        return Optional.ofNullable(mongoTemplate.findAndRemove(query, EmailActivationToken.class));
    }

    public Optional<EmailActivationToken> removeOneByToken(String token) {
        Query query = new Query();
        query.addCriteria(Criteria.where("token").is(token));

        return Optional.ofNullable(mongoTemplate.findAndRemove(query, EmailActivationToken.class));
    }

    // remove all whose expiration date is below certain threshold
    public long removeExpired() {
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

         //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
         //Query query = Query.query(Criteria.where("CREATE_DATETIME").lte(dateFormat.format(prevDate)));

        Query query = new Query();
        query.addCriteria(Criteria.where("expiration").lte(LocalDateTime.now(ZoneOffset.UTC)));

        DeleteResult result = mongoTemplate.remove(query, EmailActivationToken.class);

        return result.getDeletedCount();
    }
}

// ---------------------------------------------------------------------------------------------------------------------

// ref: https://rohit-talukdar.medium.com/different-methods-to-generate-unique-token-ids-9a695131d6c7
// The intent of UUIDs is to enable distributed systems to uniquely identify information without significant central coordination. In this context the word unique should be taken to mean "practically unique" rather than "guaranteed unique". Since the identifiers have a finite size, it is possible for two differing items to share the same identifier. In any system that we design, we may need to identify each object uniquely. Very often, this token generator module itself can become a performance and concurrency bottleneck. Token Generator options:
// 1. use a monotonically increasing counter and assign whenever a new request for a unique resource id is made
// 2. use hashing function like MD5|SHA1|SHA2 or equivalent to generate a unique hash function. SHA-1 - This algorithm takes a message of any length as input and produces a 160-bit "fingerprint" as output. SHA-1 or Secure Hash Algorithm 1 is a cryptographic algorithm that takes an input and produces a 160-bit (20-byte) hash value. This hash value is known as a message digest. This message digest is usually then rendered as a hexadecimal number which is 40 digits long.
// 3. use UUID / GUID
// 4. use PRIMARY key in DB
// 5. use SnowflakeID aka TwitterID
// UUIDs are 36-character strings containing numbers, letters, and dashes. UUIDs are designed to be globally unique. The chances of a duplicate UUID being generated are so low that it is safe to assume each ID will be unique. Separate computers can generate UUIDs at the same time with no communication and still be confident that the UUIDs are unique. Independent systems that use UUIDs can be safely merged at any time without worrying about collisions. This is an extremely useful property, as many computer systems today are distributed.

// ---------------------------------------------------------------------------------------------------------------------

// ref: https://medium.com/@mounikakurapati17/how-to-generate-unique-number-in-java-ab3acfa82973
// BigInteger bigInteger = new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16);

//UUID.randomUUID(). toString() produces a unique string of length 36 that is made up of characters (alphabets and “-”) and digits, which are in hexadecimal format. So with the help of BigInteger, we can convert hexadecimal to decimal format. Here radix, also known as base, is 16, since uuid is hexadecimal.

//To produce a positive unique long value we can also use LocalDateTime
//String now = LocalDateTime.now().toString();
//for (String s : Arrays.asList("-", ".", ":", "T")) {
//      now = now.replace(s,"");
//}
//Usually LocalDateTime.now() returns “2022–12–05T16:31:36.037198” which contains special characters like “-,” “:,” etc. So we need to replace them, and then we get the unique number.

//We can also use HashSet and Random Library
//Set<Integer> uniqueNumbersSet= new HashSet<>();
//Random r=new Random();
//while(uniqueNumbersSet.size()<n) {
//    uniqueNumbersSet.add(r.nextInt(n));
//}
//List<Integer> uniqueNumbersList = new ArrayList<>(uniqueNumbersSet);
//Integer integer = uniqueNumbersList.get(0);
//uniqueNumbersSet.remove(integer);

// ---------------------------------------------------------------------------------------------------------------------

// ref: https://www.baeldung.com/java-uuid
//A standard UUID code contains 32 hex digits along with 4 “-” symbols, which makes its length equal to 36 characters. The UUID class has a single constructor that requires two long parameters describing the most significant at the least significant 64 bits:
//UUID uuid = new UUID(mostSignificant64Bits, leastSignificant64Bits);

//The downside of using the constructor directly is that we must construct the bit pattern of the UUID, which might be a good solution when we want to recreate a UUID object. But most of the time, we use UUID to identify something and can assign a random value. Therefore, the UUID class provides three static methods we can use.

//First, we can create a version 3 UUIF using the .nameUUIDFromBytes() method, which requires a byte array as a parameter:
//UUID uuid = UUID.nameUUIDFromBytes(bytes);

//Second, we can parse a UUID string value from a previously generated code:
//UUID uuid = UUID.fromString(uuidHexDigitString);

//Next, we’ll try to understand the structure of a UUID. In particular, let’s consider the following UUID with a corresponding mask underneath:
//123e4567-e89b-42d3-a456-556642440000
//xxxxxxxx-xxxx-Bxxx-Axxx-xxxxxxxxxxxx
//
//In the example above, A denotes the variant that defines the layout of the UUID. All the other bits in the UUID depend on the layout of the variant field. Therefore, the variant represents the three most significant bits of A:
//  MSB1    MSB2    MSB3
//   0       X       X     reserved (0)
//   1       0       X     current variant (2)
//   1       1       0     reserved for Microsoft (6)
//   1       1       1     reserved for future (7)
//
//Above, the A value in the example UUID is “a”, which is 10xx in binary. So, the layout variant is 2. Similarly, B represents the version. In the example UUID, the value of B is 4, which means it is using version 4.

//For any UUID object inside Java, we can check the variant and the version using the .variant() and the .version() methods:
//UUID uuid = UUID.randomUUID();
//int variant = uuid.variant();
//int version = uuid.version();

//Moreover, there are five different versions for variant 2 UUIDs:
//    Time-Based (UUIDv1)
//    DCE Security (UUIDv2)
//    Name Based (UUIDv3 and UUIDv5)
//    Random (UUIDv4)
//However, Java provides an implementation only for v3 and v4. Alternatively, we can use the constructor to generate the other types.

//UUID version 1 uses the current timestamp and the MAC address of the device generating the UUID. In particular, the timestamp is measured in units of 100 nanoseconds from October 15, 1582. Still, if privacy is a concern, we can use a random 48-bit number instead of the MAC address. With this in mind, let’s generate the least significant and most significant 64 bits as long values:
//private static long get64LeastSignificantBitsForVersion1() {
//    Random random = new Random();
//    long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
//    long variant3BitFlag = 0x8000000000000000L;
//    return random63BitLong | variant3BitFlag;
//}
//Above, we combine two long values representing the last 63 bits of a random long value and the 3-bit variant flag. Next, we create the 64 most significant bits using a timestamp:
//private static long get64MostSignificantBitsForVersion1() {
//    final long currentTimeMillis = System.currentTimeMillis();
//    final long time_low = (currentTimeMillis & 0x0000_0000_FFFF_FFFFL) << 32;
//    final long time_mid = ((currentTimeMillis >> 32) & 0xFFFF) << 16;
//    final long version = 1 << 12;
//    final long time_hi = ((currentTimeMillis >> 48) & 0x0FFF);
//    return time_low | time_mid | version | time_hi;
//}
//We can then pass these two values to the constructor of the UUID:
//public static UUID generateType1UUID() {
//    long most64SigBits = get64MostSignificantBitsForVersion1();
//    long least64SigBits = get64LeastSignificantBitsForVersion1();
//    return new UUID(most64SigBits, least64SigBits);
//}

//Next, version 2 uses a timestamp and the MAC address as well. However, RFC 4122 does not specify the exact generation details, so that we won’t look at an implementation in this article.

//Versions 3 and 5 UUIDs use hashed names drawn from a unique namespace. Moreover, the concept of names is not limited to textual form. For example, Domain Name System (DNS), Object Identifiers (OIDs), URLs, etc., are considered valid namespaces.
//UUID = hash(NAMESPACE_IDENTIFIER + NAME)

//In detail, the difference between UUIDv3 and UUIDv5 is the Hashing Algorithm — v3 uses MD5 (128 bits), while v5 uses SHA-1 (160 bits) truncated to 128 bits. For both versions, we replace the bits to correct the version and the variant accordingly. Alternatively, we can generate type 3 UUID from a previous namespace and given name and use the method .nameUUIDFromBytes():
//byte[] nameSpaceBytes = bytesFromUUID(namespace);
//byte[] nameBytes = name.getBytes("UTF-8");
//byte[] result = joinBytes(nameSpaceBytes, nameBytes);
//UUID uuid = UUID.nameUUIDFromBytes(result);
//Here, we convert the hex string for the namespace to a byte array and then combine it with the name to create the UUID.

// ---------------------------------------------------------------------------------------------------------------------

// ref: https://www.baeldung.com/java-random-string

// a random String bounded to 7 characters:
//    byte[] array = new byte[7]; // length is bounded by 7
//    new Random().nextBytes(array);
//    String generatedString = new String(array, Charset.forName("UTF-8"));
// keep in mind that the new string will not be anything remotely alphanumeric.

//Next let’s look at creating a more constrained random string; we’re going to generate a random String using lowercase alphabetic letters and a set length:
//    int leftLimit = 97; // letter 'a'
//    int rightLimit = 122; // letter 'z'
//    int targetStringLength = 10;
//    Random random = new Random();
//    StringBuilder buffer = new StringBuilder(targetStringLength);
//    for (int i = 0; i < targetStringLength; i++) {
//        int randomLimitedInt = leftLimit + (int)
//          (random.nextFloat() * (rightLimit - leftLimit + 1));
//        buffer.append((char) randomLimitedInt);
//    }
//    String generatedString = buffer.toString();

//Now let’s use Random.ints, added in JDK 8, to generate an alphabetic String:
//    int leftLimit = 97; // letter 'a'
//    int rightLimit = 122; // letter 'z'
//    int targetStringLength = 10;
//    Random random = new Random();
//    String generatedString = random.ints(leftLimit, rightLimit + 1)
//      .limit(targetStringLength)
//      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//      .toString();

//Then we can widen our character set in order to get an alphanumeric String:
//    int leftLimit = 48; // numeral '0'
//    int rightLimit = 122; // letter 'z'
//    int targetStringLength = 10;
//    Random random = new Random();
//    String generatedString = random.ints(leftLimit, rightLimit + 1)
//      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
//      .limit(targetStringLength)
//      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//      .toString();
//We used the filter method above to leave out Unicode characters between 65 and 90 in order to avoid out of range characters.
