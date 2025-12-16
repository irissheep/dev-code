package org.springblade.modules.beixiang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.support.Query;
import org.springblade.modules.beixiang.dto.DeviceAddOrUpdateDTO;
import org.springblade.modules.beixiang.dto.DeviceDTO;
import org.springblade.modules.beixiang.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springblade.modules.beixiang.vo.DeviceTreeVO;
import org.springblade.modules.beixiang.vo.DeviceVO;

import java.util.List;

/**
* @author haiyu
* @description 针对表【bx_device】的数据库操作Service
* @createDate 2024-06-17 17:07:51
*/
public interface DeviceService extends IService<Device> {

    Long submit(DeviceAddOrUpdateDTO dto);

	IPage<DeviceVO> getDeviceList(Query query, DeviceDTO dto);

	List<DeviceVO> deviceWarn();

	List<DeviceTreeVO> tree();
}
