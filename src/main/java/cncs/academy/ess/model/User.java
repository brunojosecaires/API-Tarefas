package cncs.academy.ess.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.time.Instant;

public class User {
    private int id;
    private String username;
    private String password;
    private byte[] salt;
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User(int id, String username, String password, byte[] salt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
    }
        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }
        public String getUsername() {
            return username;
        }
        public String getPassword() {
            return password;
        }
        public byte[] getSalt() { return salt;}
        public String getToken() {
        try {

            Instant now = Instant.now();
            Instant nowAhead = now.plusSeconds(60);

            //System.out.println(now + " / " + nowAhead);

            Algorithm algorithm = Algorithm.HMAC256("mysecret");
            String token = JWT.create()
                    .withClaim("issuer","SESARAM")
                    .withClaim("username",this.getUsername())
                    .withClaim("issuedAt", now.toString())
                    //.withClaim("expiredAt", nowAhead.toString())
                    .withExpiresAt(nowAhead)
                    .sign(algorithm);

            return token;

        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
        }


        return null;

    }
}

