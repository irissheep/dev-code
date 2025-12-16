package org.springblade.modules.beixiang.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.beixiang.dto.NumberCountDTO;
import org.springblade.modules.beixiang.entity.Bill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springblade.modules.beixiang.vo.BillVO;
import org.springblade.modules.beixiang.vo.PurchaseBehaviorVO;

import java.util.List;

/**
* @author haiyu
* @description 针对表【bx_bill】的数据库操作Mapper
* @createDate 2024-06-17 17:07:51
* @Entity org.springblade.modules.beixiang.entity.Bill
*/
public interface BillMapper extends BaseMapper<Bill> {
	IPage<BillVO> getPage(IPage page,Long userId,String keyword);

	List<PurchaseBehaviorVO> purchaseBehavior(@Param("dto") NumberCountDTO dto);

	Bill getBillIdByproductId(Long productId, Long userId);

}




