package blockchain.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashUtils {
    private static byte[] generateRandomBytes() {
        byte[] randomBytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    public static byte[] generateHash() {
        try {
            MessageDigest message = MessageDigest.getInstance("SHA3-256");
            return message.digest(generateRandomBytes());
        } catch (NoSuchAlgorithmException exception) {
            System.out.println(exception.getMessage());
            return new byte[] {};
        }
    }

    public static byte[] generateHash(byte[] data) {
        try {
            MessageDigest message = MessageDigest.getInstance("SHA3-256");
            return message.digest(data);
        } catch (NoSuchAlgorithmException exception) {
            System.out.println(exception.getMessage());
            return new byte[] {};
        }
    }

    public static byte[] generateHash(String data) {
        try {
            MessageDigest message = MessageDigest.getInstance("SHA3-256");
            return message.digest(Common.hexToBytes(data));
        } catch (NoSuchAlgorithmException exception) {
            System.out.println(exception.getMessage());
            return new byte[] {};
        }
    }
}
