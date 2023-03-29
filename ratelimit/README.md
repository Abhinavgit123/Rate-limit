## **Features**
Rate limiter using sliding window method

## **Configurations**

Change the following parameters in application.properties as per your need:

There are three different categories for processing rate limiting.It is **assumed** that the application has Enterprise edition, community edition, free edition(Default) and thereby applying the necessary values for each edition.

1. **request.time.limit**: fixed time window (seconds) to apply rate limit.
2. **request.counter**: Request count allowed in the fixed time window.
3. **time.window.count**: Sub-Windows
4. **userId.name**: UserID attribute name in the HTTP header. This application uses this attribute name to uniquely identify a user and then applies rate limit on incoming requests based on the configured parameters.

The values for the below parameters change according to the editions:

1. **request.time.limit**
2. **request.counter**
3. **time.window.count**

## **Deployment steps**

To build and run the application you need to have Maven and JDK(preferably latest version).
1. To build it, run the command: **mvn clean install** 
2. Go to the target directory and run the command: **java -jar ratelimit-0.0.1-SNAPSHOT.jar** (OR) Clone the project in IDE,configure and run the              application
3. Send a POST request to http://localhost:8080/v1/ratelimit/limitcheck with the request header containing "userid" key,corresponding value and "edition" key,corresponding value (Note:"edition" is not a mandatory header,if not applicable it would be considered as free edition).


## **Output**

1. If rate-limit is allowed within the time period for the specified userId it will return **true**.
2. If rate-limit is exceeded within the time period for the specified userId it will return **Request limit exceeded for User ID.Please try again in sometime.**
3. If Userid is not specified it will return **Please provide a valid UserID**


## **Screenshots**

### Scenario 1: When rate limiting for a user is accepted


### Scenario 2: When rate limiting for a user is exceeded


### Scenario 3: When userid is not specified in the header



## **Testing**

1. TestNG framework has been used for creating automated tests

## **Code Docs**

This code implements rate limiting for a given username by limiting the number of requests made by the user within a specific time window.

The limitcheck method checks if a user is allowed to make a request or not by calling isRatelimitAllowed method.
If the user is a new user, then it creates a new record for the user and allows the user to make a request. 
Otherwise, it checks the existing user record and verifies whether the user has exceeded the rate limit or not.

The isRatelimitAllowed method checks the current time window, deletes outdated entries from the user's record, and checks if the number of requests made by the user is within the allowed limit.
If the limit is not exceeded, then it updates the record for the current time window and returns true. 
Otherwise, it throws a RatelimitException indicating that the request limit has been exceeded.

The deletePrevEntries method removes outdated entries from the user's record by iterating through the existing entries and removing those that fall outside the valid time window within the time limit.
It then returns the overall count of requests made by the user within the valid time windows.
