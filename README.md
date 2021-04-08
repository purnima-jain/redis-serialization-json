# redis-serialization-json
Usage of Redis as database with HashKey as uniqueId and Value serialized as JSON

Sample application for Redis usage as database where:
- Key is "CUSTOMER_STORE" + customerId
- HashKey is an attribute of Customer called CustomerId
- Value is the entire Customer stored as JSON

Additionally, there is an expiry/TTL set for each Key.