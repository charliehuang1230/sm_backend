# SQL 日誌與監控配置指南

## 📋 概述

本專案已配置完整的 SQL 日誌與性能監控系統，包括：

1. **MyBatis-Plus SQL 日誌** - 記錄所有 SQL 語句和參數
2. **HikariCP 連接池日誌** - 監控連接池狀態
3. **P6Spy SQL 監控** - 詳細的 SQL 性能分析
4. **慢查詢檢測** - 自動檢測超過 2 秒的查詢
5. **連接洩漏檢測** - 檢測未正確關閉的連接

## 🔧 配置說明

### 1. MyBatis-Plus SQL 日誌

**配置位置**: `src/main/resources/application.yaml`

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
```

**日誌級別**:
```yaml
logging:
  level:
    com.demo.todolist.mapper: DEBUG
```

**日誌輸出範例**:
```
2026-01-15 14:30:45.123 [http-nio-8080-exec-1] DEBUG com.demo.todolist.mapper.ProductMapper - ==>  Preparing: SELECT product_id, sku, product_name, category_id, list_price, is_active FROM product WHERE sku = ? AND is_active = ?
2026-01-15 14:30:45.125 [http-nio-8080-exec-1] DEBUG com.demo.todolist.mapper.ProductMapper - ==> Parameters: SKU001(String), true(Boolean)
2026-01-15 14:30:45.156 [http-nio-8080-exec-1] DEBUG com.demo.todolist.mapper.ProductMapper - <==      Total: 1
```

### 2. HikariCP 連接池日誌

**日誌級別**:
```yaml
logging:
  level:
    com.zaxxer.hikari: DEBUG
```

**日誌輸出範例**:
```
2026-01-15 14:30:45 [main] DEBUG com.zaxxer.hikari.HikariConfig - dynamic-abc123 - configuration:
2026-01-15 14:30:45 [main] DEBUG com.zaxxer.hikari.HikariConfig - maximumPoolSize................................1
2026-01-15 14:30:45 [main] DEBUG com.zaxxer.hikari.HikariConfig - minimumIdle...................................0
2026-01-15 14:30:45 [main] DEBUG com.zaxxer.hikari.pool.HikariPool - dynamic-abc123 - Pool stats (total=1, active=1, idle=0, waiting=0)
```

### 3. P6Spy SQL 監控

**配置位置**: `src/main/resources/spy.properties`

**主要功能**:
- 記錄所有 SQL 語句（包括執行時間）
- 顯示實際參數值（而不是 ?）
- 多行格式化輸出，易於閱讀
- 慢查詢標記（超過 2 秒）

**日誌輸出範例**:
```
2026-01-15 14:30:45.123 | took 156ms | statement | connection 1 | url jdbc:postgresql://localhost:5432/testdb
SELECT
    product_id,
    sku,
    product_name,
    category_id,
    list_price,
    is_active
FROM product
WHERE sku = 'SKU001'
  AND is_active = true
;
```

### 4. 慢查詢檢測

**配置**:
```properties
# spy.properties
outagedetection=true
outagedetectioninterval=2000  # 2000 毫秒 = 2 秒
```

當 SQL 執行時間超過 2 秒時，會在日誌中特別標記：

```
2026-01-15 14:30:50.123 | OUTAGE (took 3456ms) | statement | connection 1
SELECT * FROM large_table WHERE complex_condition = 'value'
;
```

### 5. 連接洩漏檢測

**配置**:
```java
// CustomDynamicDataSourceRegistry.java
config.setLeakDetectionThreshold(60000);  // 60 秒
```

當連接超過 60 秒未關閉時，會記錄警告：

```
2026-01-15 14:31:45 WARN  com.zaxxer.hikari.pool.ProxyLeakTask - Connection leak detection triggered for dynamic-abc123, stack trace follows
java.lang.Exception: Apparent connection leak detected
    at com.demo.todolist.service.CustomDynamicCommerceService.queryProducts(CustomDynamicCommerceService.java:122)
    ...
```

## 📊 日誌級別配置

### 開發環境（當前配置）

```yaml
logging:
  level:
    root: INFO
    com.demo.todolist: DEBUG              # 應用程式詳細日誌
    com.demo.todolist.mapper: DEBUG       # SQL 詳細日誌
    com.zaxxer.hikari: DEBUG              # 連接池詳細日誌
    org.springframework.jdbc: DEBUG       # Spring JDBC 日誌
```

### 生產環境建議

```yaml
logging:
  level:
    root: INFO
    com.demo.todolist: INFO               # 減少應用日誌
    com.demo.todolist.mapper: INFO        # 只記錄重要 SQL
    com.zaxxer.hikari: WARN               # 只記錄警告
    org.springframework.jdbc: WARN        # 只記錄警告
```

## 🧪 測試日誌輸出

### 1. 啟動應用

```bash
./gradlew bootRun
```

### 2. 測試 SQL 日誌

連接資料庫並執行查詢：

```bash
# 連接資料庫
curl -X POST http://localhost:8080/api/db/connect \
  -H "Content-Type: application/json" \
  -d '{
    "dbType": "POSTGRES",
    "host": "localhost",
    "port": 5432,
    "database": "testdb",
    "username": "user",
    "password": "password"
  }'

# 返回: {"connectionId": "abc-123-def", ...}

# 執行查詢
curl -X POST http://localhost:8080/api/postgres/db/products \
  -H "Content-Type: application/json" \
  -d '{
    "connectionId": "abc-123-def",
    "sku": "SKU001",
    "limit": 10
  }'
```

### 3. 查看日誌輸出

應用啟動後，在控制台會看到：

```
# MyBatis-Plus SQL 日誌
2026-01-15 14:30:45 DEBUG com.demo.todolist.mapper.ProductMapper - ==>  Preparing: SELECT ...
2026-01-15 14:30:45 DEBUG com.demo.todolist.mapper.ProductMapper - ==> Parameters: SKU001(String)

# P6Spy 格式化 SQL
2026-01-15 14:30:45 | took 25ms | statement | connection 1
SELECT product_id, sku, product_name
FROM product
WHERE sku = 'SKU001'
;

# HikariCP 連接池狀態
2026-01-15 14:30:45 DEBUG com.zaxxer.hikari.pool.HikariPool - Pool stats (total=1, active=1, idle=0)
```

## 📁 日誌文件輸出（可選）

如果需要將日誌輸出到文件，取消註解以下配置：

```yaml
logging:
  file:
    name: logs/todolist.log
    max-size: 10MB
    max-history: 30
```

日誌文件將保存在 `logs/` 目錄：
- `todolist.log` - 當前日誌
- `todolist.log.1` - 昨天的日誌
- `todolist.log.2` - 前天的日誌
- ...（保留 30 天）

## 🔍 日誌分析技巧

### 1. 查找慢查詢

```bash
grep "took.*ms" logs/todolist.log | grep -E "(took [5-9][0-9]{3}|took [1-9][0-9]{4})"
```

### 2. 統計 SQL 執行次數

```bash
grep "==>  Preparing:" logs/todolist.log | sort | uniq -c | sort -rn | head -10
```

### 3. 查找連接洩漏

```bash
grep "Connection leak" logs/todolist.log
```

### 4. 監控連接池狀態

```bash
grep "Pool stats" logs/todolist.log | tail -20
```

## ⚙️ 性能調優建議

### 1. 關閉不必要的日誌

生產環境建議只保留以下日誌：
- ERROR 級別的系統錯誤
- WARN 級別的連接池警告
- 慢查詢日誌（超過 2 秒）

### 2. 使用非同步日誌

對於高流量系統，建議使用 Logback 非同步 Appender：

```xml
<!-- logback-spring.xml -->
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="FILE" />
    <queueSize>512</queueSize>
</appender>
```

### 3. 調整 P6Spy 配置

如果 P6Spy 影響性能，可以：
- 只記錄慢查詢：設置 `executionThreshold=2000`
- 排除特定 SQL：使用 `exclude` 配置
- 關閉 P6Spy：移除 `spy.properties` 或註解相關配置

## 🚨 故障排除

### 問題 1: 看不到 SQL 日誌

**解決方案**:
1. 檢查日誌級別是否為 DEBUG
2. 確認 `mybatis-plus.configuration.log-impl` 已配置
3. 確認 Mapper 接口的包路徑正確

### 問題 2: P6Spy 日誌格式不正確

**解決方案**:
1. 確認 `spy.properties` 在 `src/main/resources/` 目錄
2. 檢查 `logMessageFormat` 配置
3. 確認 P6Spy 依賴已正確引入

### 問題 3: 連接池日誌過多

**解決方案**:
```yaml
logging:
  level:
    com.zaxxer.hikari: WARN  # 只記錄警告
```

## 📚 相關文檔

- [MyBatis-Plus 官方文檔](https://baomidou.com/)
- [HikariCP GitHub](https://github.com/brettwooldridge/HikariCP)
- [P6Spy 官方文檔](https://p6spy.readthedocs.io/)
- [Logback 官方文檔](https://logback.qos.ch/)

## 🎯 下一步

日誌配置完成後，建議：

1. **監控慢查詢** - 定期檢查超過 2 秒的查詢
2. **優化索引** - 根據日誌分析添加必要的索引
3. **調整連接池** - 根據實際負載調整 HikariCP 配置
4. **設置告警** - 配置日誌監控告警（如 ELK、Prometheus）
