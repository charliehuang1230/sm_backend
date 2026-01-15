# @MockBean vs @Mock - 為什麼 Spring Boot 需要 MockBean？

## 🤔 您的問題很好！

> "那怎麼還會有 MockBean 這種東西？"

這是一個很好的問題！@MockBean 確實是 Spring Boot 測試的殺手級功能，讓我解釋為什麼它比純 @Mock 更強大。

## 🔥 @MockBean 的革命性優勢

### 1. **Spring 容器整合** - 這是關鍵！
```java
// ❌ 純 @Mock - 需要手動管理一切
@Mock
private CustomDynamicDataSourceRegistry mockRegistry;
@Mock
private ProductMapper mockMapper;
// 手動創建，無法利用 Spring 功能
private CustomDynamicCommerceService service = new CustomDynamicCommerceService(mockRegistry, mockMapper);

// ✅ @MockBean - Spring 自動處理依賴注入
@MockBean
private CustomDynamicDataSourceRegistry mockRegistry;  // 自動注入到 Spring 容器

@Autowired
private CustomDynamicCommerceService realService;  // 真實的 Spring bean，但依賴已被 mock 替換
```

### 2. **部分 Mock 的威力** - 這是純 Mock 做不到的！
```java
@SpringBootTest
class PartialMockingExample {

    @MockBean
    private CustomDynamicDataSourceRegistry mockRegistry;  // Mock 外部依賴

    @Autowired
    private CustomDynamicCommerceService realService;  // 使用真實業務邏輯

    @Test
    void testBusinessLogicWithMockedDependencies() {
        // 🎯 測試真實的業務邏輯，但控制外部依賴
        // 這在純 @Mock 中需要大量手動配置才能實現
    }
}
```

### 3. **Spring 配置和特性完全生效**
```java
// ✅ 這些 Spring 功能在 @MockBean 中都正常工作：
@Value("${app.config}")  // 配置注入
@ConfigurationProperties  // 配置綁定
@Transactional  // 事務管理
@Cacheable  // 快取
@Async  // 異步處理
@EventListener  // 事件監聽
// ... 所有 Spring 功能都可用！

// ❌ 純 @Mock 測試中這些都無效，需要大量額外配置
```

## 📊 實際對比案例

### 測試場景：測試一個依賴外部資料庫的服務

#### **純 @Mock 方式** (複雜且限制多)
```java
@ExtendWith(MockitoExtension.class)
class PureMockTest {

    @Mock private CustomDynamicDataSourceRegistry mockRegistry;
    @Mock private ProductMapper mockMapper;
    @Mock private TransactionManager mockTxManager;
    @Mock private CacheManager mockCacheManager;
    // ... 需要 mock 所有依賴

    private CustomDynamicCommerceService service;

    @BeforeEach
    void setUp() {
        // 手動組裝所有依賴 - 繁瑣且容易出錯
        service = new CustomDynamicCommerceService(mockRegistry, mockMapper);
        // 無法測試 Spring 的初始化邏輯
        // 無法測試 @Value, @ConfigurationProperties 等配置
    }
}
```

#### **@MockBean 方式** (簡潔且功能完整)
```java
@SpringBootTest
class MockBeanTest {

    @MockBean
    private CustomDynamicDataSourceRegistry mockRegistry;  // 只 mock 需要的

    @Autowired  // Spring 自動注入，包含所有配置
    private CustomDynamicCommerceService service;

    @Test
    void test() {
        // ✅ 測試真實的 Spring bean
        // ✅ 包含完整的 Spring 配置
        // ✅ 只 mock 外部依賴
        // ✅ 業務邏輯完全真實
    }
}
```

## 🎯 何時使用什麼？

### **@Mock (純 Mockito)**
```java
✅ 使用場景：
- 純單元測試 (隔離所有依賴)
- 快速執行的測試 (不啟動 Spring 容器)
- 簡單邏輯測試
- CI/CD 中的快速驗證

⚡ 優勢：
- 執行速度快 (< 1秒)
- 完全隔離
- 不依賴外部配置
```

### **@MockBean (Spring Boot Test)**
```java
🔥 使用場景：
- 需要 Spring 功能的測試
- 部分整合測試 (mock 外部系統，測試內部邏輯)
- 複雜業務流程測試
- 配置相關的測試

💪 優勢：
- Spring 容器完整功能
- 自動依賴注入
- 配置文件生效
- 真實的初始化流程
```

## 🚀 實際專案中的最佳實踐

我們的專案現在有完美的測試分層：

```
📊 測試金字塔：
┌─────────────────────────────┐
│  手動整合測試 (HTTP API)     │  ← 端到端驗證
├─────────────────────────────┤
│  @MockBean 測試             │  ← 業務邏輯 + Spring 功能
├─────────────────────────────┤
│  @Mock 單元測試 (70個)       │  ← 快速邏輯驗證
└─────────────────────────────┘

🎯 結果：
- 快速反饋：@Mock 測試 < 10秒
- 功能驗證：@MockBean 測試涵蓋 Spring 整合
- 端到端保證：HTTP API 測試驗證真實功能
```

## ✅ 總結

**@MockBean 不是 @Mock 的替代品，而是補強！**

- **@Mock**: 純邏輯，快速執行，完全隔離
- **@MockBean**: Spring 整合，部分 mock，配置生效

兩者配合使用才能建立完整、高效、可維護的測試體系！

---

**現在您了解為什麼 Spring Boot 需要 @MockBean 了嗎？** 🎯

它填補了純單元測試和完整整合測試之間的空白，讓我們能夠：
- 測試真實的 Spring bean
- 控制外部依賴
- 保持測試速度
- 驗證 Spring 配置

這就是現代 Spring Boot 測試的精髓！