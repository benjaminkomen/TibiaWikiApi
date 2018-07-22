[![Build Status](https://www.travis-ci.org/benjaminkomen/TibiaWikiApi.svg?branch=master)](https://www.travis-ci.org/benjaminkomen/TibiaWikiApi)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=coverage)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)

# TibiaWikiApi

Gets data from http://tibia.wikia.com and exposes this data using a RESTful JSON API.

## Instructions
Clone this git project to your local computer and compile it using: `mvn clean install` from your favourite command line
environment such as Git Bash. Now you can start the application by typing the following in your command line:
 `java -jar target/TibiaWikiApi-1.0.0-SNAPSHOT.jar` while the current directory is the home folder of the project. 
 This process was tested with Maven 3.5.2 and Java 9.0.1
 
 You can now access the REST resources using your browser or any REST client such as Postman or curl from your command line.
 E.g. navigating to http://localhost:8080/corpses should give you a list of corpses.

## Achievements

A list of all achievements (200 KB):
http://localhost:8080/achievements

An achievement by achievement name, e.g. "Goo Goo Dancer":
http://localhost:8080/achievements/Goo_Goo_Dancer

## Books

A list of all books (1.1 MB):
http://localhost:8080/books

A book by book name, e.g. "Dungeon Survival Guide (Book)":
http://localhost:8080/books/Dungeon_Survival_Guide_%28Book%29

## Buildings

A list of all buildings (223 kB):
http://localhost:8080/buildings

A building by building name, e.g. "Theater Avenue 8b":
http://localhost:8080/buildings/Theater_Avenue_8b

## Corpses

A list of all corpses (16 kB):
http://localhost:8080/corpses

A corpse by corpse name, e.g. "Dead Rat":
http://localhost:8080/corpses/Dead_Rat

## Creatures

A list of all creatures (2.55 MB):
http://localhost:8080/creatures

A creature by creature name, e.g. "Dragon":
http://localhost:8080/creatures/Dragon

## Effects

A list of all effects (31 kB):
http://localhost:8080/effects

An effect by effect name, e.g. "Throwing Cake Effect":
http://localhost:8080/effects/Throwing_Cake_Effect

## Locations (Geography)

A list of all locations (12 kB):
http://localhost:8080/locations

A location by location name, e.g. "Thais":
http://localhost:8080/locations/Thais

## Hunting Places

A list of all hunting places (12 kB):
http://localhost:8080/huntingplaces

A location by location name, e.g. "Hero Cave":
http://localhost:8080/huntingplaces/Hero_Cave
