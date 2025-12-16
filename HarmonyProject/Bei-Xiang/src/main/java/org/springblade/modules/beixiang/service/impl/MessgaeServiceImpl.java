package org.springblade.modules.beixiang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springblade.modules.beixiang.entity.Message;
import org.springblade.modules.beixiang.mapper.MessageMapper;
import org.springblade.modules.beixiang.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessgaeServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
}
