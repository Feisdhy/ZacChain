package blockchain.util;

public class Common {
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b)); // 使用大写字母表示十六进制
        }
        return result.toString();
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] result = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return result;
    }

    public static byte[] bytesToNibbles(byte[] bytes) {
        byte[] nibbles = new byte[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++) {
            nibbles[i * 2] = (byte) ((bytes[i] >> 4) & 0x0F);
            nibbles[i * 2 + 1] = (byte) (bytes[i] & 0x0F);
        }
        return nibbles;
    }

    public static byte[] nibblesToBytes(byte[] nibbles) {
        if (nibbles.length % 2 != 0) {
            throw new IllegalArgumentException("Nibble array length must be even.");
        }
        byte[] bytes = new byte[nibbles.length / 2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (((nibbles[i * 2] & 0x0F) << 4) | (nibbles[i * 2 + 1] & 0x0F));
        }
        return bytes;
    }
}
