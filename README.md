# 🚦 Distributed Rate Limiter

A **Java Spring Boot** service implementing a **Token Bucket Rate Limiter** with:

- ✅ **Redis** backend (Lua script) for distributed consistency  
- ✅ **Micrometer + Prometheus** for metrics  
- ✅ **Grafana** dashboards for observability  
- ✅ **Docker Compose** setup for one-command startup  

---

## ✨ Features
- Token Bucket algorithm (capacity + refill/second)
- Distributed rate limiting with Redis
- In-memory fallback (for local dev/testing)
- Per-client buckets via `X-Api-Key` header (fallback → client IP)
- Returns `429 Too Many Requests` when limited
- Metrics exposed at `/actuator/prometheus`
- Out-of-the-box Prometheus + Grafana setup

---

## 🚀 Getting Started

### 1. Clone & build
```bash
git clone https://github.com/your-username/spring-boot-rate-limiter.git
cd spring-boot-rate-limiter
mvn clean package
```

### 2. Run with Docker Compose
```bash
docker compose up --build
```
This will start:
 - app → Spring Boot service (port 8080)
 - redis → Redis 7 (port 6379)
 - prometheus → Prometheus (port 9090)
 - grafana → Grafana (port 3000, user: admin / pass: admin)

## 🛠️ Usage
### 1. Test requests
```bash
# Allowed requests (capacity=10, refill=5/sec)
for i in {1..10}; do curl -s -o /dev/null -w "%{http_code}\n" \
  -H "X-Api-Key: user-123" http://localhost:8080/api/hello; done

# Exceed limit → expect 429 responses
for i in {1..6}; do curl -s -o /dev/null -w "%{http_code}\n" \
  -H "X-Api-Key: user-123" http://localhost:8080/api/hello; done
```

## 📊 Observability
### Prometheus

URL → http://localhost:9090

Example queries:
```bash
ratelimiter_requests_total
sum(rate(ratelimiter_requests_total{outcome="allowed"}[1m]))
sum(rate(ratelimiter_requests_total{outcome="blocked"}[1m]))
```

### Grafana
URL → http://localhost:3000 (admin/admin)

Data source already configured → http://prometheus:9090





