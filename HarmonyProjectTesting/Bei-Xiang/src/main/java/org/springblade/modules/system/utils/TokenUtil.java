package org.springblade.modules.system.utils;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class TokenUtil {
	@Resource
	//RedisTemplate<String,String> redisTemplate;
	private BladeRedis bladeRedis;

	/**
	 * 创建秘钥
	 */
	private static final byte[] SECRET = "qngChengBoYa-realtimeWuIngWangJiaQiZhangYv".getBytes();

	/**
	 * 生成token
	 * @param account
	 * @return {@link String}
	 */
	public  String buildToken(String account) {

		try {
			/**
			 * 1.创建一个32-byte的密匙
			 */
			MACSigner macSigner = new MACSigner(SECRET);
			/**
			 * 2. 建立payload 载体
			 */
			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.subject("login")
				.claim("ACCOUNT",account)
				.issueTime(new Date())
				.build();

			/**
			 * 3. 建立签名
			 */
			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
			signedJWT.sign(macSigner);

			/**
			 * 4. 生成token
			 */
			String token = signedJWT.serialize();
			//redisTemplate.opsForValue().set(account, token,3,TimeUnit.HOURS);
			bladeRedis.setEx(account, token,8*60*60l);
			return token;
		} catch (KeyLengthException e) {
			e.printStackTrace();
		} catch (JOSEException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 校验token
	 * @param token
	 * @return
	 */
	public  boolean verifyToken(String token) {

		try {
			SignedJWT jwt = SignedJWT.parse(token);
			JWSVerifier verifier = new MACVerifier(SECRET);

			/**
			 * 校验是否有效
			 */
			if (!jwt.verify(verifier)) {
				return false;
			}
			/**
			 * 获取载体中的数据
			 */
			String account = (String) jwt.getJWTClaimsSet().getClaim("ACCOUNT");
			//是否有
			if (Objects.isNull(account)){

				return false;
			}
			/**
			 * 判断redis里是否有account为key的值，如果有
			 * 判断token是否和redis里存的是是否一样，
			 * 如果不一样说明已经有其他账号登录了，则回到登录页面
			 * 如果一样，则给token续期
			 */
			/*if (redisTemplate.hasKey(account)){
				String s = redisTemplate.opsForValue().get(account);
				if (s.equals(token)){
					redisTemplate.expire(account,3,TimeUnit.HOURS);
					return true;
				}
				//throw new ServiceException("有其他设备登录");
			}*/
			if (bladeRedis.exists(account)){
				String s =bladeRedis.get(account);
				if (s.equals(token)){
					bladeRedis.setEx(account,token,8*60*60l);
					return true;
				}
				//throw new ServiceException("有其他设备登录");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JOSEException e) {
			e.printStackTrace();
		}
		return false;
	}



}
