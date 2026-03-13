package cncs.academy.ess.controller;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.http.UnauthorizedResponse;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationMiddleware implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationMiddleware.class);
    private final UserRepository userRepository;
    private Enforcer enforcer = new Enforcer("src/main/resources/casbin/model.conf", "src/main/resources/casbin/policy.csv");

    public AuthorizationMiddleware(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        // if method is OPTIONS bypass auth middleware
        if (ctx.method() == HandlerType.OPTIONS) {
            // Optionally: validate if it is a legitimate CORS preflight
            return;

        }

        // Allow unauthenticated requests to /user (register) and /login

       // if (ctx.path().equals("/user") && ctx.method().name().equals("POST") ||
       //         ctx.path().equals("/login") && ctx.method().name().equals("POST"))
       //     return;
        if (ctx.path().equals("/login") && ctx.method().name().equals("POST"))
            return;


        // Check if authorization header exists
        String authorizationHeader = ctx.header("Authorization");
        String path = ctx.path();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Authorization header is missing or invalid '{}' for path '{}'", authorizationHeader, path);
            throw new UnauthorizedResponse();
        }

        // Extract token from authorization header
        String token = authorizationHeader.substring(7); // Remove "Bearer "

        // Check if token is valid (perform authentication logic)
        int userId = validateTokenAndGetUserId(token);
        if (userId == -1) {
            logger.info("Authorization token is invalid {}", token  );
            throw new UnauthorizedResponse();
        } else {
            if (checkAcessControl(ctx, userId)) {
                // Add user ID to context for use in route handlers
                ctx.attribute("userId", userId);
            } else {
                logger.info("Operation not Authorized for user:"+userId, ctx.path(),ctx.method().name());
                throw new UnauthorizedResponse();
            }
        }
    }

    private boolean checkAcessControl(Context ctx, int userid){
        String path= ctx.path();
        String method= ctx.method().name();
        String username= userRepository.findById(userid).getUsername();
        logger.info("Enforcer", path,method,username );
        return enforcer.enforce(username,path,method);

    }


    /**
     * NOTE: This method currently uses username lookup as a placeholder for real token validation.
     * Replace with proper token parsing/verification (e.g., JWT, session lookup) as needed.
     */
    public Integer validateTokenAndGetUserId(String token) {
        DecodedJWT decodedJWT;
        String username = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256("mysecret");
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify any specific claim validations
                    .withClaim("issuer","SESARAM")
                    // reusable verifier instance
                    .build();

            decodedJWT = verifier.verify(token);
            username = decodedJWT.getClaim("username").asString();
            System.out.println("decoded USERNAME: " + username);




        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            System.out.println("Invalid signature/claims");
        }


        // Placeholder behavior: treat token as username (legacy behavior)
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return -1;
        }
        return user.getId();
    }
}

