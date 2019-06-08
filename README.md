[![Build Status](https://www.travis-ci.org/benjaminkomen/TibiaWikiApi.svg?branch=master)](https://www.travis-ci.org/benjaminkomen/TibiaWikiApi)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=coverage)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)

# TibiaWikiApi

Gets data from http://tibia.wikia.com and exposes this data using a RESTful JSON API.

## Instructions
Clone this git project to your local computer and compile it using: `mvn clean install` from your favourite command line
environment such as Git Bash. Now you can start the application by typing the following in your command line:
 `java -jar target/TibiaWikiApi-1.0.0-SNAPSHOT.jar` while the current directory is the home folder of the project. 
 This process was tested with Maven 3.6.1 and Java 12.
 
 Alternatively and maybe easier, you can also execute: `mvn spring-boot:run` in your command line.
 
 You can now access the REST resources using your browser or any REST client such as Postman or curl from your command line.
 E.g. navigating to https://tibiawikiapi.herokuapp.com/api/corpses should give you a list of corpses.
 
## API documentation
Navigate to https://tibiawikiapi.herokuapp.com/ for Swagger API documentation.
 
## Query parameters
For all resources the query parameter ?expand=true can be appended to get a full list of JSON objects at the collection resource level. For example, instead of https://tibiawikiapi.herokuapp.com/achievements the url https://tibiawikiapi.herokuapp.com/achievements?expand=true should be used.

## Achievements

A list of all achievements:
https://tibiawikiapi.herokuapp.com/api/achievements

An achievement by achievement name, e.g. "Goo Goo Dancer":
https://tibiawikiapi.herokuapp.com/api/achievements/Goo_Goo_Dancer

## Books

A list of all books:
https://tibiawikiapi.herokuapp.com/api/books

A book by book name, e.g. "Dungeon Survival Guide (Book)":
https://tibiawikiapi.herokuapp.com/api/books/Dungeon_Survival_Guide_%28Book%29

## Buildings

A list of all buildings:
https://tibiawikiapi.herokuapp.com/api/buildings

A building by building name, e.g. "Theater Avenue 8b":
https://tibiawikiapi.herokuapp.com/api/buildings/Theater_Avenue_8b

## Corpses

A list of all corpses:
https://tibiawikiapi.herokuapp.com/api/corpses

A corpse by corpse name, e.g. "Dead Rat":
https://tibiawikiapi.herokuapp.com/api/corpses/Dead_Rat

## Creatures

A list of all creatures:
https://tibiawikiapi.herokuapp.com/api/creatures

A creature by creature name, e.g. "Dragon":
https://tibiawikiapi.herokuapp.com/api/creatures/Dragon

## Effects

A list of all effects:
https://tibiawikiapi.herokuapp.com/api/effects

An effect by effect name, e.g. "Blue Electricity Effect":
https://tibiawikiapi.herokuapp.com/api/effects/Blue_Electricity_Effect

## Hunting Places

A list of all hunting places:
https://tibiawikiapi.herokuapp.com/api/huntingplaces

A location by location name, e.g. "Hero Cave":
https://tibiawikiapi.herokuapp.com/api/huntingplaces/Hero_Cave

## Items

A list of all items:
https://tibiawikiapi.herokuapp.com/api/items

An item by item name, e.g. "Carlin Sword":
https://tibiawikiapi.herokuapp.com/api/items/Carlin_Sword

## Keys

A list of all keys:
https://tibiawikiapi.herokuapp.com/api/keys

A key by key name (not only the number but the full wiki pagename), e.g. "Key 4055":
https://tibiawikiapi.herokuapp.com/api/keys/Key_4055

## Locations (Geography)

A list of all locations:
https://tibiawikiapi.herokuapp.com/api/locations

A location by location name, e.g. "Thais":
https://tibiawikiapi.herokuapp.com/api/locations/Thais

## Missiles

A list of all missiles:
https://tibiawikiapi.herokuapp.com/api/missiles

A missile by name, e.g. "Throwing Cake Missile":
https://tibiawikiapi.herokuapp.com/api/missiles/Throwing_Cake_Missile

## Mounts

A list of all mounts:
https://tibiawikiapi.herokuapp.com/api/mounts

A mount by mount name, e.g. "Donkey":
https://tibiawikiapi.herokuapp.com/api/mounts/Donkey

## NPCs

A list of all NPCs:
https://tibiawikiapi.herokuapp.com/api/npcs

An NPC by NPC name, e.g. "Sam":
https://tibiawikiapi.herokuapp.com/api/npcs/Sam

## Objects

A list of all objects:
https://tibiawikiapi.herokuapp.com/api/objects

An object by object name, e.g. "Blueberry Bush":
https://tibiawikiapi.herokuapp.com/api/objects/Blueberry_Bush

## Outfits

A list of all outfits:
https://tibiawikiapi.herokuapp.com/api/outfits

An outfit by outfit name, e.g. "Pirate Outfits":
https://tibiawikiapi.herokuapp.com/api/outfits/Pirate_Outfits

## Quests

A list of all quests:
https://tibiawikiapi.herokuapp.com/api/quests

A quest by quest name, e.g. "The Paradox Tower Quest":
https://tibiawikiapi.herokuapp.com/api/quests/The_Paradox_Tower_Quest

## Spells

A list of all spells:
https://tibiawikiapi.herokuapp.com/api/spells

A spell by spell name, e.g. "Light Healing":
https://tibiawikiapi.herokuapp.com/api/spells/Light_Healing

## Streets

A list of all streets:
https://tibiawikiapi.herokuapp.com/api/streets

A street by street name, e.g. "Sugar Street":
https://tibiawikiapi.herokuapp.com/api/streets/Sugar_Street