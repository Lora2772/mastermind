# Master Mind
A Spring Boot MVC application built with Gradle.

## Requirements
Java 17+

## Running the Application
By default, the application runs on **http://localhost:8080**.

### Run with Gradle
```bash
./gradlew bootRun
```

## Initial Thoughts
A web application with a simple HTML UI was designed, with the backend implemented using Spring Boot and the frontend rendered by the Thymeleaf engine. The project was set up with Spring Initializr and managed with Gradle. 


## Code Structure
Following the MVC architecture, the application includes models for a Game class and a Guess class, a controller, and Services that handle the core logic for computing the number of correct digits and their positions.

## Process
The project began with the implementation of a backend service to generate a four-digit answer, initially using Java’s Random class, along with corresponding JUnit tests to verify its correctness. 

Once this foundation was in place, a Game class and controller were created to support game initialization, allowing a new session to be started from the home page. 

A Guess class was then introduced to handle user input, enabling players to enter four-digit guesses while tracking and displaying the number of attempts. 

To extend functionality, logic was developed to calculate the number of correct digits and their positions for each guess, and guess history was displayed in a table for ongoing reference. 

Input validation was also added so that invalid entries, such as digits greater than seven, triggered immediate warning messages. Instant feedback mechanisms were integrated into the game page, providing users with colored indicators after each submission. 

A dedicated result page was implemented to present the final outcome—whether the player won or lost—along with the complete guess history once the game concluded or all attempts were exhausted. 

The answer generation service was later enhanced by switching from Java’s random number generator to the random.org public API, with checked exceptions handled and redundant controller code removed for better maintainability. 

Additional features were developed to allow restarting or resuming games, addressing lifecycle edge cases and ensuring correct navigation between home, game, and result pages. 

Robustness was further improved by introducing a fallback mechanism so that when the random.org API returned a “server busy” response, the application automatically reverted to the Java-based generator. The CheckService class was optimized by replacing HashMaps with arrays in the number-counting logic. 

Finally, the attempt counter message was refined to display remaining attempts, aligning the user experience with assignment requirements.

## Implemented extensions
Add a timer for the entire game - displayed the time spent on the result page.
