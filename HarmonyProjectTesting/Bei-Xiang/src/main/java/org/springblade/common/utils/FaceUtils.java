package org.springblade.common.utils;



import java.util.List;

/**
 * add hmy
 * 人脸比对工具
 * 2024年6月12日16:34:22
 */
public class FaceUtils {

	// 计算平方值
	private static double euclideanNorm(double []vec) {
		double sum = 0.0;
		for (int i =0; i < vec.length; i++){
			sum += vec[i] * vec[i];
		}
		return Math.sqrt(sum);
	}
	// 计算平方值
	private static double euclideanNorm(List<Float> vec) {
		double sum = 0.0f;
		for (int i =0; i < vec.size(); i++){
			sum += vec.get(i) * vec.get(i);
		}
		return Math.sqrt(sum);
	}
	// 计算两个向量的点积
	private static double dotProduct(double[] vec1,double[] vec2) {

		double result = 0.0;
		for (int i = 0; i < vec1.length; ++i) {
			result += vec1[i] * vec2[i];
		}

		return result;
	}
	// 计算两个向量的点积
	private static double dotProduct(List<Float> vec1, List<Float> vec2) {

		double result = 0.0f;
		for (int i = 0; i < vec1.size(); ++i) {
			result += (vec1.get(i) * vec2.get(i));
		}

		return result;
	}
	//计算余弦相似度 返回具体分值

}
