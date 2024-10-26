package com.example.chatplatform.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.extern.slf4j.Slf4j;
import com.example.chatplatform.entity.constants.RedisKeys;
import com.example.chatplatform.entity.constants.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author archi
 */
@Component
@Slf4j
public class RedisUtils {
    private static StringRedisTemplate STRING_REDIS_TEMPLATE;

    public static Set<String> keys(String key) {
        return STRING_REDIS_TEMPLATE.keys(key);
    }


    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate){
        RedisUtils.STRING_REDIS_TEMPLATE = stringRedisTemplate;
    }
    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间
     * @return true / false
     */
    public static boolean expire(String key,long time,TimeUnit timeUnit){
        try {
            if(time>0){
                STRING_REDIS_TEMPLATE.expire(key,time, timeUnit);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static long getExpiration(String key){
        return STRING_REDIS_TEMPLATE.getExpire(key,TimeUnit.MINUTES);
    }
    public static boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(STRING_REDIS_TEMPLATE.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @SuppressWarnings("unchecked") 忽略类型转换警告
     * @param key 键（一个或者多个）
     */
    @SuppressWarnings("unchecked")
    public static void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                STRING_REDIS_TEMPLATE.delete(key[0]);
            } else {
                //传入一个 Collection<String> 集合
                STRING_REDIS_TEMPLATE.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        return StringUtils.hasLength(key) ? STRING_REDIS_TEMPLATE.opsForValue().get(key) : null;
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true / false
     */
    public static boolean set(String key, String value) {
        try {
            STRING_REDIS_TEMPLATE.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间（秒），如果 time < 0 则设置无限时间
     * @return true / false
     */
    public static boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                STRING_REDIS_TEMPLATE.opsForValue().set(key, value, time, TimeUnit.MINUTES);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 递增大小
     * @return
     */
    public static long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于 0");
        }
        return STRING_REDIS_TEMPLATE.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 递减大小
     * @return
     */
    public static long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于 0");
        }
        return STRING_REDIS_TEMPLATE.opsForValue().increment(key, delta);
    }



    /**
     * HashGet
     * @param key 键（no null）
     * @param item 项（no null）
     * @return 值
     */
    public static Object hget(String key, String item) {
        return STRING_REDIS_TEMPLATE.opsForHash().get(key, item);
    }

    /**
     * 获取 key 对应的 map
     * @param key 键（no null）
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hmget(String key) {
        return STRING_REDIS_TEMPLATE.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 值
     * @return true / false
     */
    public static boolean hmset(String key, Map<Object, Object> map) {
        try {
            STRING_REDIS_TEMPLATE.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 值
     * @param time 时间
     * @return true / false
     */
    public static boolean hmset(String key, Map<Object, Object> map, long time,TimeUnit timeUnit) {
        try {
            STRING_REDIS_TEMPLATE.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time,timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张 Hash表 中放入数据，如不存在则创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true / false
     */
    public static boolean hset(String key, String item, Object value) {
        try {
            STRING_REDIS_TEMPLATE.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张 Hash表 中放入数据，并设置时间，如不存在则创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间（如果原来的 Hash表 设置了时间，这里会覆盖）
     * @return true / false
     */
    public static boolean hset(String key, String item, Object value, long time,TimeUnit timeUnit) {
        try {
            STRING_REDIS_TEMPLATE.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time,timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除 Hash表 中的值
     * @param key 键
     * @param item 项（可以多个，no null）
     */
    public static void hdel(String key, Object... item) {
        STRING_REDIS_TEMPLATE.opsForHash().delete(key, item);
    }

    /**
     * 判断 Hash表 中是否有该键的值
     * @param key 键（no null）
     * @param item 值（no null）
     * @return true / false
     */
    public static boolean hHasKey(String key, String item) {
        return STRING_REDIS_TEMPLATE.opsForHash().hasKey(key, item);
    }

    /**
     * Hash递增，如果不存在则创建一个，并把新增的值返回
     * @param key 键
     * @param item 项
     * @param by 递增大小 > 0
     * @return
     */
    public static Double hincr(String key, String item, Double by) {
        return STRING_REDIS_TEMPLATE.opsForHash().increment(key, item, by);
    }

    /**
     * Hash递减
     * @param key 键
     * @param item 项
     * @param by 递减大小
     * @return
     */
    public static Double hdecr(String key, String item, Double by) {
        return STRING_REDIS_TEMPLATE.opsForHash().increment(key, item, -by);
    }




    /**
     * 将JavaBean转化为Map,然后将其保存到redis中
     * 实体类中的非控制都被转化为String,null值忽略不转化
     *
     * @param bean       要转化的bean
     * @param redisKey   要保存到的redis key
     * @param redisTTL   这个redis key的保存时间
     */
    public static void storeBeanAsHash(Object bean, String redisKey, long redisTTL) {
        Map<String, Object> beanMap = BeanUtil.beanToMap(bean, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> {
                            if (fieldValue == null) {
                                return null; // 如果fieldValue为null，那么从Map中排除这个值
                            }
                            return fieldValue.toString();
                        }));

        STRING_REDIS_TEMPLATE.opsForHash().putAll(redisKey, beanMap);
        STRING_REDIS_TEMPLATE.expire(redisKey, redisTTL, TimeUnit.MINUTES);
    }

    public static void storeBeanAsJson(String redisKey, Object bean, long redisTTL) {
        String jsonValue = JsonUtils.javaBeanToJsonString(bean);
        log.info(jsonValue);
        if (jsonValue != null) {
            STRING_REDIS_TEMPLATE.opsForValue().set(redisKey,jsonValue);
        }
        STRING_REDIS_TEMPLATE.expire(redisKey, redisTTL, TimeUnit.MINUTES);
    }
    /*
        创建一个列表并保存
     */
    public static void storeList(String key, List<String> list,long redisTTL,TimeUnit timeUnit) {
        STRING_REDIS_TEMPLATE.opsForList().rightPushAll(key, list);
        expire(key,redisTTL,timeUnit);
    }
    public static void listRightPush(String key,String value){
        STRING_REDIS_TEMPLATE.opsForList().rightPush(key,value);
    }
    public static void listLeftPush(String key,String value){
        STRING_REDIS_TEMPLATE.opsForList().leftPush(key,value);
    }
    public static void listRightPop(String key){
        STRING_REDIS_TEMPLATE.opsForList().rightPop(key);
    }
    public static String listLeftPop(String key){
        return STRING_REDIS_TEMPLATE.opsForList().leftPop(key);
    }
    /*
        获取列表中所有元素
     */
    public static List<String> getList(String key){
        return STRING_REDIS_TEMPLATE.opsForList().range(key,0,-1);
    }
    public static List<String> getList(String key,Long start,Long end){
        return STRING_REDIS_TEMPLATE.opsForList().range(key,start,end);
    }

    /**
     * 创建一个有序集合并保存
     * @param key
     * @param value
     * @param redisTTL
     */
    public static void storeZSet(String key, Set<String> value, Long redisTTL) {
        value.forEach(item -> STRING_REDIS_TEMPLATE.opsForZSet().add(key, item, 0));
        if (redisTTL != null) {
            STRING_REDIS_TEMPLATE.expire(key, redisTTL, TimeUnit.MINUTES);
        }
    }
    /**
     * 创建一个无序集合并保存
     * @param key
     * @param value
     * @param redisTTL
     */
    public static void storeSet(String key,Set<String>value,Long redisTTL){
        value.forEach(item -> STRING_REDIS_TEMPLATE.opsForSet().add(key, item));
        if (redisTTL != null) {
            STRING_REDIS_TEMPLATE.expire(key, redisTTL, TimeUnit.MINUTES);
        }
    }

    // 添加元素到 Set
    public static void addToSet(String key, String element) {
        STRING_REDIS_TEMPLATE.opsForSet().add(key, element);
    }



    /**
     * 从 ZSet 中删除元素
     * @param key
     * @param element
     */
    public static void removeFromSet(String key, String element) {
        STRING_REDIS_TEMPLATE.opsForSet().remove(key, element);
    }

    /**
     * 获取 ZSet 中的所有元素
     * @param key
     * @return
     */
    public static Set<String> getZSet(String key) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = STRING_REDIS_TEMPLATE.opsForZSet().rangeWithScores(key, 0, -1);
        Set<String> result = new HashSet<>();
        if (typedTuples != null) {
            for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
                result.add(tuple.getValue());
            }
            return result;
        }
        return null;
    }

    public static Set<String> getSet(String key) {
        return STRING_REDIS_TEMPLATE.opsForSet().members(key);
    }


    /**
     * 分页获取 ZSet 中的元素
     * @param key
     * @param page 页数
     * @param size 每页数量
     * @return
     */
    public static Set<String> getZSetPaginated(String key, int page, int size) {
        long startIndex = (long) (page - 1) * size;
        long endIndex = startIndex + size - 1;
        return STRING_REDIS_TEMPLATE.opsForZSet().range(key, startIndex, endIndex);
    }



    /**
     * 判断 ZSet 中是否包含某个元素
     * @param key
     * @param element
     * @return
     */
    public static boolean isMemberOfZSet(String key, String element) {
        Double score = STRING_REDIS_TEMPLATE.opsForZSet().score(key, element);
        return score != null;
    }

    public static List<String> multiGet(Set<String> strings) {
        return STRING_REDIS_TEMPLATE.opsForValue().multiGet(strings);
    }

    public static void saveHeartBeat(String userId){
        RedisUtils.set(RedisKeys.NETTY_HEART_BEAT+userId,"", SystemConstants.HEART_BEAT);
    }
    public static void removeHeartBeat(String userId){
        RedisUtils.del(RedisKeys.NETTY_HEART_BEAT+userId);
    }
}
