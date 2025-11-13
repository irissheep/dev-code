## HarmonyProjectTesting 项目总览

这是一个"后端 Java + 鸿蒙前端"的多项目仓库：
- **后端**位于 `Bei-Xiang/`，基于 Spring Boot + BladeX + MyBatis-Plus，集成 Swagger/Knife4j、Druid、Flowable、LiteFlow、OSS、短信等能力；默认端口 9999。
- **前端**位于 `SalesNewMaster/`，为 HarmonyOS 应用（ArkTS/ETS），通过 HTTP 调用后端接口。

本文从结构、工作原理、运行流程到常见问题，帮助你快速理解与上手。

---

## 一、项目结构与情况

### 顶层目录结构
- `Bei-Xiang/`: Java 后端服务
- `SalesNewMaster/`: HarmonyOS 前端应用
- `bladex.sql`: 后端平台相关初始化 SQL
- `README.md`: 本文档

### 后端服务（Bei-Xiang）

后端是一个标准的 Spring Boot 单体服务，按"分层 + 领域模块"的模式组织。

#### 关键位置
- **启动入口**：`org.springblade.Application`（运行即可启动服务）
- **配置文件**：`src/main/resources/application*.yml`
  - `application.yml`: 基础配置（端口、数据源驱动等）
  - `application-dev.yml`: **开发环境配置（当前使用远程数据库）**
  - `application-test.yml`: 测试环境配置
  - `application-prod.yml`: 生产环境配置
- **端口**：默认 `9999`（`application.yml -> server.port`）
- **API文档**：`http://localhost:9999/doc.html`（Knife4j/Swagger）
- **数据源监控**：`http://localhost:9999/druid/`（默认账密见配置文件）

#### 主要目录
- `src/main/java/org/springblade/common`: 公共配置与基础能力（日志、拦截器、XSS、Swagger、WebSocket、报表等）
- `src/main/java/org/springblade/modules`: 业务域模块（`system`/`resource`/`develop`/`desk`/`auth`/`beixiang` 等）
  - 每个模块通常包含 `controller` → `service` → `mapper`（MyBatis-Plus）以及 `entity`/`vo`/`dto`/`*.xml`
- `src/main/java/org/springblade/flow`: 流程相关（LiteFlow/Flowable 的节点、示例与封装）
- `src/main/resources/liteflow`: LiteFlow 的 `.el.xml` 流程定义
- `src/main/resources/processes`: Flowable 的 BPMN 流程定义

#### 请求处理链路（简化）
1) 客户端发起 HTTP 请求 → 2) 过滤器/拦截器（日志、XSS、安全、租户等） → 3) `controller`
→ 4) `service` 进行业务编排（可触发 LiteFlow/Flowable 流程） → 5) `mapper` 访问数据库
→ 6) 统一结果返回（异常和日志在公共层集中处理）。

### 前端应用（SalesNewMaster）

HarmonyOS（ArkTS/ETS）项目，结构清晰，核心是通过封装的 HTTP 工具对接后端接口。

#### 关键位置
- **页面**：`entry/src/main/ets/pages/*`（`login`/`customer`/`merchant`/`common` 等）
- **API 封装**：`entry/src/main/ets/api/*.ets`（集中与后端交互）
- **HTTP 工具**：`entry/src/main/ets/utils/HttpUtils.ets`
- **服务器地址配置**：`entry/src/main/ets/entryability/EntryAbility.ts`（应用启动时配置BASE_URL）
- **实时通信（可选）**：`entry/src/main/ets/model/socket.ts`

#### 前后端交互
前端从 `EntryAbility.ts` 配置后端地址 → 通过 `HttpUtils.ets` 发起 REST 请求 → 命中后端 `modules/*/controller`
→ 服务调用 → 数据返回并渲染。

---

## 二、远程后端和本地后端切换

### 当前配置状态

**前端配置**（`SalesNewMaster/entry/src/main/ets/entryability/EntryAbility.ts`）：
- **默认配置**：Android模拟器使用 `http://10.0.2.2:9999`
- **可切换配置**：支持本地浏览器、真机调试、远程服务器

**后端配置**：
- **默认端口**：9999
- **环境配置**：通过 `--spring.profiles.active=dev` 指定使用 `application-dev.yml`

### 前端切换后端地址

#### 配置位置
编辑 `SalesNewMaster/entry/src/main/ets/entryability/EntryAbility.ts`，找到以下代码：

```typescript
if (!AppStorage.Get('BASE_URL')) {
  // 当前配置：Android模拟器（10.0.2.2是Android模拟器访问宿主机的特殊IP）
  AppStorage.SetOrCreate('BASE_URL', 'http://10.0.2.2:9999')
  
  // 如果需要使用其他配置，请注释掉上面这行，取消注释下面对应的配置：
  // 本地浏览器调试：
  // AppStorage.SetOrCreate('BASE_URL', 'http://localhost:9999')
  // 真机调试（替换为你的电脑IP）：
  // AppStorage.SetOrCreate('BASE_URL', 'http://192.168.1.100:9999')
  // 远程服务器：
  // AppStorage.SetOrCreate('BASE_URL', 'http://42.193.243.96:9999')
}
```

#### 不同场景的配置

| 场景 | BASE_URL配置 | 说明 |
|------|-------------|------|
| **Android模拟器** | `http://10.0.2.2:9999` | 当前默认配置，10.0.2.2是模拟器访问宿主机的特殊IP |
| **本地浏览器调试** | `http://localhost:9999` | 直接在浏览器中调试 |
| **真机调试** | `http://192.168.1.100:9999` | 需要替换为你的电脑实际IP地址（通过`ipconfig`获取） |
| **远程服务器** | `http://42.193.243.96:9999` | 连接远程部署的后端服务 |

#### 切换步骤

1. **打开配置文件**
   - `SalesNewMaster/entry/src/main/ets/entryability/EntryAbility.ts`

2. **根据你的场景修改**
   - 注释掉当前配置
   - 取消注释对应的配置行
   - 如果是真机调试，替换为你的电脑IP地址

3. **重新运行应用**
   - 重新编译并运行前端应用
   - 查看控制台日志确认配置生效：
     ```
     已设置默认后端地址（Android模拟器）: http://10.0.2.2:9999
     ```

### 后端启动配置

#### 使用IDE启动（推荐）

1. **打开运行配置**
   - 找到 `Application.java`（`src/main/java/org/springblade/Application.java`）
   - 右键 → `Run` → `Edit Configurations...`

2. **配置运行参数**
   - 在 **"Program arguments"** 字段中添加：
     ```
     --spring.profiles.active=dev
     ```
   - ⚠️ **注意**：不要放在"有效配置文件"字段中，会导致日志配置错误

3. **配置缩短命令行**（解决命令行过长错误）
   - 找到 **"缩短命令行(L)"** (Shorten command line) 下拉菜单
   - 选择：**`@argfile (Java 9+)`**

4. **保存并运行**
   - 点击 `Apply` → `OK`
   - 运行应用

#### 使用Maven命令启动

```bash
cd Bei-Xiang
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 验证后端启动

启动成功后，应该看到：
```
The following profiles are active: dev
...
Undertow started on port(s) 9999 (http)
...
Started Application in X.XXX seconds
```

测试后端是否可访问：
- 浏览器访问：`http://localhost:9999/doc.html`
- 如果能看到Swagger API文档页面，说明后端启动成功 ✅

---

## 三、本地数据库配置与注意事项

### 当前数据库配置（dev）

**配置文件**：`Bei-Xiang/src/main/resources/application-dev.yml`

**本地数据库配置**（当前已启用）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bladex?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true&lowerCaseTableNames=1
    username: root
    password: root
```

**Redis配置**（仍指向远程，如需本地部署请自行调整）：
```yaml
spring:
  redis:
    host: 42.193.243.96
    port: 6379
    password: tb@730*#$
```

### 使用本地数据库的步骤

1. **安装并启动 MySQL**
   - 推荐 MySQL 8.0+
   - 确保监听 3306 端口
2. **初始化数据库**
   ```bash
   mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS bladex CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
   mysql -uroot -proot bladex < bladex.sql
   ```
3. **启动后端（dev 环境）**
   - 添加参数 `--spring.profiles.active=dev`
   - 查看日志确认 `jdbc:mysql://localhost:3306/bladex`
4. **可选：本地部署 Redis**
   - 将 `spring.redis.host` 改为 `127.0.0.1`
   - 在本地启动 Redis 或根据需要关闭相关功能

### 本地数据库的优缺点

**✅ 优点**
- 完全离线开发，不依赖外部网络
- 数据可随意清空、修改，降低误操作风险
- 性能稳定，不受网络波动影响

**⚠️ 注意事项**
- 需要提前导入 `bladex.sql`
- 本地 MySQL 会占用一定系统资源
- 与团队协作时需自行同步数据

### 扩展：切换回远程数据库

若暂时想使用远程托管的数据库，可将 `application-dev.yml` 调整为：

```yaml
spring:
  datasource:
    url: jdbc:mysql://42.193.243.96:13306/bladex?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&allowPublicKeyRetrieval=true&lowerCaseTableNames=1
    username: proj4user
    password: fWQ#WcxmpX
```

切换后请确保网络能访问 `42.193.243.96:13306`，并同样在启动时指定 `--spring.profiles.active=dev`。

---

## 四、运行与调试

### 后端启动

1. **准备环境**
   - JDK 11+
   - Maven（或IDE内置构建）

2. **启动方式**
   - **方式A（IDE推荐）**：运行 `org.springblade.Application.main`，配置运行参数 `--spring.profiles.active=dev`
   - **方式B（命令行）**：
     ```bash
     cd Bei-Xiang
     mvn spring-boot:run -Dspring-boot.run.profiles=dev
     ```

3. **验证启动**
   - 查看控制台日志，确认看到 "Started Application"
   - 访问 `http://localhost:9999/doc.html` 查看API文档
   - 访问 `http://localhost:9999/druid/` 查看数据源监控

### 前端启动

1. **使用DevEco Studio**
   - 打开 `SalesNewMaster` 项目
   - 确认 `EntryAbility.ts` 中的BASE_URL配置正确
   - 选择设备（模拟器/真机）运行

2. **验证连接**
   - 查看应用控制台日志，应该看到：
     ```
     已设置默认后端地址（Android模拟器）: http://10.0.2.2:9999
     [HttpUtils] 后端服务器: http://10.0.2.2:9999
     ```
   - 尝试登录，确认可以正常连接后端

---

## 五、常见问题（FAQ）

### 后端相关问题

**Q: 后端启动失败，提示数据库连接失败？**
- 检查网络是否能访问远程数据库服务器
- 确认启动时使用了 `--spring.profiles.active=dev` 参数
- 查看完整错误日志，确认具体错误信息

**Q: 启动时提示"命令行过长"错误？**
- 在IDE运行配置中，找到"缩短命令行(L)"选项
- 选择 `@argfile (Java 9+)`
- 保存并重新运行

**Q: 如何确认使用的是远程数据库？**
- 查看启动日志中的数据库连接信息，应该看到：
  ```
  jdbc:mysql://42.193.243.96:13306/bladex
  ```

### 前端相关问题

**Q: 前端无法连接后端（"Couldn't connect to server"）？**
- 确认后端服务已启动（查看后端控制台）
- 检查前端BASE_URL配置是否正确
- 根据设备类型使用正确的地址：
  - Android模拟器：`http://10.0.2.2:9999`
  - 真机调试：使用电脑IP地址
  - 本地浏览器：`http://localhost:9999`

**Q: 如何查看前端实际连接的服务器地址？**
- 查看应用控制台日志，每次请求都会输出：
  ```
  [HttpUtils] 后端服务器: http://...
  [HttpUtils] 完整请求地址: http://.../...
  ```

**Q: 真机调试无法连接localhost？**
- 使用电脑的局域网IP地址，例如 `http://192.168.1.100:9999`
- 获取IP地址：Windows运行 `ipconfig`，找到 "IPv4 地址"
- 确保手机和电脑在同一WiFi网络

### 配置相关问题

**Q: 修改了本地后端代码没有效果？**
- 确认前端BASE_URL指向本地后端，而不是远程服务器
- 确认后端服务已重新启动
- 查看前端日志确认实际连接的服务器地址

**Q: 如何切换不同的后端服务器？**
- 修改 `EntryAbility.ts` 中的BASE_URL配置
- 重新运行前端应用
- 查看日志确认配置生效

---

## 六、快速开始（TL;DR）

### 完整启动流程

1. **启动后端服务**
   ```bash
   cd Bei-Xiang
   # 使用IDE：运行Application.java，添加参数 --spring.profiles.active=dev
   # 或使用命令行：
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   - 验证：访问 `http://localhost:9999/doc.html` 确认后端启动成功

2. **配置前端连接**
   - 打开 `SalesNewMaster/entry/src/main/ets/entryability/EntryAbility.ts`
   - 根据设备类型配置BASE_URL：
     - Android模拟器：`http://10.0.2.2:9999`（当前默认）
     - 真机调试：`http://你的电脑IP:9999`
     - 本地浏览器：`http://localhost:9999`

3. **运行前端应用**
   - 使用DevEco Studio运行 `SalesNewMaster`
   - 查看日志确认连接地址正确
   - 测试登录功能

---

## 七、模块深挖示例：`modules/beixiang`

该模块为定制业务域，包含订单、商品、设备、统计、账户、图片处理、IoT 等能力。采用标准的三层结构与 MyBatis-Plus 数据访问模式。

### 目录速览
- `controller/`: `BillController`, `ProductController`, `DeviceController`, `ProductStatisticsController`, `AccountController`, `ImageController`, `IotController` 等
- `service/`: `*Service` 接口（均继承 `IService<T>`）与其实现类（通常在 `service.impl/`）
- `mapper/`: `*Mapper.java`（继承 `BaseMapper<T>`）与同名 `*Mapper.xml`（自定义 SQL）
- `entity/`: 领域实体，如 `Bill`, `Product`, `Device`, `Account` 等
- `vo`/`dto`: 视图对象与传输对象

### 典型调用链（以"订单分页查询"为例）
1) 请求：`GET /bill/page`
2) `BillController.page(...)` 接收参数、校验并调用 `BillService`
3) `BillService` 组织业务规则（如状态过滤、权限/租户过滤）
4) `BillMapper` 调用（MP 分页 + 可选 XML 自定义查询）查询数据库
5) 结果封装为分页对象返回给前端

### 常见接口一览
- **订单**：`/bill/page`, `/bill/detail`, `/bill/add`, `/bill/editStatus`, `/bill/handleBill`, `/bill/clear`
- **商品**：`/product/submit`, `/product/detail`, `/product/page`, `/product/listName`, `/product/replenishment`, `/product/replenishWarn`
- **设备**：`/device/submit`, `/device/page`, `/device/list`, `/device/deviceWarn`
- **统计**：`/statistics/inventory`, `/statistics/numberCount`, `/statistics/saleTrend`, `/statistics/purchaseBehavior`, `/statistics/saleCount`, `/statistics/SaleRank`
- **账户**：`/account/add`, `/account/log`, `/account/getBalance`
- **图片**：`/images/upload`, `/images/recognizeImage`, `/images/download`, `/images/comparison`, `/images/armRecognition`
- **IoT**：`/iot_report`, `/iot_control`, `/iot_devices`, `/iot_deviceStatus`, `/login`

---

## 八、前后端映射速查

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
  - 前端：`entry/src/main/ets/utils/HttpUtils.ets`（请求封装），`entry/src/main/ets/entryability/EntryAbility.ts`（后端基地址配置）

---

如需进一步按"具体业务模块"输出更细的调用图与表结构关系，请指出模块名称，我会继续补充到本文档中。
