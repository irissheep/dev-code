package org.springblade.modules.beixiang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName bx_user_feature
 */
@TableName(value ="bx_user_feature")
@Data
public class UserFeature implements Serializable {
    /**
     * id
     */
    @TableId(type =IdType.ASSIGN_ID )
    private Long id;

    /**
     * 用户id
     */
	@TableField(value ="user_id")
    private Long userId;

    /**
     * 人脸特征
     */
	@TableField(value ="json_path")
    private String jsonPath;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
