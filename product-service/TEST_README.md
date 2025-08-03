# Тестирование Product Service

Этот документ описывает тестовую стратегию и инструкции по запуску тестов для Product Service.

## Структура тестов

### 1. Unit тесты
- **ProductServiceTest** - тестирование бизнес-логики сервиса
- **ProductControllerTest** - тестирование контроллеров с MockMvc
- **CategoryControllerTest** - тестирование контроллера категорий
- **ProductMapperTest** - тестирование маппинга между DTO и моделями
- **GlobalExceptionHandlerTest** - тестирование обработки исключений
- **ProductRequestValidationTest** - тестирование валидации DTO

### 2. Интеграционные тесты
- **ProductControllerIntegrationTest** - полное тестирование API с TestRestTemplate
- **ProductRepositoryTest** - тестирование репозитория с реальной БД

### 3. Конфигурационные тесты
- **ProductServiceApplicationTests** - тестирование загрузки контекста

## Запуск тестов

### Запуск всех тестов
```bash
mvn test
```

### Запуск конкретного класса тестов
```bash
mvn test -Dtest=ProductServiceTest
```

### Запуск тестов с подробным выводом
```bash
mvn test -Dspring.profiles.active=test
```

### Запуск тестов с покрытием кода
```bash
mvn test jacoco:report
```

## Конфигурация тестов

### Профиль тестирования
Тесты используют профиль `test`, который настраивается в `application-test.yml`:
- Используется H2 in-memory база данных
- Отключен Eureka Client
- Отключен Swagger UI
- Минимальное логирование

### Зависимости для тестирования
- **spring-boot-starter-test** - основные тестовые зависимости
- **h2** - in-memory база данных
- **assertj** - fluent assertions
- **mockito** - мокирование

## Покрытие тестов

### Основные сценарии тестирования

#### Product Service
- ✅ Создание продукта
- ✅ Получение продукта по ID
- ✅ Обновление продукта
- ✅ Удаление продукта
- ✅ Поиск продуктов по имени
- ✅ Фильтрация по категории
- ✅ Фильтрация по ценовому диапазону
- ✅ Обновление остатков
- ✅ Обработка исключений

#### Product Controller
- ✅ HTTP статус коды
- ✅ Валидация входных данных
- ✅ Обработка ошибок
- ✅ Пагинация
- ✅ Поиск и фильтрация

#### Validation
- ✅ Валидация обязательных полей
- ✅ Валидация длины строк
- ✅ Валидация числовых значений
- ✅ Валидация отрицательных значений

#### Exception Handling
- ✅ ProductNotFoundException (404)
- ✅ AlreadyExistsException (409)
- ✅ ValidationException (400)
- ✅ GeneralException (500)

## Рекомендации по написанию тестов

### 1. Именование тестов
Используйте паттерн: `methodName_ShouldReturnExpectedResult_WhenCondition`

### 2. Структура тестов
```java
@Test
void methodName_ShouldReturnExpectedResult_WhenCondition() {
    // Given - подготовка данных
    // When - выполнение действия
    // Then - проверка результата
}
```

### 3. Использование AssertJ
```java
assertThat(result).isNotNull();
assertThat(result.getName()).isEqualTo("Expected Name");
assertThat(resultList).hasSize(2);
```

### 4. Мокирование
```java
when(mockService.method(any())).thenReturn(expectedResult);
verify(mockService).method(any());
```

## Отладка тестов

### Включение SQL логирования
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### Включение детального логирования
```yaml
logging:
  level:
    org.com.productservice: DEBUG
    org.springframework.test: DEBUG
```

## CI/CD интеграция

### GitHub Actions
Тесты автоматически запускаются при каждом push и pull request.

### Локальная проверка
```bash
# Проверка стиля кода
mvn checkstyle:check

# Запуск тестов
mvn test

# Проверка покрытия
mvn jacoco:report
```

## Известные проблемы

1. **H2 Database** - некоторые специфичные SQL функции PostgreSQL могут не работать
2. **Eureka Client** - отключен в тестах для изоляции
3. **Cache** - отключен в тестах для предсказуемости

## Добавление новых тестов

1. Создайте тестовый класс в соответствующем пакете
2. Используйте правильные аннотации (@Test, @SpringBootTest, @DataJpaTest)
3. Следуйте паттерну именования
4. Добавьте тест в этот README

## Полезные команды

```bash
# Очистка и пересборка
mvn clean test

# Запуск только unit тестов
mvn test -Dtest="*Test" -DfailIfNoTests=false

# Запуск только интеграционных тестов
mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false

# Запуск тестов с параллельным выполнением
mvn test -Dparallel=methods -DthreadCount=4
``` 