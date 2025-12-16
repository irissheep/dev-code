-- -----------------------------------
-- 数据源表新增分库分表配置字段
-- -----------------------------------
ALTER TABLE `blade_datasource`
    ADD COLUMN `category` int(2) NULL DEFAULT 1 COMMENT '数据源类型' AFTER `id`,
    ADD COLUMN `sharding_config` longtext NULL COMMENT '分库分表配置' AFTER `password`;

-- -----------------------------------
-- 字典表表新增数据源类型
-- -----------------------------------
INSERT INTO `blade_dict`(`id`, `parent_id`, `code`, `dict_key`, `dict_value`, `sort`, `remark`, `is_sealed`, `is_deleted`) VALUES (1735215689272508418, 0, 'datasource_category', '-1', '数据源类型', 16, '', 0, 0);
INSERT INTO `blade_dict`(`id`, `parent_id`, `code`, `dict_key`, `dict_value`, `sort`, `remark`, `is_sealed`, `is_deleted`) VALUES (1735215870613241857, 1735215689272508418, 'datasource_category', '1', 'jdbc', 1, '', 0, 0);
INSERT INTO `blade_dict`(`id`, `parent_id`, `code`, `dict_key`, `dict_value`, `sort`, `remark`, `is_sealed`, `is_deleted`) VALUES (1735215901546233858, 1735215689272508418, 'datasource_category', '2', 'sharding', 2, '', 0, 0);

-- -----------------------------------
-- 对象存储表新增资源转换地址字段
-- -----------------------------------
ALTER TABLE `blade_oss`
ADD COLUMN `transform_endpoint` varchar(255) NULL COMMENT '外网资源地址' AFTER `endpoint`;
