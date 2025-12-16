package org.springblade.modules.beixiang.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.google.gson.Gson;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.common.constant.ServiceConstant;
import org.springblade.common.utils.OkHttpUtil;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.beixiang.entity.RelUser;
import org.springblade.modules.beixiang.entity.Weight;
import org.springblade.modules.beixiang.service.AccountService;
import org.springblade.modules.beixiang.service.BillService;
import org.springblade.modules.beixiang.service.RelUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class IotController {

	private static final Logger log = LoggerFactory.getLogger(IotController.class);

	static String token;

	@Resource
	private BillService billService;

	@Resource
	private BladeRedis bladeRedis;

	@Resource
	private AccountService accountService;

	@Resource
	private RelUserService relUserService;

	@PostConstruct
	public void construct() {
		R<String> result = login("test035",
			"12345035");
		String token = result.getData();

	}


	//上报数据
	@PostMapping("iot_report")
	@Transactional(rollbackFor = Exception.class)
	public R report(@RequestBody Map<String, Object> param) {
		try {

			log.info("接受数据: {}", param);
			Map<String, Object> notifyData = (Map<String, Object>) param.get("notify_data");

			Map<String, Object> body = (Map<String, Object>) notifyData.get("body");
			Map<String, Object> header = (Map<String, Object>) notifyData.get("header");
			String deviceId = "";

			if (header != null) {
				//获取设备id
				deviceId = (String) header.get("device_id");
			}

			if (body != null) {
				List<Object> services = (List<Object>) body.get("services");
				if (services != null && !services.isEmpty()) {
					Map<String, Object> firstService = (Map<String, Object>) services.get(0);
					//获取上报时间
					String eventTime = (String) firstService.get("event_time");
					//获取上报的属性值
					Map<String, Object> properties = (Map<String, Object>) firstService.get("properties");
					String ntpTime = (String) properties.get("ntp_time");
					Double first = getWeightByStrToDouble((String) properties.get("weight_first"));
					Double second = getWeightByStrToDouble((String) properties.get("weight_second"));
					Double third = getWeightByStrToDouble((String) properties.get("weight_third"));
					Double fourth = getWeightByStrToDouble((String) properties.get("weight_fourth"));

					Weight weight = new Weight();
					weight.setWeightFirst(first);
					weight.setWeightSecond(second);
					weight.setWeightThird(third);
					weight.setWeightFourth(fourth);
					weight.setDeviceId(deviceId);
					weight.setRecentReportTime(eventTime);
					Gson gson = new Gson();
					String toJson = gson.toJson(weight);
					Double total = first + second + third + fourth;


					Map<String, Double> weightMap = new HashMap<>();
					weightMap.put("first", first);
					weightMap.put("second", second);
					weightMap.put("third", third);
					weightMap.put("fourth", fourth);

					RelUser relUser = relUserService.getOne(new LambdaQueryWrapper<RelUser>().eq(RelUser::getNtpTime, ntpTime));
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date recentTime = sf.parse(eventTime);
					synchronized (this){
						if (relUser != null) {
							relUser.setWeightInfo(toJson);
							relUser.setNtpTime(ntpTime);
							relUserService.updateById(relUser);
							Long userId = null;
							if (StrUtil.isNotEmpty(relUser.getUserId())) {
								userId = Long.parseLong(relUser.getUserId());
							}
							log.info("上传时间: {},识别用户: {}", ntpTime, userId);
							billService.handleData(deviceId, weightMap, recentTime, ntpTime, userId);
						} else {
							if (!bladeRedis.exists("weight_total")) {
								bladeRedis.set("weight_total", total);
								relUser = new RelUser();
								relUser.setWeightInfo(toJson);
								relUser.setNtpTime(ntpTime);
								relUserService.save(relUser);
							} else {
								String weight_total = bladeRedis.get("weight_total");
								double parseDouble = Double.parseDouble(weight_total);
								double value = parseDouble - total;
								if (Math.abs(value) > 5.0) {
									relUser = new RelUser();
									relUser.setWeightInfo(toJson);
									relUser.setNtpTime(ntpTime);
									relUserService.save(relUser);
									bladeRedis.set("weight_total", total + "");
								}
							}
						}
					}

				}
			}

			log.info("测试");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("解析参数失败");
		}
		return R.status(true);


	}

	private Double getWeightByStrToDouble(String str) {
		String weight = str.replace("g", "");
		return Double.valueOf(weight);
	}

	@PostMapping("iot_control")
	public R control(@RequestBody Map<String, Object> param) {
		try {
			String deviceId = param.get("deviceId").toString();

			String jsonStr = JSON.toJSONString(param);
			System.out.println("iot_report jsonStr: " + jsonStr);
			//将收到的消息转发给webSocket客户端
		} catch (Exception e) {
			System.out.println("解析参数失败");
		}
		return R.status(true);
	}

	@GetMapping("iot_devices")
	public R getDevices() {
		try {

			String url = ServiceConstant.BASE_URL + "/api/product/platformDevice/list";
			HttpResponse response = HttpUtil.createPost("http://117.78.16.25:9999/api/product/platformDevice/list")
				.header("Content-Type", "application/json")
				.body("{}")
				.header("token", getToken())
				.timeout(20000)
				.execute();
			JSONObject object = JSON.parseObject(response.body());
			String code = object.getString("code");
			if (code.equals("50014")) {

				throw new ServiceException("未登录或登录失效重新登录");


			}
			JSONObject data = object.getJSONObject("data");

			String jsonStr = JSON.toJSONString(data);
			System.out.println("iot_report jsonStr: " + jsonStr);
			//将收到的消息转发给webSocket客户端
		} catch (Exception e) {
			System.out.println("解析参数失败");
		}
		return R.status(true);
	}

	@GetMapping("iot_deviceStatus")
	public R<String> getDeviceStatus() {
		try {
			String deviceId = "kixzybvctfbj-1800739805722574925_smart_warehouse";
			String appId = "r3mcn013stoy-1612994706281594953";
			String id = "990";
			//String url = baseUrl + "/api/product/platformDeviceData/detail/" + deviceId;
			//String api = String.format("/product/platformDevice/state/{appId}/{deviceId}/{id}", appId, deviceId, id);
			String api = "/api/product/platformDevice/state/" + appId + "/" + deviceId + "/" + id;
			String url = ServiceConstant.BASE_URL + api;

            /*HttpResponse response = HttpUtil.createPost(url)
                .header("Content-Type", "application/json")
                .body("{}")
                .header("token", getToken())
                .timeout(20000)
                .execute();*/

			Map<String, String> header = new HashMap<>();
			header.put("client_type", "web");
			header.put("Content-Type", "application/json");
			header.put("token", getToken());
			log.info("token: {}", getToken());
			//String body = JSON.toJSONString(param);
			Response response = OkHttpUtil.postJson(url, "{}", header);
			String s = response.body().string();
			JSONObject object = JSON.parseObject(s);
			//JSONArray data = object.getJSONArray("data");
			JSONObject data = object.getJSONObject("data");
			//JSONObject jsonObject = data.getJSONObject(0);
			//String reportTime = jsonObject.getString("reportTime");
			String status = data.getString("deviceState");
			String jsonStr = JSON.toJSONString(data);
			System.out.println("iot_report jsonStr: " + jsonStr);
			//将收到的消息转发给webSocket客户端
			return R.data(status);
		} catch (Exception e) {
			System.out.println("解析参数失败");
		}
		return R.data(null);
	}

	@PostMapping("login")
	public R<String> login(String username, String password) {
		try {

			String url = "http://117.78.16.25:9999/api/login";
			Map<String, String> param = new HashMap<>();
			param.put("username", username);
			param.put("password", password);
			Map<String, String> header = new HashMap<>();
			header.put("client_type", "web");
			header.put("Content-Type", "application/json");
			String body = JSON.toJSONString(param);
			String s = OkHttpUtil.postJson(url, body, header).body().string();
			JSONObject object = JSON.parseObject(s);
			JSONObject data = object.getJSONObject("data");
			token = data.getString("token");
			System.out.println("token: " + token);
			bladeRedis.getStringRedisTemplate().opsForValue().set("iot_token", token);
			//将收到的消息转发给webSocket客户端
			//websocket.sendOneMessage("1",token);
		} catch (Exception e) {
			System.out.println("解析参数失败");
		}
		return R.data(token);
	}

	static public String getToken() {
		return token;
	}


}
