![Build Status](https://github.com/benjaminkomen/TibiaWikiApi/workflows/Build/badge.svg)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.tibiawiki%3ATibiaWikiApi&metric=coverage)](https://sonarcloud.io/dashboard?id=com.tibiawiki%3ATibiaWikiApi)

# TibiaWikiApi
Gets data from http://tibia.wikia.com and exposes this data using a RESTful JSON API.

## View online
Navigate to https://tibiawiki.dev to view the Swagger API of this project.

## Run locally
Clone this git project to your local computer and compile it using: `./gradlew build` from your favourite command line
terminal. Then execute: `./gradlew bootRun` and open your browser on http://localhost:8080

Note that you need to add the [sample settings.xml](.travis.settings.xml) to your $HOME/.m2/settings.xml directory
with a valid username and github token with read packages scope, to download all dependencies.
 
 You can now access the REST resources using your browser or any REST client such as Postman or curl from your command line.
 E.g. navigating to http://localhost:8080/api/corpses should give you a list of corpses.

## Query parameters
For all resources the query parameter `?expand=true` can be appended to get a full list of JSON objects
 at the collection resource level. For example, instead of https://tibiawiki.dev/api/achievements the url
 https://tibiawiki.dev/api/achievements?expand=true can be used.

## Resources

The following resources are available:

| Entity          | List                                                        | Example                                                                                            |
|:-------------   |:------------------------------------------------------      |:-------------------------------------------------------------------------------------------------- |
| Achievement     | [achievements](https://tibiawiki.dev/api/achievements)      | [Goo Goo Dancer](https://tibiawiki.dev/api/achievements/Goo_Goo_Dancer)                            |
| Books           | [books](https://tibiawiki.dev/api/books)                    | [Dungeon Survival Guide (Book)](https://tibiawiki.dev/api/books/Dungeon_Survival_Guide_%28Book%29) |
| Buildings       | [buildings](https://tibiawiki.dev/api/buildings)            | [Theater Avenue 8b](https://tibiawiki.dev/api/buildings/Theater_Avenue_8b)                         |
| Corpses         | [corpses](https://tibiawiki.dev/api/corpses)                | [Dead Rat](https://tibiawiki.dev/api/corpses/Dead_Rat)                                             |
| Creatures       | [creatures](https://tibiawiki.dev/api/creatures)            | [Dragon](https://tibiawiki.dev/api/creatures/Dragon)                                               |
| Effects         | [effects](https://tibiawiki.dev/api/effects)                | [Blue Electricity Effect](https://tibiawiki.dev/api/effects/Blue_Electricity_Effect)               |
| Hunting Places  | [hunting places](https://tibiawiki.dev/api/huntingplaces)   | [Hero Cave](https://tibiawiki.dev/api/huntingplaces/Hero_Cave)                                     |
| Items           | [items](https://tibiawiki.dev/api/items)                    | [Carlin Sword](https://tibiawiki.dev/api/items/Carlin_Sword)                                       |
| Keys            | [keys](https://tibiawiki.dev/api/keys)                      | [Key 4055](https://tibiawiki.dev/api/keys/Key_4055)                                                |
| Locations       | [locations](https://tibiawiki.dev/api/locations)            | [Thais](https://tibiawiki.dev/api/locations/Thais)                                                 |
| Loot Statistics | [loot](https://tibiawiki.dev/api/loot)                      | [Ferumbras](https://tibiawiki.dev/api/loot/Ferumbras)                                              |
| Missiles        | [missiles](https://tibiawiki.dev/api/missiles)              | [Throwing Cake Missile](https://tibiawiki.dev/api/missiles/Throwing_Cake_Missile)                  |
| Mounts          | [mounts](https://tibiawiki.dev/api/mounts)                  | [Donkey](https://tibiawiki.dev/api/mounts/Donkey)                                                  |
| NPCs            | [npcs](https://tibiawiki.dev/api/npcs)                      | [Sam](https://tibiawiki.dev/api/npcs/Sam)                                                          |
| Objects         | [objects](https://tibiawiki.dev/api/objects)                | [Blueberry Bush](https://tibiawiki.dev/api/objects/Blueberry_Bush)                                 |
| Outfits         | [outfits](https://tibiawiki.dev/api/outfits)                | [Pirate Outfits](https://tibiawiki.dev/api/outfits/Pirate_Outfits)                                 |
| Quests          | [quests](https://tibiawiki.dev/api/quests)                  | [The Paradox Tower Quest](https://tibiawiki.dev/api/quests/The_Paradox_Tower_Quest)                |
| Spells          | [spells](https://tibiawiki.dev/api/spells)                  | [Light Healing](https://tibiawiki.dev/api/spells/Light_Healing)                                    |
| Streets         | [streets](https://tibiawiki.dev/api/streets)                | [Sugar Street](https://tibiawiki.dev/api/streets/Sugar_Street)                                     |
