package com.example.chatplatform.util;

import cn.hutool.core.util.RandomUtil;
import com.example.chatplatform.entity.enums.UserContactTypeEnum;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

/**
 * @author archi
 */
public class GenerateUtils {
    //生成11位UserId 如 U1234567890
    public static String generateUserUid(){
        return UserContactTypeEnum.USER.getPrefix()+ UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0,11);
    }
    //生成11位群聊id 如G1234567890
    public static String generateGroupUid(){
        return UserContactTypeEnum.GROUP.getPrefix()+ UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0,11);
    }
    //生成随机短UID
    public static String generateShortUID(){
        long timestamp = Instant.now().toEpochMilli();
        String randomString = generateRandomString(5);
        return timestamp + randomString;
    }
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
