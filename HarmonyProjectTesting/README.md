## HarmonyProjectTesting 项目总览

这是一个“后端 Java + 鸿蒙前端”的多项目仓库：
- 后端位于 `Bei-Xiang/`，基于 Spring Boot + BladeX + MyBatis-Plus，集成 Swagger/Knife4j、Druid、Flowable、LiteFlow、OSS、短信等能力；默认端口 9999。
- 前端位于 `SalesNewMaster/`，为 HarmonyOS 应用（ArkTS/ETS），通过 HTTP 调用后端接口。

本文从结构、工作原理、运行流程到常见问题，帮助你快速理解与上手。

### 顶层目录结构
- `Bei-Xiang/`: Java 后端服务
- `SalesNewMaster/`: HarmonyOS 前端应用
- `bladex.sql`: 后端平台相关初始化 SQL
- `README.md`: 本文档

### 后端服务（Bei-Xiang）
后端是一个标准的 Spring Boot 单体服务，按“分层 + 领域模块”的模式组织。

- 关键位置
  - 启动入口：`org.springblade.Application`（运行即可启动服务）
  - 配置文件：`src/main/resources/application*.yml`（端口、数据源、日志、Swagger、OSS、多租户等）
  - 端口：默认 `9999`（`application.yml -> server.port`）
  - 文档：`http://localhost:9999/doc.html`（Knife4j/Swagger）
  - 数据源监控：`http://localhost:9999/druid/`（默认账密见配置文件）

- 主要目录
  - `src/main/java/org/springblade/common`: 公共配置与基础能力（日志、拦截器、XSS、Swagger、WebSocket、报表等）
  - `src/main/java/org/springblade/modules`: 业务域模块（`system`/`resource`/`develop`/`desk`/`auth`/`beixiang` 等）
    - 每个模块通常包含 `controller` → `service` → `mapper`（MyBatis-Plus）以及 `entity`/`vo`/`dto`/`*.xml`
  - `src/main/java/org/springblade/flow`: 流程相关（LiteFlow/Flowable 的节点、示例与封装）
  - `src/main/resources/liteflow`: LiteFlow 的 `.el.xml` 流程定义
  - `src/main/resources/processes`: Flowable 的 BPMN 流程定义
  - `doc/script`: 运行脚本与 Docker/ELK 示例（`fatjar/service.cmd|sh`、`docker/elk/docker-compose.yml`）
  - `doc/sql`: 多数据库初始化或升级 SQL（MySQL、Oracle、PG、SQLServer、达梦、亚信等）

- 请求处理链路（简化）
  1) 客户端发起 HTTP 请求 → 2) 过滤器/拦截器（日志、XSS、安全、租户等） → 3) `controller`
  → 4) `service` 进行业务编排（可触发 LiteFlow/Flowable 流程） → 5) `mapper` 访问数据库
  → 6) 统一结果返回（异常和日志在公共层集中处理）。

- 能力开关（常见）
  - Swagger/Knife4j：`knife4j.enable: true`
  - 流程引擎：`liteflow` 与 `flowable` 目录和配置
  - 多租户/鉴权：`blade.secure`、`blade.tenant`
  - 报文加密：`blade.api.crypto.*`
  - 对象存储：`oss.*`（MinIO/S3/阿里云/华为/Tencent 等）

### 前端应用（SalesNewMaster）
HarmonyOS（ArkTS/ETS）项目，结构清晰，核心是通过封装的 HTTP 工具对接后端接口。

- 关键位置
  - 页面：`entry/src/main/ets/pages/*`（`login`/`customer`/`merchant`/`common` 等）
  - API 封装：`entry/src/main/ets/api/*.ets`（集中与后端交互）
  - HTTP 工具：`entry/src/main/ets/utils/HttpUtils.ets`，读取 `config.data.ets` 的后端基地址
  - 实时通信（可选）：`entry/src/main/ets/model/socket.ts`

- 前后端交互
  - 前端从 `config.data.ets` 取到后端地址 → 通过 `HttpUtils.ets` 发起 REST 请求 → 命中后端 `modules/*/controller`
  → 服务调用 → 数据返回并渲染。

### 运行与调试
后端与前端可独立运行，建议先启动后端，再运行前端。

1) 后端
   - 准备：JDK 11、Maven（或 IDE 内置构建）
   - 方式 A：IDE 运行 `org.springblade.Application.main`
   - 方式 B：命令行（需配置 Maven）：
     ```bash
     cd Bei-Xiang
     mvn -U clean package -DskipTests
     java -jar target/bei-xiang.jar
     ```
   - 验证：
     - API 文档 `http://localhost:9999/doc.html`
     - Druid 监控 `http://localhost:9999/druid/`

2) 前端（HarmonyOS）
   - 使用 DevEco Studio 或命令行（`hvigorw(.bat)`）按官方流程运行 `SalesNewMaster`。
   - 确认 `utils/config.data.ets` 指向后端地址（如 `http://localhost:9999`）。

### 部署与运维（可选）
- Fat-Jar：`doc/script/fatjar/service.cmd|sh` 快速启动/停止。
- Docker：使用根目录 `Bei-Xiang/Dockerfile` 构建镜像；`doc/script/docker/elk/` 为 ELK 参考编排。
- 配置分环境 `application-dev|test|prod.yml`，日志按环境在 `resources/log/`。

### 常见问题（FAQ）
- 无法识别 Maven：请安装并配置 `MAVEN_HOME` 与 `Path`，或在 IDE 内使用 Maven 面板构建。
- 依赖缺失导致注解找不到：检查 `pom.xml` 的 Spring Boot/MyBatis-Plus 相关 starter 是否正确引入，并刷新依赖。
- 页面无法访问接口：确认后端端口、跨域与 `config.data.ets` 的服务地址是否一致；查看后端日志与 `doc.html` 是否可打开。

### 你可以从哪里开始
- 想理解后端：从 `modules/system` 或自研的 `modules/beixiang` 入手，跟一条“Controller → Service → Mapper”的调用链。
- 想理清流程编排：查看 `resources/liteflow/*.el.xml` 与 `flow` 包中的节点实现，或 `processes/*.bpmn20.xml` 与 Flowable 相关类。
- 想验证接口：启动后打开 `doc.html`，在线调试所有 REST API。

---
如需进一步按“具体业务模块”输出更细的调用图与表结构关系，请指出模块名称，我会继续补充到本文档中。

### 模块深挖示例：`modules/beixiang`
该模块为定制业务域，包含订单、商品、设备、统计、账户、图片处理、IoT 等能力。采用标准的三层结构与 MyBatis-Plus 数据访问模式。

- 目录速览（局部）
  - `controller/`: `BillController`, `ProductController`, `DeviceController`, `ProductStatisticsController`, `AccountController`, `ImageController`, `IotController` 等
  - `service/`: `*Service` 接口（均继承 `IService<T>`）与其实现类（通常在 `service.impl/`）
  - `mapper/`: `*Mapper.java`（继承 `BaseMapper<T>`）与同名 `*Mapper.xml`（自定义 SQL）
  - `entity/`: 领域实体，如 `Bill`, `Product`, `Device`, `Account` 等
  - `vo`/`dto`: 视图对象与传输对象

- 典型调用链（以“订单分页查询”为例）
  1) 请求：`GET /bill/page`
  2) `BillController.page(...)` 接收参数、校验并调用 `BillService`
  3) `BillService` 组织业务规则（如状态过滤、权限/租户过滤）
  4) `BillMapper` 调用（MP 分页 + 可选 XML 自定义查询）查询数据库
  5) 结果封装为分页对象返回给前端

- 常见接口一览（节选，具体见 `controller` 上的 Mapping）
  - 订单：`/bill/page`, `/bill/detail`, `/bill/add`, `/bill/editStatus`, `/bill/handleBill`, `/bill/clear`
  - 商品：`/product/submit`, `/product/detail`, `/product/page`, `/product/listName`, `/product/replenishment`, `/product/replenishWarn`
  - 设备：`/device/submit`, `/device/page`, `/device/list`, `/device/deviceWarn`
  - 统计：`/statistics/inventory`, `/statistics/numberCount`, `/statistics/saleTrend`, `/statistics/purchaseBehavior`, `/statistics/saleCount`, `/statistics/SaleRank`
  - 账户：`/account/add`, `/account/log`, `/account/getBalance`
  - 图片：`/images/upload`, `/images/recognizeImage`, `/images/download`, `/images/comparison`, `/images/armRecognition`
  - IoT：`/iot_report`, `/iot_control`, `/iot_devices`, `/iot_deviceStatus`, `/login`

- 数据访问
  - 基础增删改查：`BaseMapper<T>` + `IService<T>`（MyBatis-Plus）
  - 复杂查询：对应的 `*Mapper.xml` 中定义 `<select>/<insert>/<update>/<delete>` 语句
  - 映射路径：`resources` 中通过 `mybatis-plus.mapper-locations` 已扫描 `org/springblade/**/mapper/*Mapper.xml`

- 业务扩展点
  - 可在 `service` 层接入 LiteFlow/Flowable 编排复杂流程（如订单状态机、补货流程、告警处理等）
  - 利用 `common/cache` 做热点数据缓存；结合 `blade.secure` 做接口鉴权与签名
  - IoT 与图片处理接口可接入对象存储（`oss.*` 配置）与 AI 能力

- Debug 建议
  - 从 `controller` 入手，定位某个接口方法，跟进 `service` 与 `mapper`
  - 若 SQL 来自 XML，则在同名 `*Mapper.xml` 中搜索对应 `id`
  - 使用 `doc.html` 快速调试请求；必要时打开 SQL 日志与 Druid 监控

### 前后端映射速查
- **登录/鉴权**
  - 后端：`modules/auth`、`modules/beixiang` 中与登录相关的 `Controller`
  - 前端：`entry/src/main/ets/pages/login/*`，API 封装见 `entry/src/main/ets/api/HomeAPI.ets`
- **账户/钱包/流水**
  - 后端：`modules/beixiang` → `AccountController`, `BillController`
  - 前端：`entry/src/main/ets/pages/customer/*`，API 见 `entry/src/main/ets/api/customerApi.ets`
- **商品/设备/统计（商户端）**
  - 后端：`modules/beixiang` → `ProductController`, `DeviceController`, `ProductStatisticsController`
  - 前端：`entry/src/main/ets/pages/merchant/*`，组件在 `pages/merchant/components/*`，API 见 `entry/src/main/ets/api/merchantApi.ets`
- **通用组件与状态**
  - 前端：`entry/src/main/ets/pages/common/components/*`
- **HTTP 与配置**
  - 前端：`entry/src/main/ets/utils/HttpUtils.ets`（请求封装），`entry/src/main/ets/utils/config.data.ets`（后端基地址）

### 快速开始（TL;DR）
1. 启动后端（JDK 11 + Maven）：
   ```bash
   cd Bei-Xiang
   mvn -U clean package -DskipTests
   java -jar target/bei-xiang.jar
   ```
   验证：`http://localhost:9999/doc.html` 与 `http://localhost:9999/druid/` 可访问。
2. 运行前端（DevEco Studio）：
   - 打开 `SalesNewMaster`
   - 确认 `entry/src/main/ets/utils/config.data.ets` 指向 `http://localhost:9999`
   - 选择设备（模拟器/真机）运行

### 常用排错清单
- **接口 404/跨域**：确认后端端口、网段可达、跨域策略；`config.data.ets` 地址一致。
- **依赖/启动失败**：刷新 Maven，检查 JDK 版本与 `pom.xml` 依赖是否完整。
- **SQL 未执行/表不存在**：导入 `bladex.sql` 与模块所需 SQL；检查数据源配置。
- **流程相关异常**：检查 `resources/liteflow/*.el.xml` 与 `resources/processes/*.bpmn20.xml` 是否存在/加载；开关是否启用。
- **前端网络失败**：在 `HttpUtils.ets` 打印实际请求地址与响应；核验 `oh_modules/@ohos/axios` 是否正常。

---
如需我将具体模块（如订单/商品/设备等）的“表结构关系图 + 前后端调用链”进一步扩展进本文档，请告知模块名称。