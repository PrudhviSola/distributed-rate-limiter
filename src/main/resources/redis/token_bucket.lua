-- KEYS[1]  -> bucket key
-- ARGV[1]  -> capacity (int)
-- ARGV[2]  -> refillPerSecond (float)
-- ARGV[3]  -> nowMs

local capacity = tonumber(ARGV[1])
local refillPerSecond = tonumber(ARGV[2])
local nowMs = tonumber(ARGV[3])

local data = redis.call('HMGET', KEYS[1], 'tokens', 'last_refill')
local tokens = tonumber(data[1])
local lastRefill = tonumber(data[2])

if tokens == nil or lastRefill == nil then
  tokens = capacity
  lastRefill = nowMs
end

local deltaMs = nowMs - lastRefill
if deltaMs < 0 then deltaMs = 0 end
local refill = (deltaMs / 1000.0) * refillPerSecond
tokens = math.min(capacity, tokens + refill)

local allowed = 0
if tokens >= 1.0 then
  tokens = tokens - 1.0
  allowed = 1
end

redis.call('HMSET', KEYS[1], 'tokens', tokens, 'last_refill', nowMs)
redis.call('EXPIRE', KEYS[1], 3600) -- 1h TTL

return allowed
