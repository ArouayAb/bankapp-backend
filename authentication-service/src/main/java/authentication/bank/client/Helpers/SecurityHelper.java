package authentication.bank.client.Helpers;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

// This helper class is used to simplify Security related actions such as hashing and generating tokens
public class SecurityHelper {

    public SecurityHelper(){
        super();
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static KeyPair generateKeyPair(String algorithm) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException
    {
        // Get this from Private Key location.
        String pemPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2nslpoa9ykp7hcQIv4fyw3TJ/HBTnomWhwsYtIgD6UeqDNfcyoIxoB188MobsXC8V0aCrZSMc+2F3y+y0TLL8lgWeU+59ibNVZFxrZZoz1w0MruHnxoyR2PFfD21yU9BD204G/m8O6QXuUepQQ85RAJ/Sgv32EPRpcXxndH3gQIbqP+hxGEy1yhIV907PTVdY1YV8JBVWWkaI8xAiBJI7OsLo9ztQF8HzWzo8J+LEPTn8AXyR7fVa7TFB1kjcZEZnP80PP5gYMg/PTcOk2upg6d5ipo6zsHQoKbFWNKtQVMcOH5UUZymxGPLq/HJTZ7PAtZNKYPqSqWyjWbVlazB2QIDAQAB";
        String pemPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDaeyWmhr3KSnuFxAi/h/LDdMn8cFOeiZaHCxi0iAPpR6oM19zKgjGgHXzwyhuxcLxXRoKtlIxz7YXfL7LRMsvyWBZ5T7n2Js1VkXGtlmjPXDQyu4efGjJHY8V8PbXJT0EPbTgb+bw7pBe5R6lBDzlEAn9KC/fYQ9GlxfGd0feBAhuo/6HEYTLXKEhX3Ts9NV1jVhXwkFVZaRojzECIEkjs6wuj3O1AXwfNbOjwn4sQ9OfwBfJHt9VrtMUHWSNxkRmc/zQ8/mBgyD89Nw6Ta6mDp3mKmjrOwdCgpsVY0q1BUxw4flRRnKbEY8ur8clNns8C1k0pg+pKpbKNZtWVrMHZAgMBAAECggEARzRhZe9+BWLW9RaZv0pgXUE/3y0Ao6e1zldYeuXlWG8JxdvIUpux7rwNjzBfbCoQx+TsDARg7htYSQ/zmGiUvWvmDy1jrwygWSXoxalCiWpEfNBPeBQClO+4WjCGgZB55Lw00DFJHqrxLGpliBWdFUQ3Ffvmj64ysnNbtpLKtllOSnbVrE2aE+yW4IKpXF1496XS+j1RbPrzE6+XkQwf0ee1PceK+NFRFyeab/QARS/+zED2j7RBI5QIza8MC5OBQroN0xo3/oipg88dB1ZMcZLxy/aO7O6p5GKdY2OeXASoJi51tj+K5IVdtzXoSDHSn62avnY4q3OiIl/MWvzbjQKBgQD0lfFJ2H1J+kpEj10L34x4PC6rw5iN4Rl9N2MPDl2yD+s/dxB9/fz0U8ERFOfUo8gqSTl/oS/r913axaDpviN+YIpk8rtdXjX8HWaCreltSO9x9QtsRoenMrIBgAtUs5j1o4tCZy6+WC8E4V8wiofYB7R4CsXcTkLVzVWpcmbI7wKBgQDkrVU5efbLWRPqk4q+8Ng783mCK4as3tG0KvrHcXytnI7zmgmo8QumL2NoQP2S6TLIH7xp0jVkodvRjsz5xjoYJafr7wn7s6mS1bAzB7AL+hmWejbn5lS2RX+rKx6anEoYc+jxha/+pKe3JKko4B4WHQ+DcxXY8Q5hg7JPTljRtwKBgQDqpbd5GyMICAGcSNYBgBRpfYNg8iO3ag9kG/EDDstA+xi8KGRAG53EYV3GH4JSdFaiiuGI2oD5JrZ6HkPEO9AdfE0Jj/3FE49DMomYMXTSjbh6YZb156xY8b/oxIkaV7sVXjNKH2eu4TcqvXTvQtgUnz+6MsukEcj5CSd3ivOlLQKBgQCcOqds+xxW1Oa8bkxuItE4NCEhg2chF6GhO3PVan1JhxpK50QOPZA920ZI5Y3YHqXo/3WTvl9n/wTU6IC1bG4oLSHKGKdzZM1HWeJlGq11oUp1+c0lJcFePaD5ah4gdwJQLnBJAZK5n/hNMSi4mlUzumrs3WVhr16p1HWvpNEdhQKBgC4ehjRhOo0sUiNZ43kjRPchY8tSnPl0NeeKCTPbPWYGjiErgKQtLB3C7KDJAT5ECIRxvtuKOaTmOtCKNEkx/9QzAq2e75A5gyk6JzVT7lTUNBaNx3xI5NBZLGSzXD3c0LZb7v2UMfSJvTyN88tsgJC7bdkFFwOTf8ItOtZY3Exb";

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pemPublicKey));
        PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

        PKCS8EncodedKeySpec prvKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pemPrivateKey));
        PrivateKey privateKey = keyFactory.generatePrivate(prvKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    public static byte[] generateHash(String inputString, byte[] salt) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException
    {
        KeySpec spec = new PBEKeySpec(inputString.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    public static String generateJwt(KeyPair key, int payload) throws
            JoseException
    {
        JwtClaims claims = new JwtClaims();
        claims.setExpirationTimeMinutesInTheFuture(1);
        claims.setClaim("id", payload);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(key.getPrivate());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return jws.getCompactSerialization();
    }

    public static JwtClaims processJwt(KeyPair key, String jwt) throws
            InvalidJwtException
    {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setVerificationKey(key.getPublic())
                .setJweAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
                .build();

        return jwtConsumer.processToClaims(jwt);
    }

    public static String generateRefreshToken() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }
}

