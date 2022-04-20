## Fuck Off As A Service Challenge
This repository contains a possible solution to the Fuck Off As A Service Challenge.

The challenge requires to implement a call to [foaas](https://www.foaas.com/) with a per-client rate limit of 5 requests every 10 seconds.

The full challenge details can be found in [notion](https://thorn-paperback-665.notion.site/L2-Coding-Challenge-f55f26875e1c4871b528f07e109c0e52)

## Technology 
This challenge was implemented using Java 14 (Switch Expressions) using Spring Boot as web framework.

## How to run
The project can be run by executing `./gradlew bootRun` from the project''' root folder.

## How to call the service
The project exposes only one endpoint, which is:

**GET: localhost:8080/message?client_id=$some_client_id**

As an example, the following cURL command can be used:

```bash
curl "http://localhost:8080/message?client_id=1234" -i
```
## Rate Limit Algorithm
The challenge has 2 rate limit algorithm implemented:

### Fixed Window 
It sets the full quota every time a new interval elapses. This means, given an interval of 10 seconds and a quota of 5 requests, every 10 seconds the full quota is available again.

This strategy guarantees that for a given interval (say 10:00:00 to 10:00:10) only 5 requests will be accepted. On the other hand 5 requests could be done at 10:00:09 and by 10:00:10 the full quota is available again. It opens a small window open for a request spike to harm the service.

### Sliding Window: 
It resets the quota proportionally to the time elapsed. As an example, given an interval of 10 seconds and a quota of 5 requests, it will create 1 unit every 2 seconds.

This strategy is safer toward spikes as the client starts with the full quota and it has a "reload" time before it gets the full quota again. The main advantage is that it allows the server to "pace" the clients as new units are added in small but consistent numbers.

The rate limit Algorithm can be changed by configuration, using the configuration key `foaas.ratelimit.algorithm`, with *fixed_window* and *sliding_window* as values.

## Rate Limit Implementation
This challenge brings 2 implementations for each algorithm.

### In House
This is a really simple and basic implementation to show how each algorithm works.

It's not production-ready as it doesn't care about border conditions and performance optimization. 
For example, it doesn't allow for "cluster rate limit" (share the rate limit between all the available servers) which is a feature supported by many libraries. Then, this implementation offers a rate limit of `quota * number of servers` for each client, which can be problematic in certain use cases.

### Bucket4J
This implements the rate limit using the Bucket4J library. Though this implementation would be production-ready, it doesn't really show how each algorithm works.

## Testing
For this implementation, only the InHouse Rate Limit has test coverage as it's the most interesting part of the challenge.

In a productive codebase, I would add test coverage to all the logic. Given that this codebase won't evolve, and it has a small amount of features, I thought it wasn't worth to have full coverage, given the time constraints for the challenge.

For e2e testing, there are some test cases defined in the [test_cases](test_cases). They can be executed from the [test_cases](test_cases) by executing the `./all_tests.sh` command. **Disclaimer: the application must be running before executing the test cases.**