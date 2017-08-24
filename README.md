# optimus
The core engine that does all the heavy lifting, responsible for doing the setup and teardown activities for each test that is triggered using optimus template project.

Consumer passes a test feed containing details of the app(s) and the required number of device(s) per test based on whether its a single app or inter-app sceanrio. Optimus registers a BOT instance per app under test, and the bot is responsible for doing the following:
* Starting an appium server instance 
* Procuring a device from the pool
* Creating a driver instance
* Destroy appium server and driver instance at the end of test run
* Release the device back to the pool

It talks to multiple sub-projects to acheive the above mentioned objectives.
* [devicemanagement](https://github.com/testvagrant/devicemanagement) for dealing with the device pool
* [mdb](https://github.com/testvagrant/mdb) for any interaction with the devices under test

