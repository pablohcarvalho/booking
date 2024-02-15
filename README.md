# Booking API

## Introduction

This guide will help you to understand this API, give example, details and the most important, I would like to go 
through to the trade-offs decisions.

The most important thing in my opinion is not where you code works but where it fails. So I'm going to start with the problems 
that you could face when implementing a Booking API.

### Problems and Trade-offs about architecture decisions.

##### Problem one: How far can grow your database?

- Let's say  that you have a booking system with only one property, 
  - Your schedule table could go: 365 * 1 number of rowns per year.
  - This is pretty small, and any query / database could handle it without any effort. 
  - You could have a table for Booking and one for Block and join them for check avaibilities.
   
- Let's take this to a more exciting problem, lets say that your booking system have grown, and now you have a five thousand properties.
  - so your schedule table could go: 365 * 5000 number of rowns per year. This started to become a more serious problem to handle
  - Join the table are not as fast as it was
  - Your reports started to become slow
  - You full system is slow, and the another parts of the system have grown with the number of properties
   
**I always start a problem thinking, would this solution works in a BigData enviroment?**

- My first architecture comes from this thought, and I decide to have one table that would save, *Blocks* and *Bookings*.
- The check of avaiabilities would be simple, and I could index it easily without joins

##### Problem two: What is the best way to save the data, by range or by day?

- This decision is much more related in how you want to handle it in you code, and how you want to save and query it from the database
- Exemple: Postgres has a function to search by date range and the query would be something similar to this: 

```
select * from schedule
where daterange(start_date::date, end_date::date, '[]') && daterange(?, ?, '[]')
and property_id = ?
and cancel_date is null
```

- But I'm not sure if all database has this type of function what could increase the complexity of your code, 
- Use this type of function could make you dependent of one type of database.
- Saving it day by day instead of date range would come back to the issue how far can grow your table and how are you gonna handle it?

**For my solution I've choosen the save it using date ranges**

##### Problem three: Use or do not use ORM

- This can become a very long discussion, I'll keep it simple:

**For my solution I've choosen to not use ORM, instead I use raw SQL and JOOQ as the framework for the repository layer**

## Reference Documentation to use the API

#### Property Resources Examples:

```
Fetch a specific Property = GET   /property/{uuid}
Fetch All Properties      = GET   /property/fetch
Save a new Property       = POST  /property
```

```
curl -X GET http://localhost:8080/property/{uuid}

curl -X GET http://localhost:8080/property/fetch

curl --verbose -X POST http://localhost:8080/property \
-H "Content-Type: application/json" \
-d '{"id": "1", "description": "Beach House - Miami", "dailyRate": 100}'
```

#### Block Resources Examples:

```
Fetch a specific Block           = GET   /schedule/block/{property_id}/{id}
Fetch All Blocks from a property = GET   /schedule/block/{property_id}/fetch
Fetch all Blocks                 = GET   /schedule/block/fetch
Schedule a new Block             = POST  /schedule/block/{property_id}
Update a Block                   = PUT   /schedule/block/{property_id}/{id}
Cancel Block                     = PUT   /schedule/block/{property_id}/{id}/cancel
```

```
curl -X GET http://localhost:8080/schedule/block/{property_id}/{id}

curl -X GET http://localhost:8080/schedule/block/{property_id}/fetch

curl -X GET http://localhost:8080/schedule/block/fetch

curl --verbose -X POST http://localhost:8080/schedule/block/{property_id} \
-H "Content-Type: application/json" \
-d '{"startDate": "2024-01-01", "endDate": "2024-01-05"}'

curl --verbose -X PUT http://localhost:8080/schedule/block/{property_id}/{id} \
-H "Content-Type: application/json" \
-d '{"startDate": "2024-01-10", "endDate": "2024-01-15", "propertyId": "{id}"}'

curl --verbose -X PUT http://localhost:8080/schedule/block/{property_id}/{id}/cancel \
-H "Content-Type: application/json"

```

#### Booking Resources Examples:

```
Schedule a new Booking           = POST   /schedule/booking/{property_id}
Update a Booking                 = PUT    /schedule/booking/{property_id}/{booking_id}
Cancel a Booking                 = PUT    /schedule/booking/{property_id}/{id}/cancel
Reopen a canceled Booking        = PUT    /schedule/booking/{property_id}/{id}/reopen
Delete a Booking                 = DELETE /schedule/booking/{property_id}/{id}/delete
Fetch a specific Booking         = GET    /schedule/booking/{property_id}/{id}
Fetch All Bookings from a property = GET    /schedule/booking/{property_id}/fetch
Fetch all Bookings                 = GET    /schedule/booking/fetch

```

```
curl --verbose -X POST http://localhost:8080/schedule/booking/{property_id} \
-H "Content-Type: application/json" \
-d '{"startDate": "2024-01-01", "endDate": "2024-01-05", "guests": [{"name": "Pablo Henriqe", "birthDate": "1993-12-24", "document": "041"}]}'

curl --verbose -X PUT http://localhost:8080/schedule/booking/{property_id}/{booking_id} \
-H "Content-Type: application/json" \
-d '{"startDate": "2024-01-10", "endDate": "2024-01-15", "propertyId": "1", "guests": [{"name": "Pablo Carvalho", "birthDate": "1993-12-24", "document": "041"}]}'

curl --verbose -X PUT http://localhost:8080/schedule/booking/{property_id}/{booking_id}/cancel \
-H "Content-Type: application/json"

curl --verbose -X DELETE http://localhost:8080/schedule/booking/{property_id}/{booking_id}/delete \
-H "Content-Type: application/json"

curl --verbose -X PUT http://localhost:8080/schedule/booking/{property_id}/{booking_id}/reopen \
-H "Content-Type: application/json"

curl --verbose -X GET http://localhost:8080/schedule/booking/{property_id}/{booking_id} \
-H "Content-Type: application/json"

curl --verbose -X GET http://localhost:8080/schedule/booking/{property_id}/fetch \
-H "Content-Type: application/json"

curl --verbose -X GET http://localhost:8080/schedule/booking/fetch \
-H "Content-Type: application/json"

```

PS: No solution for pagination was created.
