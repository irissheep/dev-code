package org.springblade.modules.beixiang.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.utils.StrToDoubleArray;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.modules.beixiang.entity.RelUser;
import org.springblade.modules.beixiang.entity.UserFeature;
import org.springblade.modules.beixiang.entity.Weight;
import org.springblade.modules.beixiang.service.BillService;
import org.springblade.modules.beixiang.service.RelUserService;
import org.springblade.modules.system.entity.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/images")
@Slf4j
@Api(tags = "图片接口")
public class ImageController {
	private static final String UPLOAD_DIR = "/home/tb/data/images/";

	private static final String IMAGE_TEMP = "/home/tb/data/temp/";

	private static final String IMAGE_ARM = "/home/tb/FaceRecognitionDemo/images/contact/";

	private static final String IMAGE_FACE = "/home/tb/FaceRecognitionDemo/images/face/";
	@Resource
	private BladeRedis bladeRedis;

	@Resource
	private RelUserService relUserService;

	@Resource
	private BillService billService;


	@ApiOperation(value = "图片上传", notes = "")
	@PostMapping("/upload")
	public R<Map<String, String>> uploadImage(@RequestPart("image") MultipartFile image, String type, HttpServletRequest request) {

		try {
			Path uploadPath = null;
			String filePath = "";
			String path = "";
			String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

			String name = image.getOriginalFilename();
			int dotIndex = name.lastIndexOf('.');
			String extension = name.substring(dotIndex + 1).toLowerCase();
			String fileName = name.substring(0, dotIndex);
			if (image.isEmpty()) {
				return R.fail("请选择一张图片上传");
			} else if (dotIndex < 0) {
				return R.fail("未知文件");
			}

			if (!("jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension) || "gif".equals(extension))) {
				return R.fail("图片格式不正确");
			}

			String uuid = UUID.randomUUID().toString().replaceAll("-", "");

			String fileUrl = url + "/images/download?fileName=" + name;
			if (!StringUtils.hasLength(type)) {
				uploadPath = Paths.get(IMAGE_TEMP + "images/" + uuid + '.' + extension);
				filePath = url + "/images/download?fileName=" + uuid + '.' + extension;
			} else if (type.equals("1")) {
				uploadPath = Paths.get(UPLOAD_DIR + uuid + '.' + extension);
				filePath = url + "/images/download?fileName=" + uuid + '.' + extension + "&type=1";

			} else if (type.equals("3")) {
				Long incr = bladeRedis.incr(fileName);
				bladeRedis.expire(fileName, 90);
				log.info("增长值: {}", incr);
				uploadPath = Paths.get(IMAGE_FACE + name);
				filePath = fileUrl + "&type=3";
				log.info("IMAGE_FACE : {}", uploadPath);
			} else if (type.equals("4")) {
				//String arm = bladeRedis.get("image:face:" + name);
				uploadPath = Paths.get(IMAGE_ARM + name);
				filePath = fileUrl + "&type=4";
				Long incr = bladeRedis.incr(fileName);
				bladeRedis.expire(fileName, 90);
				log.info("增长值: {}", incr);
				log.info("IMAGE_FACE : {}", uploadPath);
			}
			Path finalUploadPath = uploadPath;

			if (!Files.exists(finalUploadPath.getParent())) {
				Files.createDirectories(finalUploadPath);
			}
			Files.write(finalUploadPath, image.getBytes());

			synchronized (this) {
				if ((type.equals("3") || type.equals("4")) && bladeRedis.getIncr(fileName) == 2) {
					String userId = armRecognition(name).getData();
					RelUser relUser = relUserService.getOne(new LambdaQueryWrapper<RelUser>().eq(RelUser::getNtpTime, fileName));
					if (relUser != null) {
						log.info("上传时间: {},识别用户: {}", fileName, userId);
						relUser.setPictureUrl(fileUrl + "&type=3" + "," + fileUrl + "&type=4");
						relUser.setUserId(userId);
						relUserService.updateById(relUser);
						Gson gson = new Gson();
						Weight weight = gson.fromJson(relUser.getWeightInfo(), Weight.class);
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date recentTime = sf.parse(weight.getRecentReportTime());
						Map<String, Double> weightMap = new HashMap<>();
						weightMap.put("first", weight.getWeightFirst());
						weightMap.put("second", weight.getWeightSecond());
						weightMap.put("third", weight.getWeightThird());
						weightMap.put("fourth", weight.getWeightFourth());
						billService.handleData(weight.getDeviceId(), weightMap, recentTime, fileName, Long.valueOf(userId));
					} else {
						relUser = new RelUser();
						relUser.setNtpTime(fileName);
						relUser.setUserId(userId);
						relUser.setPictureUrl(fileUrl + "&type=3" + "," + fileUrl + "&type=4");
						relUserService.save(relUser);
					}
				}
			}


			Map<String, String> map = new HashMap<>();
			map.put("filePath", filePath);
			return R.data(map, "图片上传成功");
		} catch (Exception e) {
			e.printStackTrace();
			return R.fail("图片上传失败：" + e.getMessage());
		}
	}

	@ApiOperation(value = "图片识别", notes = "")
	@PostMapping("/recognizeImage")
	public R<String> recognizeImage(@RequestPart("image") MultipartFile image, String userId, HttpServletRequest request) {
		if (!StringUtils.hasLength(userId) || image == null) {
			throw new ServiceException("参数缺失");
		}
		R<Map<String, String>> uploadImage = uploadImage(image, "1", request);
		Map<String, String> map = uploadImage.getData();
		String filePath = map.get("filePath");
		userId = userId.substring(userId.indexOf(",") != -1 ? 1 : 0);
		String fileName = filePath.substring(filePath.indexOf("=") + 1, filePath.indexOf("&"));
		String s = recognition(userId, fileName);
		User user = Db.getById(userId, User.class);
//		user.setAvatar(filePath);
		user.setIsCertified(1);
		Db.updateById(user);
		return R.data(s);
	}

	@ApiOperation(value = "图片下载", notes = "")
	@GetMapping("/download")
	public void downloadImage(String fileName, String type, HttpServletResponse response) {

		int dotIndex = fileName.lastIndexOf('.');
		String extension = fileName.substring(dotIndex + 1).toLowerCase();
		Path path = null;
		if (!StringUtils.hasLength(type)) {
			path = Paths.get(IMAGE_FACE, fileName);
		} else if (type.equals("1")) {
			path = Paths.get(UPLOAD_DIR, fileName);
		} else if (type.equals("2")) {
			path = Paths.get(IMAGE_TEMP, fileName);
		} else if (type.equals("3")) {
			path = Paths.get(IMAGE_FACE, fileName);
		} else if (type.equals("4")) {
			path = Paths.get(IMAGE_ARM, fileName);
		}

		if (!Files.exists(path)) {
			throw new IllegalStateException("图片不存在");
		}
		File file = path.toFile();
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			switch (extension) {
				case "jpg":
					response.setContentType("image/jpg");
					break;
				case "png":
					response.setContentType("image/png");
					break;
				case "gif":
					response.setContentType("image/gif");
					break;
				default:
					response.setContentType("image/jpeg");
					break;
			}
			//response.setHeader("content-type", "application/octet-stream");
			//response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fileInputStream.read(buffer)) > 0) {
				response.getOutputStream().write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/*@ApiOperation(value = "图片比对", notes = "")
	@GetMapping("/comparison")
	public R<Boolean> comparison(@RequestPart("image") MultipartFile image, Long userId, HttpServletRequest request) {
		String path = uploadImage(image, "1", request).getData().get("filePath");
		log.info("filePath: " + path);
		String fileName = path.substring(path.lastIndexOf("=") + 1);
		String result = "";
		BufferedReader in = null;

		// 设置Python脚本的路径和需要传递的参数
		String featureScriptPath = "/home/tb/FaceRecognitionDemo/get_feature.py";

		String similarityScriptPath = "/home/tb/FaceRecognitionDemo/get_similarity.py";
		String imagePath = IMAGE_TEMP + "images/" + fileName;
		log.info("Image path: " + imagePath);
		String jsonPath = IMAGE_TEMP + "json/" + fileName.substring(0, fileName.lastIndexOf(".")) + ".json";
		// 使用ProcessBuilder来构建并执行命令
		//String scriptName = pythonScriptPath + " --image_path " + imagePath + " --write_file_path "+ jsonPath;
		result = callPythonScript(featureScriptPath, imagePath, jsonPath);
		if (result.contains("否")) {
			return R.status(false);
		}
		UserFeature one = Db.lambdaQuery(UserFeature.class).eq(UserFeature::getUserId, userId).one();
		String oneJsonPath = one.getJsonPath();
		//String SimilarityScript = similarityScriptPath + " --base_feature_path " + oneJsonPath + " --write_file_path "+ jsonPath;
		log.info("----------------1111111111");
		return R.data(callPythonScript1(similarityScriptPath, oneJsonPath, jsonPath));
	}*/

	private String getUserId(String path, String extension, String name) {

		String result = "";

		List<UserFeature> list = Db.lambdaQuery(UserFeature.class).list();
		double max = 0.0;
		String userId = "";
		try {
			for (UserFeature userFeature : list) {
				String json = userFeature.getJsonPath();
				log.info("features: {}", path);
				double[] userFeatures = StrToDoubleArray.strToDoubleArray(json, ",");
				double[] results = StrToDoubleArray.strToDoubleArray(path, ",");
				double value = cosineSimilarity(userFeatures, results);
				log.info("相似度: {}", value);
				if (value > max) {
					max = value;
					userId = userFeature.getUserId() + "";
				}
				if (max >= 0.6) {
					return userId;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@ApiOperation(value = "手臂识别", notes = "")
	@GetMapping("/armRecognition")
	public R<String> armRecognition(String name) {
		Path face = Paths.get(IMAGE_FACE, name);
		Path arm = Paths.get(IMAGE_ARM, name);
		log.info("fileName: " + name);
		//if(true) return R.data(path);
		//String fileName = path.substring(path.lastIndexOf("=") + 1);

		String result = "";
		// 设置Python脚本的路径和需要传递的参数
		String scriptPath = "/home/tb/FaceRecognitionDemo/predict.py";
		int dotIndex = name.lastIndexOf('.');
		String extension = name.substring(dotIndex + 1).toLowerCase();
		log.info("Face path: " + face.toAbsolutePath());
		log.info("Arm path: " + arm.toAbsolutePath());
		result = callPythonScript2(scriptPath, name, extension);
		log.info("Json path: {}" + result);


		return R.data(getUserId(result, extension, name));
	}

	//手臂识别脚本
	private String callPythonScript2(String scriptPath, String fileName, String extension) {
		BufferedReader in = null;
		String s = "";
		String name = fileName;
		try {
			ProcessBuilder pb = new ProcessBuilder("python", scriptPath, "--image_name", name);
			Process process = pb.start();
			// 读取Python脚本的输出
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			log.info("当前执行脚本: callPythonScript2 参数: scriptPath: {},fileName: {}", scriptPath, name);
			String ret = null;
			log.info("-----------------------------------开始循环");
			String check = "";
			StringBuilder face = new StringBuilder();
			while ((ret = in.readLine()) != null) {
				log.info("--------------------------------读取内容:" + ret);
				log.info("--------------------------------分割线:" + "------------");
				if (ret.contains("检测结果")) {
					check = ret.substring(ret.indexOf("：") + 1).trim();
					log.info("读取内容：" + ret);
					log.info("----------  分割线  ----------");
					continue;
				}
				if (check.equals("True")) {
					if (ret.contains("特征值")) {
						ret = ret.substring(ret.lastIndexOf("["));
						log.info("特征值: ", ret);
						log.info("----------  分割线  ----------");
						face.append(ret);
					}
				}

			}
			s = face.toString();
			System.out.println(s);
			// 等待Python脚本执行完成，并获取其退出值
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
			} else {
				// 异常处理
				System.out.println("Something went wrong with the Python script execution.");
			}

			/*try (BufferedWriter writer = new BufferedWriter(new FileWriter(IMAGE_TEMP + "json/" + name + ".json"))) {
				// 写入字符串到文件
				writer.write(face.toString());
				System.out.println("String successfully written to file.");
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return s;
	}

	public String recognition(String userId, String fileName) {
		String result = "";
		BufferedReader in = null;
		String features = "";
		try {
			// 设置Python脚本的路径和需要传递的参数
			String pythonScriptPath = "/home/tb/FaceRecognitionDemo/get_feature.py";
			String imagePath = UPLOAD_DIR + fileName;
			log.info("Image path: " + imagePath);
			String jsonPath = "/home/tb/FaceRecognitionDemo/json/" + fileName.substring(0, fileName.lastIndexOf(".")) + ".json";
			// 使用ProcessBuilder来构建并执行命令
			ProcessBuilder pb = new ProcessBuilder("python3", pythonScriptPath, "--image_path", imagePath, "--write_file_path", jsonPath);
			Process process = pb.start();
			// 读取Python脚本的输出
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String ret = null;
			log.info("-----------------------------------");
			while ((ret = in.readLine()) != null) {
				log.info("--------------------------------:" + ret);
				log.info("--------------------------------:" + "------------");
				if (ret.contains("是否为活体")) {
					String check = ret.trim();
					result = check.substring(check.length() - 1);

					System.out.println(ret);
				}
				if (result.equals("否")) {
					return result;
				}
				if (ret.contains("特征向量")) {
					features = ret.substring(ret.lastIndexOf("["));
				}
			}

			// 等待Python脚本执行完成，并获取其退出值
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
			} else {
				// 异常处理
				System.out.println("Something went wrong with the Python script execution.");
			}
			log.info("result: " + result);
			//log.info("sb: " + sb);
			System.out.println("userId: " + userId + " path: " + jsonPath);
			UserFeature userFeature = Db.lambdaQuery(UserFeature.class).eq(UserFeature::getUserId, userId).one();
			if (userFeature == null) {
				userFeature = new UserFeature();
				userFeature.setUserId(Long.parseLong(userId));
				userFeature.setJsonPath(features);
				Db.save(userFeature);
			} else {
				userFeature.setJsonPath(features);
				Db.updateById(userFeature);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	//图片特征写入json文件脚本
	private String callPythonScript(String scriptName, String imagePath, String jsonPath) {
		String result = "";
		String s = "";
		BufferedReader in = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("python3", scriptName, "--image_path", imagePath, "--write_file_path", jsonPath);
			Process process = pb.start();
			// 读取Python脚本的输出
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String ret = null;
			log.info("执行脚本:callPythonScript 参数: scriptName:{}, imagePath:{},jsonPath:{}", scriptName, imagePath, jsonPath);
			while ((ret = in.readLine()) != null) {
				log.info("读取内容-----------------------:" + ret);
				log.info("-分割线-----------------------:" + "------------");
				if (ret.contains("是否为活体")) {
					String check = ret.trim();
					result = check.substring(check.length() - 1);

					System.out.println(ret);
				}
				if (result.equals("否")) {
					return result;
				}
				if (ret.contains("特征向量")) {
					s = ret.substring(ret.lastIndexOf("["));
				}
			}
			log.info("特征向量: {}", s);
			// 等待Python脚本执行完成，并获取其退出值
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
			} else {
				// 异常处理
				System.out.println("Something went wrong with the Python script execution.");
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return s;
	}

	//人脸对比识别脚本
	private double callPythonScript1(String scriptName, String jsonPath1, String jsonPath2) {
		String result = "";
		BufferedReader in = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("python3", scriptName, "--base_feature_path", jsonPath1, "--input_feature_path", jsonPath2);
			Process process = pb.start();
			// 读取Python脚本的输出
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String ret = null;
			log.info("-----------------------------------2222");
			while ((ret = in.readLine()) != null) {
				log.info("--------------------------------3333333:" + ret);
				log.info("--------------------------------4444444:" + "------------");
				if (ret.contains("两个向量的相似度")) {
					String check = ret.substring(ret.indexOf(":") + 1).trim();
					System.out.println("-------:------    " + check);
					return Double.valueOf(check);
				}
			}

			// 等待Python脚本执行完成，并获取其退出值
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
			} else {
				// 异常处理
				System.out.println("Something went wrong with the Python script execution.");
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return 0;
	}

	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
		if (vectorA.length != vectorB.length) {
			throw new IllegalArgumentException("Vectors must be of the same length");
		}

		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;

		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}

		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}


}



