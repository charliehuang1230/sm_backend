# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 專案概述

sm_backend 是一個基於 Spring Boot 的動態資料庫連線驗證與資料存取服務。主要目的是讓使用者透過 API 輸入資料庫帳號密碼，系統驗證成功後建立安全的資料庫連線，使用者可透過此連線存取指定資料庫的資料。

### 核心業務流程
1. **連線建立**: 使用者提供資料庫連線資訊（主機、埠、資料庫名、帳密）
2. **帳密驗證**: 系統驗證資料庫連線有效性
3. **連線管理**: 建立動態資料源並返回 connectionId
4. **資料存取**: 使用者透過 connectionId 查詢商品資料
5. **連線清理**: 自動或手動清理過期連線

支援多種資料庫（MySQL、PostgreSQL、Oracle、SQL Server）並提供商品查詢 API 範例。

## 核心架構

### 動態資料源系統
- **DynamicDataSourceRegistry**: 負責動態資料源的註冊、生命週期管理和清理
- **DynamicRoutingDataSource**: 實現動態路由到不同資料源
- **DynamicDataSourceContext**: 維護當前執行緒的資料源上下文
- 連線具有 TTL (預設 30 分鐘)，超時自動清理

### 商品實體模型
專案包含單一的商品實體作為查詢範例：
- Product (商品): sku, productName, categoryId, listPrice, costPrice, isActive, createdAt

### API 結構
- **連線驗證與管理**:
  - `/api/db/connect` - 驗證資料庫帳密並建立安全連線
  - `/api/db/close` - 主動關閉指定連線
- **商品查詢服務**: `/api/db/products` - 透過 connectionId 查詢商品資料
- **安全性**: 所有查詢都需要有效的 connectionId，確保資料存取安全性

## 常用開發指令

### 建置與執行
```bash
# 建置專案
./gradlew build

# 執行應用程式
./gradlew bootRun

# 執行測試
./gradlew test

# 執行單一測試類
./gradlew test --tests "com.demo.todolist.controller.DynamicDbControllerTest"
```

### 開發工具
- API 測試：使用 `api-test.http` 檔案進行商品查詢 API 測試
- 資料庫初始化：`src/main/resources/sql/test_ddl.sql` 包含商品表結構和測試資料

## 資料庫配置

### 預設資料源
- MySQL: `jdbc:mysql://localhost:3306/sm_db`
- 帳密: root/123456

### 支援的資料庫類型
- MYSQL: `com.mysql.cj.jdbc.Driver`
- POSTGRES: `org.postgresql.Driver`
- ORACLE: `oracle.jdbc.OracleDriver`
- MSSQL: `com.microsoft.sqlserver.jdbc.SQLServerDriver`

## 程式碼結構

```
src/main/java/com/demo/todolist/
├── config/          # 動態資料源配置
├── controller/      # REST API 控制器 (連線管理 + 商品查詢)
├── dto/            # 請求/回應資料傳輸物件
├── entity/         # 商品實體類別
├── mapper/         # MyBatis Mapper 介面
└── service/        # 業務邏輯服務層
```

## 重要實作細節

### 動態連線驗證流程
1. 客戶端提供資料庫連線參數（主機、埠、資料庫名、使用者名稱、密碼）
2. 系統嘗試建立實際資料庫連線進行帳密驗證
3. 驗證成功後建立 HikariCP 連線池並註冊到動態路由系統
4. 返回唯一的 `connectionId` 給客戶端
5. 客戶端使用 `connectionId` 進行後續資料查詢
6. 連線在 30 分鐘後自動過期，或可手動關閉

### MyBatis-Plus 配置
- 自動駝峰命名轉換：`map-underscore-to-camel-case: true`
- Mapper 掃描：`@MapperScan("com.demo.todolist.mapper")`

### 查詢模式
商品查詢 API 接受 DynamicProductQueryRequest 參數，支援：
- 精確匹配 (如 sku, categoryId)
- 模糊搜尋 (如 productName)
- 範圍查詢 (如 minListPrice, maxListPrice)
- 狀態篩選 (如 isActive)
- 分頁限制 (limit 參數)