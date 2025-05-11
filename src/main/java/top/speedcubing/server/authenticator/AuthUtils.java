package top.speedcubing.server.authenticator;

import com.google.common.base.Preconditions;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;

public class AuthUtils {

    public static String generateSecret() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] buffer = new byte[30];
            random.nextBytes(buffer);
            byte[] secret = Arrays.copyOf(buffer, 10);
            return new String(new Base32().encode(secret));
        } catch (NoSuchProviderException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean authorize(String secret, int verificationCode) {
        Preconditions.checkNotNull(secret);

        if (verificationCode > 0 && verificationCode < 1000000) {
            return checkCode(secret, verificationCode, (new Date()).getTime());
        }

        return false;
    }


    private static boolean checkCode(String secret, int code, long timestamp) {
        byte[] decodedKey = new Base32().decode(secret);

        long timeWindow = timestamp / TimeUnit.SECONDS.toMillis(30L);

        for (int i = -1; i <= 1; ++i) {
            int hash = calculateCode(decodedKey, timeWindow + (long) i);
            if (hash == code) {
                return true;
            }
        }

        return false;
    }


    static int calculateCode(byte[] key, long tm) {
        byte[] data = new byte[8];
        long value = tm;

        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) ((int) value);
        }

        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);
            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 15;
            long truncatedHash = 0L;

            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 255);
            }

            truncatedHash &= 2147483647L;
            truncatedHash %= 1000000L;
            return (int) truncatedHash;
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new RuntimeException();
        }
    }
}
