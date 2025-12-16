package org.springblade.common.constant;

public interface ServiceConstant {
	Long MERCHANT_ID = 1806202507931783169l;

	String BASE_URL = "http://117.78.16.25:9999";

	String COMMAND_ON = "{\"service_id\": \"weight\", \"command_name\": \"replenishment\", \"deviceId\": \"kixzybvctfbj-1800739805722574925_smart_warehouse\", \"paras\": {\n\t\"switch\": \"on\"\n} }";;
	;
	String COMMAND_OFF = "{\"service_id\": \"weight\", \"command_name\": \"replenishment\", \"deviceId\": \"kixzybvctfbj-1800739805722574925_smart_warehouse\", \"paras\": {\n\t\"switch\": \"off\"\n} }";;
	;
	//1 开始补货 2完成补货
	String STATUS_ON = "1";
	String STATUS_OFF = "2";
}
