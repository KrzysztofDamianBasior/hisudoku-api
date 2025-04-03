package org.hisudoku.hisudokuapi.general.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

import org.hisudoku.hisudokuapi.users.services.MailService;

import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Component;

// @Value is the immutable variant of @Data; all fields are made private and final by default, and setters are not generated. The class itself is also made final by default, because immutability is not something that can be forced onto a subclass. Like @Data, useful toString(), equals() and hashCode() methods are also generated, each field gets a getter method, and a constructor that covers every argument (except final fields that are initialized in the field declaration) is also generated.
// @Value is similar to the @Data annotation, but it creates immutable objects. It is a shortcut annotation which combines @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE), @Getter, @AllArgsConstructor, @ToString and @EqualsAndHashCode. However, it doesn't have @Setter. Moreover, the usage of @FieldDefaults makes every instance field private final.
// @Value is for value classes, i.e., classes whose instances are immutable. If you want a field to be mutable, then you clearly don't have a value class. All observable aspects of the object are immutable.
@Value
@Component
@RequiredArgsConstructor
public class MagicLinkOneTimeTokenGenerationSuccessHandler implements OneTimeTokenGenerationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(OneTimeTokenGenerationSuccessHandler.class);
    MailService mailService;
    OneTimeTokenGenerationSuccessHandler redirectHandler = new RedirectOneTimeTokenGenerationSuccessHandler("/account/ott-sent");

    @SneakyThrows
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken) throws IOException, ServletException {
        // UriComponentsBuilder and UriComponents are the utility class in Spring Framework for building and modifying URLs. UriComponentsBuilder is encoding your URI in accordance with RFC 3986

        //UriComponentsBuilder b1 = UriComponentsBuilder.fromHttpUrl("https://www.example.com/hotels/42?filter=f1&filter=f2&option&query=#hash");
        //UriComponents c1 = b1.build();
        //assertEquals("https", c1.getScheme());
        //assertEquals("www.example.com", c1.getHost());
        //assertEquals(-1, c1.getPort());
        //assertEquals(Lists.list("hotels", "42"), c1.getPathSegments());
        //assertEquals("f1", c1.getQueryParams().getFirst("filter"));
        //assertNull(c1.getQueryParams().getFirst("option"));
        //assertEquals("", c1.getQueryParams().getFirst("query"));
        //assertEquals("hash", c1.getFragment());

        //UriComponentsBuilder b2 = b1.cloneBuilder();
        //b2.path("/info");
        //assertEquals("https://www.example.com/hotels/42/info?filter=f1&filter=f2&option&query=#hash", b2.build().toUriString());

        //UriComponentsBuilder b3 = b1.cloneBuilder();
        //b3.replacePath("/info/hotels/42");
        //assertEquals("https://www.example.com/info/hotels/42?filter=f1&filter=f2&option&query=#hash", b3.build().toUriString());

        //UriComponentsBuilder b4 = b1.cloneBuilder();
        //b4.replaceQuery(null);
        //b4.fragment(null);
        //b4.userInfo("user1");
        //assertEquals("https://user1@www.example.com/hotels/42", b4.build().toUriString());

        //UriComponentsBuilder b5 = b1.cloneBuilder();
        //b5.queryParam("query", "q1", "q2");
        //assertEquals("https://www.example.com/hotels/42?filter=f1&filter=f2&option&query=&query=q1&query=q2#hash", b5.build().toUriString());

        //UriComponentsBuilder b6 = b1.cloneBuilder();
        //b6.replaceQueryParam("query", "q1", "q2");
        //assertEquals("https://www.example.com/hotels/42?filter=f1&filter=f2&option&query=q1&query=q2#hash", b6.build().toUriString());

//        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("www.baeldung.com").path("/{article-name}").buildAndExpand("junit-5");
//        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("www.google.com").path("/").query("q={keyword}").buildAndExpand("baeldung");
//        String template = "/myurl/{name:[a-z]{1,5}}/show";
//        UriComponents uriComponents = UriComponentsBuilder.fromUriString(template).build();
//        uriComponents = uriComponents.expand(Collections.singletonMap("name", "test"));

        // request.getContextPath()- return root path of your application, while ../ - returns parent directory of a file.
        //You use request.getContextPath(), as it will always point to root of your application. If you were to move your jsp file from one directory to other, nothing needs to be changed. Now, consider second approach. If you were to move your jsp files from one folder to other, you'd have to make changed at every location where your are referring your files.

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                // .path("/login/ott") // default Submit url
                .path("/account/ott-submit") // custom Submit url
                .queryParam("token", oneTimeToken.getTokenValue());

        // base_url/login/ott?token=<token>
        String magicLink = builder.toUriString();
        log.info(magicLink);

        mailService.sendHtml(oneTimeToken.getUsername(),
                "Your Spring Security One Time Token",
                """
                Use the following link to sign in into the application: <a href="%s">%s</a>
                """.formatted(magicLink, magicLink));

        this.redirectHandler.handle(request, response, oneTimeToken);
    }
}
