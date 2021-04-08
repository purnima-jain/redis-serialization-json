# redis-serialization-json
Usage of Redis as database with unique Keys, customerId as HashKeys &amp; complete Customer object serialized-as-JSON as value.

Sample application for Redis usage as database where:
- Key is "CUSTOMER_STORE" + customerId
- HashKey is an attribute of Customer called customerId
- Value is the entire Customer stored as JSON

Additionally, there is an expiry/TTL set for each Key.