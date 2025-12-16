package org.springblade.modules.beixiang.util;

import org.apache.commons.lang3.StringUtils;
import org.springblade.core.redis.cache.BladeRedis;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SerialNumberGenUtil {
	public static String createAutoID(BladeRedis bladeRedis, String keyPre, int size) {
		//加上时间戳 如果不需要 时分秒可以去掉
		String datetime = new SimpleDateFormat("yyyyMMdd").format(new Date());
		//这里是 Redis key的前缀
		String key = MessageFormat.format("{0}:{1}", keyPre, datetime);
		//查询 key 是否存在， 不存在返回 1 ，存在的话则自增加1
		Long autoID = bladeRedis.incr(key);
		bladeRedis.expire(key, 1 * 24 * 60 * 60L);
		//这里是 size 位id，如果位数不够可以自行修改 ，下面的意思是 得到上面 key 的 值，位数为size ，不够的话在左边补 0 ，比如  110 会变成  000110
		String value = StringUtils.leftPad(String.valueOf(autoID), size, "0");
		return value;
	}

}
