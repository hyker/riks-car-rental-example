# Car Rental Example

## Introduction

This mini project is a demonstration of how RIKS can be used to provide confidentiality for sensitive data with the option of retroactively giving access to the data.
Much of the project which does not directly involve RIKS is mocked for simplicity, for example the Car Rental is Implemented as a list a cars.

## Scenario

### Problem

The scenario revolves around a car rental service.
This car rental service consists of a fleet owner and a bunch of cars.
The biggest asset of the rental service is, of course, the cars.
Lately there has been a number of car thefts.
In order to deal with the car theft problem, the owner wants to fit the cars with GPS trackers.

The thefts has not occurred when the cars was in the garage, but when they were rented out.
So the cars would need to have a tracker even when they are rented out.
The customers position being known by the car rental company would be a privacy infringement and would hurt the public image of the company.

So how do we solve this?
The customer does not want his or her position known to the company, but the company wants to be able to track stolen cars.
The key is that once a car is stolen, the customer is no longer in the car and the position is therefore no longer confidential.
The decision of when to allow the rental company access to data needs to be in the hand of the customer, otherwise he or she can never know if the position is private.
But on the other hand, once the history of where a car has been can be valuable when tracking a stolen car.

### Solution

This is a scenario where RIKS comes in handy.
Using RIKS, we can let the car broadcast its current position constantly and cache the position in a central storage.
But before the position leaves the car, it is encrypted using RIKS with the key of the current car user.

This way, the full position history of the car is cached and available.
But you need to ask the current car holder for the encryption key in order to be able to decrypt it.

## System Parts

### Clients

The drivers and the rental service are able to encrypt, decrypt and send messages.
The cars are subjects of the clients.

### Cloud service

The clients communicate through a cloud service, in this case an MQTT broker.

## Test it

### Obtain the code

    git clone git@github.com:hykersec/riks-car-rental-example.git

### Download libraries

    [TODO]

### Run project

#### Terminal

    cd riks-car-rental-example
    gradle run

#### Intellij

You can also use intellij to run the project.