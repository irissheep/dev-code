package org.springblade.modules.beixiang.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.modules.beixiang.dto.ProductDTO;
import org.springblade.modules.beixiang.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springblade.modules.beixiang.vo.InventoryVO;
import org.springblade.modules.beixiang.vo.ProductVO;
import org.springblade.modules.beixiang.vo.SaleRankVO;
import org.springblade.modules.beixiang.vo.SaleTrendVO;

import java.util.List;

/**
* @author haiyu
* @description 针对表【bx_product】的数据库操作Mapper
* @createDate 2024-06-17 17:07:51
* @Entity org.springblade.modules.beixiang.entity.Product
*/
public interface ProductMapper extends BaseMapper<Product> {

	IPage<ProductVO> getProductByPage(IPage page, ProductDTO dto);

	List<InventoryVO> getInventory();

	List<SaleRankVO> SaleRank(@Param("condition") String condition);

}




