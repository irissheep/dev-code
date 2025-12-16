package org.springblade.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.tool.jackson.JsonUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

	public static final OkHttpClient SSL_CLIENT;
	private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);
	private static final MediaType MT_JSON = MediaType.parse("application/json; charset=utf-8");
	public static final OkHttpClient CLIENT;

	static {
		CLIENT = new OkHttpClient().newBuilder()
			.connectTimeout(30, TimeUnit.MINUTES)
			.readTimeout(30, TimeUnit.MINUTES)
			.writeTimeout(30, TimeUnit.MINUTES)
			.build();
		SSL_CLIENT = buildOKHttpClient()
			.connectTimeout(3000, TimeUnit.SECONDS)
			.readTimeout(3000, TimeUnit.SECONDS)
			.writeTimeout(3000, TimeUnit.SECONDS)
			.build();
	}

	private OkHttpUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static Response get(String url, Map<String, String> param) throws IOException {
		return get(CLIENT, url, param);
	}

	public static Response get(OkHttpClient client, String url, Map<String, String> param) throws IOException {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		Request request = new Request.Builder().get().url(urlBuilder.build()).build();
		return client.newCall(request).execute();
	}

	public static Response get(String url, Map<String, String> param, Map<String, String> headers) throws IOException {
		return get(CLIENT, url, param, headers);
	}

	public static Response get(OkHttpClient client, String url, Map<String, String> param, Map<String, String> headers) throws IOException {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		Request request = new Request.Builder()
			.get()
			.url(urlBuilder.build())
			.headers(setHeaders(headers))
			.build();
		return client.newCall(request).execute();
	}

	public static Response post(String url, Map<String, String> param) throws IOException {
		return post(CLIENT, url, param);
	}

	public static Response post(OkHttpClient client, String url, Map<String, String> param) throws IOException {
		FormBody.Builder builder = new FormBody.Builder();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				builder.add(entry.getKey(), entry.getValue());
			}
		}
		Request request = new Request.Builder().post(builder.build()).url(url).build();
		return client.newCall(request).execute();
	}

	public static Response post(String url, Map<String, String> param, Map<String, String> headers) throws IOException {
		return post(CLIENT, url, param, headers);
	}

	public static Response post(OkHttpClient client, String url, Map<String, String> param, Map<String, String> headers) throws IOException {
		FormBody.Builder formBuilder = new FormBody.Builder();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				formBuilder.add(entry.getKey(), entry.getValue());
			}
		}
		Request request = new Request.Builder().
			post(formBuilder.build()).
			url(url)
			.headers(setHeaders(headers))
			.build();

		return client.newCall(request).execute();
	}

	public static JsonNode getCoordinateTrans(Map<String, Object> map, String urlPath) throws IOException {
		//curl
		Response response = postJson(urlPath, JsonUtil.toJson(map));
		return assembleResponse(response);
	}

	public static Response post2(OkHttpClient client, String url, Map<String, Object> param, Map<String, String> headers) throws IOException {
		// 添加请求类型
		MultipartBody.Builder builder = new MultipartBody.Builder();
		builder.setType(MediaType.parse("multipart/form-data"));
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, Object> entry : param.entrySet()) {
				if (entry.getValue() instanceof File) {
					File file = (File) entry.getValue();
					builder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(file, null));
				} else {
					builder.addFormDataPart(entry.getKey(), entry.getValue().toString());
				}
			}
		}
		Request request = new Request.Builder().
			post(builder.build()).
			url(url)
			.headers(setHeaders(headers))
			.build();

		return client.newCall(request).execute();
	}

	private static Headers setHeaders(Map<String, String> headersMap) {
		Headers headers = null;
		final Headers.Builder builder = new Headers.Builder();
		if (null != headersMap && !headersMap.isEmpty()) {
			for (Map.Entry<String, String> entry : headersMap.entrySet()) {
				builder.add(entry.getKey(), entry.getValue());
			}
		}
		headers = builder.build();
		return headers;
	}

	public static Response postJson(String url, String json) throws IOException {
		return postJson(CLIENT, url, json);
	}

	public static Response postJson(OkHttpClient client, String url, String json) throws IOException {
		logger.info("postJson,RequestBody: [{}]", json);
		RequestBody requestBody = RequestBody.create(MT_JSON, json);
		Request request = new Request.Builder().url(url).post(requestBody).build();
		return client.newCall(request).execute();
	}

	public static Response postJson(String url, String json, Map<String, String> headers) throws IOException {
		return postJson(CLIENT, url, json, headers);
	}

	public static Response postJson(OkHttpClient client, String url, String json, Map<String, String> headers) throws IOException {
		// logger.info("postJson,RequestBody: [{}]", json);
		RequestBody requestBody = RequestBody.create(MT_JSON, json);
		Request request = new Request.Builder()
			.url(url)
			.post(requestBody)
			.headers(setHeaders(headers))
			.build();
		return client.newCall(request).execute();
	}

	public static Response postJson(OkHttpClient client, String url, Map<String, String> param, String json, Map<String, String> headers) throws IOException {
		// logger.info("postJson,RequestBody: [{}]", json);
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		RequestBody requestBody = RequestBody.create(MT_JSON, json);
		Request request = new Request.Builder()
			.url(urlBuilder.build())
			.post(requestBody)
			.headers(setHeaders(headers))
			.build();
		return client.newCall(request).execute();
	}

	public static Response putFile(OkHttpClient client, MediaType mediaType, String url, String localPath) throws IOException {
		logger.info("putFile,RequestBody: [{}]", localPath);
		File file = new File(localPath);
		RequestBody requestBody = RequestBody.create(mediaType, file);
		Request request = new Request.Builder().url(url).put(requestBody).build();
		Response response = client.newCall(request).execute();
		boolean successful = response.isSuccessful();
		String data = response.body().string();
		if (successful) {
			logger.info("putFile,上传成功: [{}],url: [{}],data: [{}]", localPath, url, data);
		} else {
			logger.error("putFile,上传失败: [{}],url: [{}],data: [{}]", localPath, url, data);
		}

		return response;
	}

	public static Response deleteFile(OkHttpClient client, String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
		boolean successful = response.isSuccessful();
		if (successful) {
			logger.error("putFile,删除成功: url: [{}]", url);
		} else {
			logger.error("putFile,删除失败: url: [{}]", url);
		}

		return response;
	}


	public static Response putJson(OkHttpClient client, String url, String json) throws IOException {
		// logger.error("putJson,RequestBody: [{}]", json);
		RequestBody requestBody = RequestBody.create(MT_JSON, json);
		Request request = new Request.Builder().url(url).put(requestBody).build();
		return client.newCall(request).execute();
	}

	public static Response putJson(OkHttpClient client, String url, String json, Map<String, String> headers) throws IOException {
		// logger.error("putJson,RequestBody: [{}]", json);
		RequestBody requestBody = RequestBody.create(MT_JSON, json);
		Request request = new Request
			.Builder()
			.url(url)
			.put(requestBody)
			.headers(setHeaders(headers))
			.build();
		return client.newCall(request).execute();
	}


	public static Response delete(OkHttpClient client, String url) throws IOException {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		Request request = new Request.Builder().delete().url(urlBuilder.build()).build();
		return client.newCall(request).execute();
	}

	public static Response delete(OkHttpClient client, String url, Map<String, String> headers) throws IOException {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		Request request = new Request.Builder()
			.delete()
			.url(urlBuilder.build())
			.headers(setHeaders(headers))
			.build();
		return client.newCall(request).execute();
	}

	public static Response upload(File file, String url, String folder) throws IOException {
		return upload(CLIENT, file, url, folder);
	}

	public static Response upload(OkHttpClient client, File file, String url, String folder) throws IOException {
		RequestBody requestBody = new MultipartBody.Builder()
			.setType(MultipartBody.FORM)
			.addFormDataPart("folder", folder)
			.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
			.build();
		Request request = new Request.Builder().url(url).post(requestBody)
			.build();
		return client.newCall(request).execute();
	}

	public static File download(String url, File file, Map<String, String> param, Map<String, String> headers) throws IOException {
		return download(CLIENT, url, file, param, headers);
	}

	public static File download(OkHttpClient client, String url, File file, Map<String, String> param, Map<String, String> headers) throws IOException {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {
				urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		Request request = new Request.Builder().get()
			.url(urlBuilder.build())
			.headers(setHeaders(headers))
			.build();

		Call call = client.newCall(request);
		Response response = call.execute();
		if (response.isSuccessful()) {
			String header = response.header("Content-Disposition");
			if (StringUtils.isNotEmpty(header)) {
				int inxex = header.lastIndexOf("=");
				String name = header.substring(inxex + 1);
				name = URLDecoder.decode(name, "UTF-8");
				file = new File(file, name);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				try (InputStream in = response.body().byteStream()) {
					try (OutputStream out = new FileOutputStream(file)) {
						byte[] buff = new byte[2048];
						int len;
						while ((len = in.read(buff)) != -1) {
							out.write(buff, 0, len);
						}
					}
				}
				return file;
			}
		} else {
			throw new RuntimeException("download file fail");
		}
		return null;
	}

	private static OkHttpClient.Builder buildOKHttpClient() {
		try {
			TrustManager[] trustAllCerts = buildTrustManagers();
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
			builder.hostnameVerifier((hostname, session) -> true);
			return builder;
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
			return new OkHttpClient.Builder();
		}
	}

	private static TrustManager[] buildTrustManagers() {
		return new TrustManager[]{
			new X509TrustManager() {
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[]{};
				}
			}
		};
	}

	/**
	 * https postJson请求
	 */
	public static Response sslPost(String url, String json) throws IOException {
		Request request = new Request.Builder()
			.url(url)
			.headers(Headers.of(new HashMap<>()))
			.post(RequestBody.create(MT_JSON, json))
			.build();
		return SSL_CLIENT.newCall(request).execute();
	}


	/**
	 * 封装http请求返回结果
	 *
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static JsonNode assembleResponse(Response response) throws IOException {
		String json = response.body().string();
		return JsonUtil.readTree(json);
	}
}
