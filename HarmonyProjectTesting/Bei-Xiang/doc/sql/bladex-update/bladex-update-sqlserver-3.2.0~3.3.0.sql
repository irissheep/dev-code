-- -----------------------------------
-- 数据源表新增分库分表配置字段
-- -----------------------------------
ALTER TABLE [dbo].[blade_datasource] ADD [category] int NOT NULL DEFAULT 1
GO

ALTER TABLE [dbo].[blade_datasource] ADD [sharding_config] nvarchar(max)
GO

EXEC sp_addextendedproperty
'MS_Description', N'数据源类型',
'SCHEMA', N'dbo',
'TABLE', N'blade_datasource',
'COLUMN', N'category'
GO

EXEC sp_addextendedproperty
'MS_Description', N'分库分表配置',
'SCHEMA', N'dbo',
'TABLE', N'blade_datasource',
'COLUMN', N'sharding_config';

-- -----------------------------------
-- 新增 对象存储字典类型
-- -----------------------------------
INSERT INTO [dbo].[blade_dict] VALUES (N'1735215689272508418', N'0', N'datasource_category', N'-1', N'数据源类型', N'16', N'', N'0', N'0');
INSERT INTO [dbo].[blade_dict] VALUES (N'1735215870613241857', N'1735215689272508418', N'datasource_category', N'1', N'jdbc', N'1', N'', N'0', N'0');
INSERT INTO [dbo].[blade_dict] VALUES (N'1735215901546233858', N'1735215689272508418', N'datasource_category', N'2', N'sharding', N'2', N'', N'0', N'0');

-- -----------------------------------
-- 新增 对象存储表新增资源转换地址字段
-- -----------------------------------
ALTER TABLE [dbo].[blade_oss] ADD [transform_endpoint] varchar(255)
GO

EXEC sp_addextendedproperty
'MS_Description', N'外网资源地址',
'SCHEMA', N'dbo',
'TABLE', N'blade_oss',
'COLUMN', N'transform_endpoint';
