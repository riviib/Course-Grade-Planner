# Course Grade Planner

A full-stack web application that allows users to plan course assessments and explore grade scenarios through real-time calculations and interactive data input.

## Overview

The Course Grade Planner helps students track assessment performance and determine what grades are needed to achieve a target overall score. The application dynamically updates results based on user input, providing immediate feedback.

## Features

* Add, edit, and delete course assessments
* Input assessment weights and marks with validation
* Real-time calculation of weighted grade and remaining course weight
* Target-grade “what-if” analysis to determine required performance
* Interactive UI with dynamic updates and data visualization using Vaadin Grid

## How It Works

* Users input assessments including name, type, weight, and marks
* The system calculates the current weighted grade based only on completed assessments
* Remaining weight is computed to determine how much of the course is left
* Users can set a target overall grade, and the system calculates the required average on remaining work
* All calculations update instantly as data is modified

## Technologies Used

* Java
* Vaadin (Flow)
* Spring Boot
* Maven

## How to Run

1. Clone the repository
2. Open the project in IntelliJ IDEA (or any Java IDE)
3. Run the Spring Boot application
4. Open the application in your browser (default: http://localhost:8080)

## Notes

This project demonstrates concepts in full-stack development, event-driven UI design, input validation, and separation of concerns.
