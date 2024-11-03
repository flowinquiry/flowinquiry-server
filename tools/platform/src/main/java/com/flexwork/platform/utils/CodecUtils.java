package com.flexwork.platform.utils;

import java.nio.ByteBuffer;
import java.util.Base64;

public class CodecUtils {

    public static String encodeLongToBase64(Long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(value);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }


    public static Long decodeBase64ToLong(String base64) {
        byte[] bytes = Base64.getUrlDecoder().decode(base64);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getLong();
    }
}
