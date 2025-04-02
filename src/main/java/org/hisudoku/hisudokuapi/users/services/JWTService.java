package org.hisudoku.hisudokuapi.users.services;

import lombok.extern.slf4j.Slf4j;

import org.hisudoku.hisudokuapi.general.exceptions.UserNotFoundException;
import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.models.HSUserPrincipal;
import org.hisudoku.hisudokuapi.users.repositories.HSUserComplexQueriesRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
@Slf4j
public class JWTService {
    private final String secretKey;
    private final long jwtExpiration;
    private final HSUserComplexQueriesRepository hsUserComplexQueriesRepository;

    public JWTService(
            @Value("${application.security.jwt.secret-key}") String secretKey,
            @Value("${application.security.jwt.access-token-expiration}") long jwtExpiration,
            HSUserComplexQueriesRepository hsUserComplexQueriesRepository
    ) {
        this.hsUserComplexQueriesRepository = hsUserComplexQueriesRepository;
        this.secretKey = secretKey; // SecretKey key = Jwts.SIG.HS256.key().build();
        this.jwtExpiration = jwtExpiration;
    }

    private Key getSignInKey() {
        // ref: https://www.wikiwand.com/en/Base64
        // BASE64- Group of binary-to-text encoding schemes using 64 symbols that represent binary data (more specifically, a sequence of 8-bit bytes) in sequences of 24 bits that can be represented by four 6-bit Base64 digits.

        // Java supports Base64 encoding and decoding features through the java.util.Base64 utility class.
        // The encoder maps the input to a set of characters in the A-Za-z0-9+/ character set.
        // "secret".getBytes("UTF-8") // convert a String to a byte[]
        // String originalInput = "test input";
        // String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        // byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        // String decodedString = new String(decodedBytes);
        // URL encoding is very similar to the basic encoder, it also uses the URL and Filename Safe Base64 alphabet. In addition, it does not add any line separation
        // String originalUrl = "https://www.google.co.nz/?gfe_rd=cr&ei=dzbFV&gws_rd=ssl#q=java";
        // String encodedUrl = Base64.getUrlEncoder().encodeToString(originalURL.getBytes());
        // byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedUrl);
        // String decodedUrl = new String(decodedBytes);
        // The MIME encoder generates a Base64-encoded output using the basic alphabet. However, the format is MIME-friendly. Each line of the output is no longer than 76 characters. Also, it ends with a carriage return followed by a linefeed (\r\n)

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // In Base64 encoding, the length of an output-encoded String must be a multiple of four. If necessary, the encoder adds one or two padding characters (=) at the end of the output as needed in order to meet this requirement.

        // ref: https://www.wikiwand.com/en/JSON_Web_Token
        // Token signature is calculated by encoding the header and payload using Base64url Encoding RFC 4648 and concatenating the two together with a period separator. That string is then run through the cryptographic algorithm specified in the header. The alg parameter in the header represents the algorithm of the signature. Valid values of alg are listed not in RFC 7515
        // String header = '{"alg":"HS256"}'
        // String claims = '{"sub":"Joe"}'
        // String encodedHeader = base64URLEncode( header.getBytes("UTF-8") )
        // String encodedClaims = base64URLEncode( claims.getBytes("UTF-8") )
        // String concatenated = encodedHeader + '.' + encodedClaims
        // SignatureAlgorithm.HS256 creates header- (alg: HS256), that means algorithm HMAC using SHA-256
        // SecretKey key = getMySecretKey()
        // byte[] signature = hmacSha256( concatenated, key )
        //String compact = concatenated + '.' + base64URLEncode( signature )
        //This is called a 'JWS' - short for signed JWT. A JWE uses cryptography to ensure that the payload remains fully encrypted and authenticated so unauthorized parties cannot see data within, nor change the data without being detected. Specifically, the JWE specification requires that Authenticated Encryption with Associated Data algorithms are used to fully encrypt and protect data.

        // SecretKey key = Keys.hmacShaKeyFor(secretkey.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(keyBytes); // Computes a Hash-based Message Authentication Code (HMAC) by using the SHA256 hash function. Here hmacShaKeyFor() method determines the Algorithm to be used based on the bit length of Secretkey.
    }

    private Claims extractAllClaims(String token) {
        // the first versions of the library recommended to use Jwts.parser() method that returned new JwtParser instance, in the next versions it was deprecated and the use of method parseBuilder was recommended, in version 0.12 the naming was changed again and the name of method parser() was returned
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractMail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        //We can use Jackson's object mapper to convert Claims (which is a Map<String, Object>) to our custom claim java object.
        //final ObjectMapper mapper = new ObjectMapper();
        //Claims jwsMap = Jwts.parser()
        //       .setSigningKey("SECRET")
        //       .parseClaimsJws("jwt")
        //       .getBody();
        //return mapper.convertValue(jwsMap, MyCustomClaim.class);

        // Adding Custom Claims
        //String token = Jwts.builder()
        //   .setSubject(subject)
        //   .setExpiration(expDate)
        //   .claim("userId", "3232")
        //   .claim("UserRole", "Admin")
        //   .signWith(SignatureAlgorithm.HS512, secret )
        //   .compact();
        //
        //Retrieving Custom Claims
        //Claims claims = Jwts.parser()
        //   .setSigningKey(tokenSecret)
        //   .parseClaimsJws(jwt).getBody();
        //
        //// Reading Reserved Claims
        //System.out.println("Subject: " + claims.getSubject());
        //System.out.println("Expiration: " + claims.getExpiration());
        //
        //// Reading Custom Claims
        //System.out.println("userId: " + claims.get("userId"));
        //System.out.println("userRole: " + claims.get("userRole"));

        return claims.get("mail", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private List<String> getClaimOrEmptyList(String claim, String token) {
        Claims claims = extractAllClaims(token);
        if (claims.get(claim) == null) return List.of();
        return claims.get(claim, List.class);
    }

    public Long extractHSUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public HSUserPrincipal extractPrincipal(String token) {
        final Claims claims = extractAllClaims(token);
        HSUser hsUser = hsUserComplexQueriesRepository.findOneById(claims.getSubject())
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.ByProperty.ID, claims.getSubject()));
        hsUser.setPassword(null);
        return new HSUserPrincipal(hsUser);
    }

    public String issueAuthToken(HSUserPrincipal userDetails) {
        return issueAuthToken(new HashMap<>(), userDetails);
    }

    public String issueAuthToken(Map<String, Object> extraClaims, HSUserPrincipal userDetails) {
        return issueAuthToken(extraClaims, userDetails, jwtExpiration);
    }

    private String issueAuthToken(Map<String, Object> extraClaims, HSUserPrincipal userDetails, long expiration) {
        // Google JSON Style Guide (recommendations for building JSON APIs at Google) recommends that:
        //      1. Property names must be camelCased, ASCII strings.
        //      2. The first character must be a letter, an underscore (_), or a dollar sign ($)

        List<String> authorities = new ArrayList<>();
        userDetails.getAuthorities().forEach(a -> authorities.add(a.getAuthority()));
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("authorities", authorities);

        return Jwts.builder()
                .claims(extraClaims)
                // .issuer("me") // The “iss” (issuer) claim identifies the principal that issued the JWT. The processing of this claim is generally application specific. The “iss” value is a case-sensitive string containing a StringOrURI value. Use of this claim is OPTIONAL.
                // .subject(String.format("%s,%s", user.getId(), user.getName()))
                // .audience().add("you").and()
                // .notBefore(notBefore) //a java.util.Date
                // .id(UUID.randomUUID().toString()) //not needed for now
                .subject(userDetails.getId()) // The “sub” (subject) claim identifies the principal that is the subject of the JWT. The claims in a JWT are normally statements about the subject. The subject value MUST either be scoped to be locally unique in the context of the issuer or be globally unique. The processing of this claim is generally application specific. The “sub” value is a case-sensitive string containing a StringOrURI value. Use of this claim is OPTIONAL
                .issuedAt(new Date(System.currentTimeMillis())) // Instant.now(), the “iat” (issued at) claim identifies the time at which the JWT was issued. This claim can be used to determine the age of the JWT. Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Instant.now().plusMillis(expiration) or Instant.now().toEpochMilli() + expiration, the “exp” (expiration time) claim identifies the expiration time on or after which the JWT MUST NOT be accepted for processing. The processing of the “exp” claim requires that the current date/time MUST be before the expiration date/time listed in the “exp” claim. Implementers MAY provide for some small leeway, usually no more than a few minutes, to account for clock skew. Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.
                .claim("userId", userDetails.getId())
//                .claim("userName", userDetails.getName())
//                .claim("userRole", userDetails.getRole().name())
//                .claim("userEnrollmentDate", userDetails.getEnrollmentDate().toString())
//                .claim("userUpdatedAt", userDetails.getUpdatedAt().toString())
                .signWith(getSignInKey())
                //.encryptWith(key, keyAlg, encryptionAlg)  //     if encrypting
                .claims(additionalClaims)
                .compact();

        //You can set the JWT payload to be any arbitrary byte array content by using the JwtBuilder content method. For example:
        //byte[] content = "Hello World".getBytes(StandardCharsets.UTF_8);
        //String jwt = Jwts.builder()
        //    .content(content, "text/plain") // <---
        //    // ... etc ...
        //    .build();
        //Notice this particular example of content uses the two-argument convenience variant:
        //    The first argument is the actual byte content to set as the JWT payload
        //    The second argument is a String identifier of an IANA Media Type.
        //The second argument will cause the JwtBuilder to automatically set the cty (Content Type) header according to the JWT specification’s recommended compact format.
    }

    public String issueActivateEmailToken(String email, HSUserPrincipal userDetails, long expiration) {
        List<String> authorities = new ArrayList<>();
        userDetails.getAuthorities().forEach(a -> authorities.add(a.getAuthority()));
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("authorities", authorities);

        return Jwts.builder()
                .subject(userDetails.getId()) // The “sub” (subject) claim identifies the principal that is the subject of the JWT. The claims in a JWT are normally statements about the subject. The subject value MUST either be scoped to be locally unique in the context of the issuer or be globally unique. The processing of this claim is generally application specific. The “sub” value is a case-sensitive string containing a StringOrURI value. Use of this claim is OPTIONAL
                .issuedAt(new Date(System.currentTimeMillis())) // Instant.now(), the “iat” (issued at) claim identifies the time at which the JWT was issued. This claim can be used to determine the age of the JWT. Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Instant.now().plusMillis(expiration) or Instant.now().toEpochMilli() + expiration, the “exp” (expiration time) claim identifies the expiration time on or after which the JWT MUST NOT be accepted for processing. The processing of the “exp” claim requires that the current date/time MUST be before the expiration date/time listed in the “exp” claim. Implementers MAY provide for some small leeway, usually no more than a few minutes, to account for clock skew. Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.
                .claim("userId", userDetails.getId())
                .claim("email", email)
                .signWith(getSignInKey())
                //.encryptWith(key, keyAlg, encryptionAlg)  //     if encrypting
                .claims(additionalClaims)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateTokenBySignature(String token) {
        // When you use a JWT, you must check its signature before storing and using it.
        // You can enforce that the JWT you are parsing conforms to expectations that you require and are important for your application. For example, let's say that you require that the JWT you are parsing has a specific sub (subject) value, otherwise you may not trust the token. You can do that by using one of the various require* methods
        try {
            //Type-safe JWTs: If you are certain your parser will only ever encounter a specific kind of JWT (for example, you only ever use signed JWTs with Claims payloads, or encrypted JWTs with byte[] content payloads, etc), you can call the associated type-safe parseSignedClaims, parseEncryptedClaims, (etc) method variant instead of the generic parse method. These parse* methods will return the type-safe JWT you are expecting, for example, a Jws<Claims> or Jwe<byte[]> instead of a generic Jwt<?,?> instance.
            Jwts.parser().verifyWith((SecretKey) getSignInKey()).build().parseSignedClaims(token).getPayload();  //OK, we can trust this JWT
//                .verifyWith(key)      //     or a constant key used to verify all signed JWTs
//                .decryptWith(key)     //     or a constant key used to decrypt all encrypted JWTs

            //If parsing a JWS and the JWS was signed with a SecretKey, the same SecretKey should be specified on the JwtParserBuilder. For example:
            //Jwts.parser()
            //  .verifyWith(secretKey) // <----
            //  .build()
            //  .parseSignedClaims(jwsString);
            //If parsing a JWS and the JWS was signed with a PrivateKey, that key’s corresponding PublicKey (not the PrivateKey) should be specified on the JwtParserBuilder. For example:
            //Jwts.parser()
            //  .verifyWith(publicKey) // <---- publicKey, not privateKey
            //  .build()
            //  .parseSignedClaims(jwsString);
            // What if JWSs and JWEs can be created with different SecretKeys or public/private keys, or a combination of both? How do you know which key to specify if you don’t inspect the JWT first? In these cases, you’ll need to configure a parsing Key Locator

            // token is trustworthy and has not been tampered with
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("token: " + token + " | " + "JWT expired", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("token: " + token + " | " + "Token is null, empty or only whitespace", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("token: " + token + " | " + "JWT is invalid", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("token: " + token + " | " + "JWT is not supported", ex);
        } catch (SignatureException ex) {
            log.error("token: " + token + " | " + "Signature validation failed");
        } catch (JwtException jwtException) {
            log.error("token: " + token + " | " + "Don't trust this JWT");
        }
        return false;
    }
}
