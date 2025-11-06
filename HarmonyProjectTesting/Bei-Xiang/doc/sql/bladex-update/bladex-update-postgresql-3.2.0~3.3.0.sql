-- -----------------------------------
-- 数据源表新增分库分表配置字段
-- -----------------------------------
ALTER TABLE "blade_datasource"
    ADD COLUMN "category" int4 DEFAULT 1,
    ADD COLUMN "sharding_config" text;

COMMENT ON COLUMN "blade_datasource"."category" IS '数据源类型';

COMMENT ON COLUMN "blade_datasource"."sharding_config" IS '分库分表配置';

-- -----------------------------------
-- 新增 对象存储字典类型
-- -----------------------------------
INSERT INTO "blade_dict" VALUES (1735215689272508418, 0, 'datasource_category', '-1', '数据源类型', 16, '', 0, 0);
INSERT INTO "blade_dict" VALUES (1735215870613241857, 1735215689272508418, 'datasource_category', '1', 'jdbc', 1, '', 0, 0);
INSERT INTO "blade_dict" VALUES (1735215901546233858, 1735215689272508418, 'datasource_category', '2', 'sharding', 2, '', 0, 0);

-- -----------------------------------
-- 对象存储表新增资源转换地址字段
-- -----------------------------------
ALTER TABLE "blade_oss"
    ADD COLUMN "transform_endpoint" varchar(255);

COMMENT ON COLUMN "blade_oss"."transform_endpoint" IS '外网资源地址';
