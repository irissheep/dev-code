package org.springblade.modules.beixiang.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.modules.beixiang.entity.Bill;
import org.springblade.modules.beixiang.vo.BillVO;

import java.util.Objects;

public class BillWrapper extends BaseEntityWrapper<Bill, BillVO> {
	public static BillWrapper build() {
		return new BillWrapper();
	}

	@Override
	public BillVO entityVO(Bill bill) {
		BillVO billVO = Objects.requireNonNull(BeanUtil.copy(bill, BillVO.class));
		return billVO;
	}
}
