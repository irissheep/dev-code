-- -----------------------------------
-- 数据源表新增分库分表配置字段
-- -----------------------------------
ALTER TABLE "BLADE_DATASOURCE"
    ADD ("CATEGORY" NUMBER(11) DEFAULT 1)
    ADD ("SHARDING_CONFIG" CLOB );
COMMENT ON COLUMN "BLADE_DATASOURCE"."CATEGORY" IS '数据源类型';
COMMENT ON COLUMN "BLADE_DATASOURCE"."SHARDING_CONFIG" IS ' ';

-- -----------------------------------
-- 字典表表新增数据源类型
-- -----------------------------------
INSERT INTO "BLADE_DICT"(ID, PARENT_ID, CODE, DICT_KEY, DICT_VALUE, SORT, REMARK, IS_SEALED, IS_DELETED) VALUES ('1735215689272508418', '0', 'datasource_category', '-1', '数据源类型', '16', '', '0', '0');
INSERT INTO "BLADE_DICT"(ID, PARENT_ID, CODE, DICT_KEY, DICT_VALUE, SORT, REMARK, IS_SEALED, IS_DELETED) VALUES ('1735215870613241857', '1735215689272508418', 'datasource_category', '1', 'jdbc', '1', '', '0', '0');
INSERT INTO "BLADE_DICT"(ID, PARENT_ID, CODE, DICT_KEY, DICT_VALUE, SORT, REMARK, IS_SEALED, IS_DELETED) VALUES ('1735215901546233858', '1735215689272508418', 'datasource_category', '2', 'sharding', '2', '', '0', '0');

-- -----------------------------------
-- 新增 对象存储表新增资源转换地址字段
-- -----------------------------------
ALTER TABLE "BLADE_OSS"
    ADD ("TRANSFORM_ENDPOINT" VARCHAR2(32) );
COMMENT ON COLUMN "BLADEX"."BLADE_OSS"."TRANSFORM_ENDPOINT" IS '外网资源地址';
