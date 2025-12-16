package org.springblade.modules.beixiang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.beixiang.dto.BillDTO;
import org.springblade.modules.beixiang.dto.NumberCountDTO;
import org.springblade.modules.beixiang.entity.Bill;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.beixiang.entity.Message;
import org.springblade.modules.beixiang.vo.BillVO;
import org.springblade.modules.beixiang.vo.PurchaseBehaviorVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author haiyu
* @description 针对表【bx_bill】的数据库操作Service
* @createDate 2024-06-17 17:07:51
*/
public interface BillService extends IService<Bill> {

	BillVO detail(Long id);

	IPage<BillVO> getPage(Query query, Long userId, String keyword);


	boolean editStatus(Message msg);

	boolean handleBill(BillDTO dto);

	List<PurchaseBehaviorVO> purchaseBehavior(NumberCountDTO dto);

	Map<String, String> saleCount();

	void handleData(String deviceId, Map<String, Double> weightMap, Date recentTime, String recentReportTime,Long userId);
}
