package org.springblade.modules.beixiang.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.modules.beixiang.dto.BillDTO;
import org.springblade.modules.beixiang.dto.ProductDTO;
import org.springblade.modules.beixiang.entity.*;
import org.springblade.modules.beixiang.service.*;

import org.springblade.modules.beixiang.vo.BillVO;
import org.springblade.modules.beixiang.vo.ProductVO;
import org.springblade.modules.beixiang.wrapper.BillWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("bill")
@Api(tags = "订单接口")
public class BillController {
	@Resource
	private BillService billService;

	@Resource
	private MessageService messageService;

	@Resource
	private ProductRelBillService productRelBillService;

	@Resource
	private ProductService productService;

	@Resource
	private RelUserService relUserService;


	@ApiOperation(value = "消费,账单记录分页", notes = "")
	@GetMapping("page")
	public R<IPage<BillVO>> page(String keyword, Long userId, Query query) {
		IPage<BillVO> page = billService.getPage(query, userId, keyword);
		return R.data(page);
	}

	@ApiOperation(value = "消费,账单记录详情", notes = "")
	@GetMapping("detail")
	public R<BillVO> detail(Long id) {
		BillVO billVO = billService.detail(id);
		return R.data(billVO);
	}

	@ApiOperation(value = "新增消费,账单记录", notes = "")
	@PostMapping("add")
	public R add(Bill bill) {
		return R.data(billService.save(bill));
	}


	@ApiOperation(value = "账单消息状态修改", notes = "")
	@PostMapping("editStatus")
	public R editStatus(@RequestBody Message msg) {
		return R.data(billService.editStatus(msg));
	}

	@ApiOperation(value = "一键已读", notes = "")
	@PostMapping("editAllStatus")
	public R editStatus(String userId, String status) {
		return R.status(Db.update(Message.class)
			.set("status", status)
			.eq("user_id", userId)
			.update());
	}

	@ApiOperation(value = "消息列表", notes = "")
	@GetMapping("messageList")
	public R<IPage<Message>> messageList(Query query, Long userId) {
		//根据用户id查询消息
		QueryWrapper<Message> wrapper = new QueryWrapper<Message>().eq("1", 1).apply("find_in_set(" + userId + ",user_id)").orderByDesc("create_time");
		//IPage<Message> page = Db.page(Condition.getPage(query), Message.class);
		IPage<Message> page = messageService.page(Condition.getPage(query), wrapper);
		Map<String, List<Message>> map = new HashMap<>();
		List<Message> messages = page.getRecords();
		Set<String> ntpTimes = new HashSet<>();
		List<Long> billIds = new ArrayList<>();
		//存ntpTime 为null
		List<Message> messageList2 = new ArrayList<>();

		if (messages == null || messages.size() == 0) {
			return R.data(page);
		}
		//遍历
		messages.forEach(message -> {

			if(StringUtil.isNotBlank(message.getNtpTime())){
				List<Message> messageList = map.get(message.getNtpTime());
				//将 上传时间 和 对应的消息列表 放入 map
				if (messageList != null && messageList.size() != 0) {
					messageList.add(message);

					map.put(message.getNtpTime(), messageList);
				} else {
					messageList = new ArrayList<>();
					messageList.add(message);
					map.put(message.getNtpTime(), messageList);
				}
				ntpTimes.add(message.getNtpTime());
			}else{
				messageList2.add(message);
			}
			//存储订单id
			if (message.getBillId()!=null) {
				billIds.add(message.getBillId());
			}
		});
		List<Message> messageList = new ArrayList<>();
		if(!CollectionUtils.isEmpty(billIds)){
			//根据订单id 查询 商品对应的订单关系
			List<ProductRelBill> productRelBills = productRelBillService.list(new LambdaQueryWrapper<ProductRelBill>().in(ProductRelBill::getBillId, billIds));

			Map<Long, Long> billRelProductMap = productRelBills.stream().collect(Collectors.toMap(ProductRelBill::getBillId, ProductRelBill::getProductId));
			//查询 商品
			List<Product> products = productService.list(new QueryWrapper<Product>().eq("is_deleted", 0));

			//获取商品id和商品图片地址
			Map<Long, String> productMap = products.stream().collect(Collectors.toMap(Product::getId, Product::getPictureUrl));
			Map<Long, String> productUrlMap = new HashMap<>();
			//查询人脸识别的记录
			List<RelUser> relUsers = relUserService.list(new QueryWrapper<RelUser>().in("ntp_time", ntpTimes));


			if(!CollectionUtils.isEmpty(relUsers)){
				for (Map.Entry<Long, Long> entry : billRelProductMap.entrySet()) {
					Long productId = entry.getValue();
					Long billId = entry.getKey();
					//存储订单和商品图片地址
					productUrlMap.put(billId, productMap.get(productId));
				}
				relUsers.forEach(relUser -> {
					List<Message> messageList1 = map.get(relUser.getNtpTime());
					if (messageList1 != null || messageList1.size() != 0) {
						for (Message message : messageList1) {
							//设置消息的商品地址和人脸图片地址
							message.setCaptureUrl(relUser.getPictureUrl());
							message.setProductUrl(productUrlMap.get(message.getBillId()));
							messageList.add(message);
						}
					}

				});
			}else{
				//人脸识别失败 订单异常情况
				for (Map.Entry<String, List<Message>> entry : map.entrySet()) {
					List<Message> value = entry.getValue();
					messageList.addAll(value);
				}

			}

			for (Message message : messageList2) {
				//message.setCaptureUrl();
				message.setProductUrl(productUrlMap.get(message.getBillId()));
			}
		}

		messageList.addAll(messageList2);
		List<Message> result = messageList.stream().sorted(Comparator.comparing(Message::getCreateTime).reversed()).collect(Collectors.toList());
		page.setRecords(result);
		return R.data(page);
	}


	@ApiOperation(value = "处置账单", notes = "")
	@PostMapping("handleBill")
	public R handleBill(@RequestBody BillDTO dto) {
		return R.status(billService.handleBill(dto));
	}

	@ApiOperation(value = "清空", notes = "")
	@GetMapping("clear")
	public R clear(Long userId) {
		return R.status(messageService.remove(new LambdaQueryWrapper<Message>().eq(Message::getUserId,userId)));
	}




}
