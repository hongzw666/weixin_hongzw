package com.hong.commons.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hong.commons.domain.ResponseError;
import com.hong.commons.domain.ResponseMessage;
import com.hong.commons.domain.ResponseToken;

@Service
public class TokenManagerImpl implements TokenManager {
	private static final Logger LOG = LoggerFactory.getLogger(TokenManagerImpl.class);
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	@Qualifier("tokenRedisTemplate")
	private RedisTemplate<String, ResponseToken> tokenRedisTemplate;

	@Override
	public String getToken(String account) {
		BoundValueOperations<String, ResponseToken> ops = tokenRedisTemplate.boundValueOps("weixin_access_token");
		ResponseToken token = ops.get();
		LOG.trace("获取令牌结果：{ }",token);
		
		if (token == null) {
			for (int i = 0; i < 10; i++) {
				Boolean locked = tokenRedisTemplate.opsForValue()
								 .setIfAbsent("weixin_access_token_lock",new ResponseToken());
				
				LOG.trace("没有令牌，增减事务锁结果：{ }",token);
				if (locked == true) {
					try {
						token = ops.get();
						if (token == null) {
							LOG.trace("再次检查令牌，还是没有调用远程接口");
							token = getRemoteToken(account);
							
							ops.set(token);
							ops.expire(token.getExpiresIn() - 60, TimeUnit.SECONDS);
						}else{
							LOG.trace("再次检查令牌，已经有令牌在Redis里面，直接使用");
						}

						break;
					} finally {
						
						LOG.trace("删除令牌事务锁");
						tokenRedisTemplate.delete("weixin_access_token_lock");
						synchronized (this) {
							this.notifyAll();
						}
						
					}

					
				} else {
					
					synchronized (this) {
						try {
							LOG.trace("其他线程锁定了令牌，无法获得锁，等待。。。。");
							this.wait(1000 * 60);
						} catch (InterruptedException e) {
							LOG.error("等待获取分布式的事务锁出现问题：" + e.getLocalizedMessage(), e);
							break;
						}
					}
				}
			}
		}
		if (token != null) {
			return token.getAccessToken();
		}
		return null;
	}

	public ResponseToken getRemoteToken(String account) {

		String appid = "wx250bebfedb705aa0";
		String appsecret = "64d8f905d9833d473d51d62851d99cbf";

		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
				+ "&appid=" + appid
				+ "&secret=" + appsecret;

		HttpClient hc = HttpClient.newBuilder()
				.version(Version.HTTP_1_1)
				.build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.GET()
				.build();

		ResponseMessage rm;
		try {
		
			HttpResponse<String> response = hc.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));

			String body = response.body();

			if (body.contains("errcode")) {
				
				rm = objectMapper.readValue(body, ResponseError.class);
				rm.setStatus(2);
			} else {
				
				rm = objectMapper.readValue(body, ResponseToken.class);
				rm.setStatus(1);
			}
			
			if (rm.getStatus() == 1) {
				return ((ResponseToken) rm);
			}
		} catch (Exception e) {
			throw new RuntimeException("无法获取令牌，因为：" + e.getLocalizedMessage());
		}

		throw new RuntimeException("无法获取令牌，因为：错误代码=" 
				+ ((ResponseError) rm).getErrorCode() 
				+ "错误描述=" + ((ResponseError) rm).getErrorMessage());
	}

}
