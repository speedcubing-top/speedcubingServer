package top.speedcubing.server.utils;

public class RankSystem {

    public static String playerNameEncode(String name) {
        int buffer = 0;
        int nBitsIn = 0;
        StringBuilder builder = new StringBuilder();
        for (char c : name.toCharArray()) {
            //57('9') -> c - '0'                 c = c - 48    // 0~9 10
            //90('Z') -> c - ('A' - 10)          c = c - 55    // A~Z 26
            //95('_') -> (26 + 10)               c = 36        // _   1
            //other   -> c - ('a' - 10 - 26 - 1) c = c - 59    // a~z 26
            int b = (c <= 57 ? c - 48 : (c <= 90 ? c - 55 : (c == 95 ? 36 : c - 60)));
            nBitsIn += 6;
            buffer |= (b << (32 - nBitsIn));
            while (nBitsIn >= 16) {
                builder.append((char) (buffer >>> 16));
                nBitsIn -= 16;
                buffer <<= 16;
            }
        }
        if (nBitsIn != 0) {
            builder.append((char) (buffer >>> 16));
        }
        return builder.toString();
    }
}
