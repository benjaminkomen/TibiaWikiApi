[![Build Status](https://www.travis-ci.org/benjaminkomen/TibiaWikiApi.svg?branch=master)](https://www.travis-ci.org/benjaminkomen/TibiaWikiApi)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=coverage)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)

# TibiaWikiApi

Gets data from http://tibia.wikia.com and exposes this data using a RESTful JSON API.

## Instructions
Clone this git project to your local computer and compile it using: `mvn clean install` from your favourite command line
environment such as Git Bash. Now you can start the application by typing the following in your command line:
 `java -jar target/TibiaWikiApi-1.0.0-SNAPSHOT.jar` while the current directory is the home folder of the project. 
 This process was tested with Maven 3.5.3 and Java 11
 
 Alternatively and maybe easier, you can also execute: `mvn spring-boot:run` in your command line.
 
 You can now access the REST resources using your browser or any REST client such as Postman or curl from your command line.
 E.g. navigating to http://localhost:8080/api/corpses should give you a list of corpses.
 
## API documentation
Navigate to http://localhost:8080/ for automatically generated api documentation provided by Swagger.
 
## Query parameters
For all resources the query parameter ?expand=true can be appended to get a full list of JSON objects at the collection resource level. For example, instead of http://localhost:8080/achievements the url http://localhost:8080/achievements?expand=true should be used.

## Achievements

A list of all achievements:
http://localhost:8080/api/achievements

An achievement by achievement name, e.g. "Goo Goo Dancer":
http://localhost:8080/api/achievements/Goo_Goo_Dancer

## Books

A list of all books:
http://localhost:8080/api/books

A book by book name, e.g. "Dungeon Survival Guide (Book)":
http://localhost:8080/api/books/Dungeon_Survival_Guide_%28Book%29

## Buildings

A list of all buildings:
http://localhost:8080/api/buildings

A building by building name, e.g. "Theater Avenue 8b":
http://localhost:8080/api/buildings/Theater_Avenue_8b

## Corpses

A list of all corpses:
http://localhost:8080/api/corpses

A corpse by corpse name, e.g. "Dead Rat":
http://localhost:8080/api/corpses/Dead_Rat

## Creatures

A list of all creatures:
http://localhost:8080/api/creatures

A creature by creature name, e.g. "Dragon":
http://localhost:8080/api/creatures/Dragon

## Effects

A list of all effects:
http://localhost:8080/api/effects

An effect by effect name, e.g. "Blue Electricity Effect":
http://localhost:8080/api/effects/Blue_Electricity_Effect

## Hunting Places

A list of all hunting places:
http://localhost:8080/api/huntingplaces

A location by location name, e.g. "Hero Cave":
http://localhost:8080/api/huntingplaces/Hero_Cave

## Items

A list of all items:
http://localhost:8080/api/items

An item by item name, e.g. "Carlin Sword":
http://localhost:8080/api/items/Carlin_Sword

## Keys

A list of all keys:
http://localhost:8080/api/keys

A key by key name (not only the number but the full wiki pagename), e.g. "Key 4055":
http://localhost:8080/api/keys/Key_4055

## Locations (Geography)

A list of all locations:
http://localhost:8080/api/locations

A location by location name, e.g. "Thais":
http://localhost:8080/api/locations/Thais

## Missiles

A list of all missiles:
http://localhost:8080/api/missiles

A missile by name, e.g. "Throwing Cake Missile":
http://localhost:8080/api/missiles/Throwing_Cake_Missile

## Mounts

A list of all mounts:
http://localhost:8080/api/mounts

A mount by mount name, e.g. "Donkey":
http://localhost:8080/api/mounts/Donkey

## NPCs

A list of all NPCs:
http://localhost:8080/api/npcs

An NPC by NPC name, e.g. "Sam":
http://localhost:8080/api/npcs/Sam

## Objects

A list of all objects:
http://localhost:8080/api/objects

An object by object name, e.g. "Blueberry Bush":
http://localhost:8080/api/objects/Blueberry_Bush

## Outfits

A list of all outfits:
http://localhost:8080/api/outfits

An outfit by outfit name, e.g. "Pirate Outfits":
http://localhost:8080/api/outfits/Pirate_Outfits

## Quests

A list of all quests:
http://localhost:8080/api/quests

A quest by quest name, e.g. "The Paradox Tower Quest":
http://localhost:8080/api/quests/The_Paradox_Tower_Quest

## Spells

A list of all spells:
http://localhost:8080/api/spells

A spell by spell name, e.g. "Light Healing":
http://localhost:8080/api/spells/Light_Healing

## Streets

A list of all streets:
http://localhost:8080/api/streets

A street by street name, e.g. "Sugar Street":
http://localhost:8080/api/streets/Sugar_Street