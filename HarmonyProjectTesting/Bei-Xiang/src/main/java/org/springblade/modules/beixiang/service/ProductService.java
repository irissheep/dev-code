package org.springblade.modules.beixiang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.beixiang.dto.ProductAddOrUpdateDTO;
import org.springblade.modules.beixiang.dto.ProductDTO;
import org.springblade.modules.beixiang.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.beixiang.vo.InventoryVO;
import org.springblade.modules.beixiang.vo.ProductVO;
import org.springblade.modules.beixiang.vo.SaleRankVO;

import java.util.List;

/**
* @author haiyu
* @description 针对表【bx_product】的数据库操作Service
* @createDate 2024-06-17 17:07:51
*/
public interface ProductService extends IService<Product> {
	/**
	 * @Author yangwenqiang
	 * @Description // 新增或修改商品
	 * @Date 17:36 2024/6/17
	 * @Param [dto]
	 * @return java.lang.String
	 **/
    Long submit(ProductAddOrUpdateDTO dto);

	ProductVO detail(String id);

	IPage<ProductVO> getProductList(Query query, ProductDTO dto);

	List<InventoryVO> inventory();

	boolean replenishment(String status);

	List<SaleRankVO> SaleRank(String condition);

	List<ProductVO> replenishWarn();

	List<String> listName();
}
