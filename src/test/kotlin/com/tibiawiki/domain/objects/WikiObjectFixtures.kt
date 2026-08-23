package com.tibiawiki.domain.objects

import com.tibiawiki.domain.enums.Article
import com.tibiawiki.domain.enums.BestiaryClass
import com.tibiawiki.domain.enums.BestiaryLevel
import com.tibiawiki.domain.enums.BestiaryOccurrence
import com.tibiawiki.domain.enums.BookType
import com.tibiawiki.domain.enums.BuildingType
import com.tibiawiki.domain.enums.City
import com.tibiawiki.domain.enums.Gender
import com.tibiawiki.domain.enums.KeyType
import com.tibiawiki.domain.enums.Rarity
import com.tibiawiki.domain.enums.Spawntype
import com.tibiawiki.domain.enums.SpellSubclass
import com.tibiawiki.domain.enums.SpellType
import com.tibiawiki.domain.enums.Hands
import com.tibiawiki.domain.enums.ObjectClass
import com.tibiawiki.domain.enums.WeaponType
import com.tibiawiki.domain.enums.YesNo
import org.springframework.test.util.ReflectionTestUtils
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Shared WikiObject constructors for unit and integration tests.
 */
object WikiObjectFixtures {

    fun achievement(
        name: String? = "Goo Goo Dancer",
        description: String? = "Seeing a mucus plug makes your heart dance and you can't resist to see what it hides. Goo goo away!",
        spoiler: String? = "Obtainable by using 100 [[Muck Remover]]s on [[Mucus Plug]]s.",
        premium: YesNo? = YesNo.YES_LOWERCASE,
        points: Int? = 1,
        secret: YesNo? = YesNo.YES_LOWERCASE,
        implemented: String? = "9.6",
        grade: Int? = 1,
        achievementid: Int? = 319,
        relatedpages: String? = "[[Muck Remover]], [[Mucus Plug]]"
    ): Achievement = Achievement(
        name = name,
        implemented = implemented,
        grade = grade,
        description = description,
        spoiler = spoiler,
        premium = premium,
        points = points,
        secret = secret,
        achievementid = achievementid,
        relatedpages = relatedpages
    )

    fun namedAchievement(name: String): Achievement = Achievement(name = name)

    fun book(): Book = Book(
        booktype = BookType.BOOK_BROWN,
        title = "Dungeon Survival Guide",
        pagename = "Dungeon Survival Guide (Book)",
        location = "[[Rookgaard Academy]]",
        blurb = "Tips for exploring dungeons, and warning against being reckless.",
        returnpage = "Rookgaard Libraries",
        relatedpages = "[[Rope]], [[Shovel]]",
        text = "Dungeon Survival Guide<br><br>Don't explore the dungeons before you tested your skills" +
            " in the training cellars of our academy. You will find dungeons somewhere in the wilderness. Don't enter" +
            " dungeons without equipment. Especially a rope and a shovel will prove valuable. Make sure you have a" +
            " supply of torches with you, while wandering into the unknown. It's wise to travel the dungeons in groups" +
            " and not alone. For more help read all the books of the academy before you begin exploring. Traveling in" +
            " the dungeons will reward the cautious and brave, but punish the reckless."
    )

    fun building(): Building = Building(
        name = "Theater Avenue 8b",
        implemented = "Pre-6.0",
        type = BuildingType.House,
        location = "South-east of depot, two floors up.",
        posx = "126.101",
        posy = "124.48",
        posz = "5",
        street = "Theater Avenue",
        houseid = 20315,
        size = 26,
        beds = 3,
        rent = 1370,
        city = City.CARLIN,
        openwindows = 3,
        floors = 1,
        rooms = 1,
        furnishings = "1 [[Wall Lamp]].",
        notes = "",
        image = "[[File:Theater Avenue 8b.png]]"
    )

    fun corpse(): Corpse = Corpse(
        name = "Dead Rat",
        article = Article.A,
        liquid = "[[Blood]]",
        firstVolume = 5,
        firstWeight = BigDecimal.valueOf(63.00).setScale(2, RoundingMode.HALF_UP),
        secondWeight = BigDecimal.valueOf(44.00).setScale(2, RoundingMode.HALF_UP),
        thirdWeight = BigDecimal.valueOf(30.00).setScale(2, RoundingMode.HALF_UP),
        firstDecaytime = "5 minutes.",
        secondDecaytime = "5 minutes.",
        thirdDecaytime = "60 seconds.",
        corpseof = "[[Rat]], [[Cave Rat]], [[Munster]]",
        sellto = "[[Tom]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Seymour]] ([[Rookgaard]]) '''2''' [[gp]]" +
            "<br>[[Billy]] ([[Rookgaard]]) '''2''' [[gp]]<br>[[Humgolf]] ([[Kazordoon]]) '''2''' [[gp]]<br>" +
            "\n[[Baxter]] ([[Thais]]) '''1''' [[gp]]<br>",
        implemented = "Pre-6.0",
        notes = "These corpses are commonly used by low level players on [[Rookgaard]] to earn some gold" +
            " for better [[equipment]]. Only fresh corpses are accepted, rotted corpses are ignored."
    )

    fun effect(): Effect = Effect(
        name = "Fireball Effect",
        effectid = listOf(7, 82),
        primarytype = "Attack",
        lightcolor = 208,
        lightradius = 6,
        causes = "*[[Fireball]] and [[Great Fireball]];",
        effect = "[[Fire Damage]] on target or nothing."
    )

    fun key(): Key = Key(
        number = "4055",
        aka = "Panpipe Quest Key",
        primarytype = KeyType.SILVER,
        location = "[[Jakundaf Desert]]",
        value = "Negotiable",
        npcvalue = 0,
        npcprice = 0,
        buyfrom = "--",
        sellto = "--",
        origin = "Hidden in a rock south of the Desert Dungeon entrance.",
        shortnotes = "Access to the [[Panpipe Quest]].",
        longnotes = "Allows you to open the door ([https://tibia.wikia.com/wiki/Mapper?coords=127.131,125.129,8,3,1,1 here]) to the [[Panpipe Quest]]."
    )

    fun location(): Location = Location(
        ruler = "[[King Tibianus]]",
        implemented = "Pre-6.0",
        population = "{{PAGESINCATEGORY:Thais NPCs|pages}}",
        organization = "[[Thieves Guild]], [[Tibian Bureau of Investigation]], [[Inquisition]]",
        near = "[[Fibula]], [[Mintwallin]], [[Greenshore]], [[Mount Sternum]]",
        map = "[[File:Map_thais.jpg]]",
        map2 = "[[File:Thais.PNG]]"
    )

    fun missile(): Missile = Missile(
        name = "Throwing Cake Missile",
        implemented = "7.9",
        missileid = 42,
        primarytype = "Throwing Weapon",
        shotby = "[[Undead Jester]]'s attack and probably by throwing a [[Throwing Cake]].",
        notes = "This missile is followed by the [[Cream Cake Effect]]: [[File:Cream Cake Effect.gif]]"
    )

    fun mount(): Mount = Mount(
        name = "Donkey",
        speed = 10,
        tamingMethod = "Use a [[Bag of Apple Slices]] on a creature transformed into Donkey.",
        implemented = "9.1",
        achievement = "Loyal Lad",
        notes = "Go to [[Incredibly Old Witch]]'s house,"
    )

    fun npc(): NPC = NPC(
        name = "Sam",
        implemented = "Pre-6.0",
        job = "Artisan",
        job2 = "Weapon Shopkeeper",
        job3 = "Armor Shopkeeper",
        location = "[[Temple Street]] in [[Thais]].",
        posx = BigDecimal.valueOf(126.104).setScale(3, RoundingMode.HALF_UP),
        posy = BigDecimal.valueOf(125.200).setScale(3, RoundingMode.HALF_UP),
        posz = 7,
        gender = Gender.MALE,
        race = "Human",
        city = City.THAIS,
        buysell = YesNo.YES_LOWERCASE,
        sells = "{{Price to Buy |Axe",
        buys = "{{Price to Sell |Axe",
        sounds = listOf("Hello there, adventurer! Need a deal in weapons or armor? I'm your man!"),
        notes = "Sam is the Blacksmith of [[Thais]]."
    )

    fun creature(): Creature = creatureWithLoot(dragonLoot())

    fun creatureWithEmptyLoot(): Creature = Creature(
        name = "Freed Soul",
        article = Article.A,
        actualname = "Freed Soul",
        plural = "Freed Soul",
        implemented = "11.40",
        hitPoints = "?",
        experiencePoints = "?",
        summon = "--",
        convince = "--",
        illusionable = YesNo.NO_LOWERCASE,
        creatureclass = "",
        primarytype = "",
        isboss = YesNo.NO_LOWERCASE,
        abilities = "[[Melee]] (0-?), [[Drown Damage|Drown Bomb]] on self (4000-8000) (damages boss only)",
        behaviour = "They fight in close combat.",
        location = "[[The Souldespoiler]]'s room.",
        strategy = "Do not kill them since you need their help in order to kill the boss.",
        loot = emptyList()
    )

    fun creatureWithLoot(loot: List<LootItem>): Creature = Creature(
        name = "Dragon",
        article = Article.A,
        actualname = "dragon",
        plural = "dragons",
        implemented = "Pre-6.0",
        hitPoints = "1000",
        experiencePoints = "700",
        summon = "--",
        convince = "--",
        illusionable = YesNo.YES_LOWERCASE,
        creatureclass = "Reptiles",
        primarytype = "Dragons",
        bestiaryclass = BestiaryClass.DRAGON,
        bestiarylevel = BestiaryLevel.Medium,
        occurrence = BestiaryOccurrence.COMMON,
        spawntype = listOf(Spawntype.REGULAR, Spawntype.RAID),
        isboss = YesNo.NO_LOWERCASE,
        isarenaboss = YesNo.NO_LOWERCASE,
        abilities = "[[Melee]] (0-120), [[Fire Wave]] (100-170), [[Great Fireball]] (60-140), [[Self-Healing]] (40-70)",
        maxdmg = "430",
        armor = "25",
        pushable = YesNo.NO_LOWERCASE,
        pushobjects = YesNo.YES_LOWERCASE,
        walksthrough = "Fire, Energy, Poison",
        walksaround = "None",
        paraimmune = YesNo.YES_LOWERCASE,
        senseinvis = YesNo.YES_LOWERCASE,
        physicalDmgMod = Percentage.of(100),
        holyDmgMod = Percentage.of(100),
        deathDmgMod = Percentage.of(100),
        fireDmgMod = Percentage.of(0),
        energyDmgMod = Percentage.of(80),
        iceDmgMod = Percentage.of(110),
        earthDmgMod = Percentage.of(20),
        drownDmgMod = Percentage.of("100%?"),
        hpDrainDmgMod = Percentage.of("100%?"),
        bestiaryname = "dragon",
        bestiarytext = "Dragons were",
        sounds = listOf("FCHHHHH", "GROOAAARRR"),
        notes = "Dragons are",
        behaviour = "Dragons are",
        runsat = "300",
        speed = "86",
        location = "[[Thais]] [[Ancient Temple]], [[Darashia Dragon Lair]], [[Mount Sternum Dragon Cave]]," +
            " [[Mintwallin]], deep in [[Fibula Dungeon]], [[Kazordoon Dragon Lair]] (near [[Dwarf Bridge]]), [[Plains" +
            " of Havoc]], [[Elven Bane]] castle, [[Maze of Lost Souls]], southern cave and dragon tower in" +
            " [[Shadowthorn]], [[Orc Fortress]], [[Venore]] [[Dragon Lair]], [[Pits of Inferno]], [[Behemoth Quest]]" +
            " room in [[Edron]], [[Hero Cave]], deep [[Cyclopolis]], [[Edron Dragon Lair]], [[Goroma]], [[Ankrahmun" +
            " Dragon Lair]]s, [[Draconia]], [[Dragonblaze Peaks]], some [[Ankrahmun Tombs]], underground of [[Fenrock]]" +
            " (on the way to [[Beregar]]), [[Krailos Steppe]] and [[Crystal Lakes]].",
        strategy = "'''All''' [[player]]s",
        loot = loot,
        history = "Dragons are"
    )

    fun dragonLoot(): List<LootItem> = listOf(
        LootItem(itemName = "Gold Coin", amount = "0-105"),
        LootItem(itemName = "Dragon Ham", amount = "0-3"),
        LootItem(itemName = "Steel Shield"),
        LootItem(itemName = "Crossbow"),
        LootItem(itemName = "Dragon's Tail"),
        LootItem(itemName = "Burst Arrow", amount = "0-10"),
        LootItem(itemName = "Longsword", rarity = Rarity.SEMI_RARE),
        LootItem(itemName = "Steel Helmet", rarity = Rarity.SEMI_RARE),
        LootItem(itemName = "Broadsword", rarity = Rarity.SEMI_RARE),
        LootItem(itemName = "Plate Legs", rarity = Rarity.SEMI_RARE),
        LootItem(itemName = "Green Dragon Leather", rarity = Rarity.RARE),
        LootItem(itemName = "Wand of Inferno", rarity = Rarity.RARE),
        LootItem(itemName = "Strong Health Potion", rarity = Rarity.RARE),
        LootItem(itemName = "Green Dragon Scale", rarity = Rarity.RARE),
        LootItem(itemName = "Double Axe", rarity = Rarity.RARE),
        LootItem(itemName = "Dragon Hammer", rarity = Rarity.RARE),
        LootItem(itemName = "Serpent Sword", rarity = Rarity.RARE),
        LootItem(itemName = "Small Diamond", rarity = Rarity.VERY_RARE),
        LootItem(itemName = "Dragon Shield", rarity = Rarity.VERY_RARE),
        LootItem(itemName = "Life Crystal", rarity = Rarity.VERY_RARE),
        LootItem(itemName = "Dragonbone Staff", rarity = Rarity.VERY_RARE)
    )

    fun outfit(): Outfit = Outfit(
        name = "Pirate",
        primarytype = "Quest",
        premium = YesNo.YES_LOWERCASE,
        outfit = "premium, see [[Pirate Outfits Quest]].",
        addons = "premium, see [[Pirate Outfits Quest]].",
        achievement = "Swashbuckler",
        implemented = "7.8",
        artwork = "Pirate Outfits Artwork.jpg",
        notes = "Pirate outfits are perfect for swabbing the deck or walking the plank. Quite dashing and great for sailing."
    )

    fun huntingPlace(): HuntingPlace = HuntingPlace(
        name = "Hero Cave",
        image = "Hero",
        implemented = "6.4",
        city = City.EDRON,
        location = "North of [[Edron]], [https://tibia.wikia.com/wiki/Mapper?coords=129.140,123.150,7,3,1,1 here].",
        vocation = "All vocations.",
        lvlknights = "70",
        lvlpaladins = "60",
        lvlmages = "50",
        skknights = "75",
        skpaladins = "80",
        skmages = "1",
        defknights = "75",
        defpaladins = "1",
        defmages = "1",
        lowerlevels = listOf(
            HuntingPlaceSkills(
                areaname = "Demons",
                lvlknights = "130",
                lvlpaladins = "130",
                lvlmages = "130",
                skknights = "1",
                skpaladins = "1",
                skmages = "1",
                defknights = "1",
                defpaladins = "1",
                defmages = "1"
            ),
            HuntingPlaceSkills(
                areaname = "Another Area (Past Teleporter)",
                lvlknights = "230",
                lvlpaladins = "230",
                lvlmages = "230",
                skknights = "2",
                skpaladins = "2",
                skmages = "2",
                defknights = "2",
                defpaladins = "2",
                defmages = "2"
            )
        ),
        exp = "Good",
        loot = "Good",
        bestloot = "Reins",
        bestloot2 = "Foobar",
        bestloot3 = "Foobar",
        bestloot4 = "Foobar",
        bestloot5 = "Foobar",
        map = "Hero Cave 3.png",
        map2 = "Hero Cave 6.png"
    )

    fun item(): TibiaObject {
        val result = TibiaObject(
            itemid = listOf(3283),
            objectclass = ObjectClass.WEAPONS.description,
            flavortext = "Foobar",
            pickupable = YesNo.YES_LOWERCASE,
            usable = YesNo.YES_LOWERCASE,
            levelrequired = 0,
            hands = Hands.One,
            weapontype = WeaponType.Sword,
            attack = "15",
            defense = 13,
            defensemod = "+1",
            enchantable = YesNo.NO_LOWERCASE,
            weight = BigDecimal.valueOf(40.00).setScale(2, RoundingMode.HALF_UP),
            marketable = YesNo.YES_LOWERCASE,
            droppedby = mutableListOf("Grorlam", "Stone Golem"),
            value = "118",
            npcvalue = "118",
            npcprice = "473",
            npcvaluerook = "0",
            npcpricerook = "0",
            buyfrom = "Baltim, Brengus, Cedrik,",
            sellto = "Baltim, Brengus, Cedrik, Esrik,"
        )
        ReflectionTestUtils.setField(result, "name", "Carlin Sword")
        ReflectionTestUtils.setField(result, "article", Article.A)
        ReflectionTestUtils.setField(result, "actualname", "carlin sword")
        ReflectionTestUtils.setField(result, "plural", "?")
        ReflectionTestUtils.setField(result, "notes", "If you have one of these ")
        return result
    }

    fun tibiaObject(): TibiaObject {
        val result = TibiaObject(
            itemid = null,
            sounds = null,
            droppedby = null,
            objectclass = "Bushes",
            walkable = YesNo.NO_LOWERCASE,
            location = "Can be found all around [[Tibia]].",
            notes2 = "<br />{{JSpoiler|After using [[Blueberry]] Bushes 500 times,"
        )
        ReflectionTestUtils.setField(result, "name", "Blueberry Bush")
        ReflectionTestUtils.setField(result, "article", Article.A)
        ReflectionTestUtils.setField(result, "implemented", "7.1")
        ReflectionTestUtils.setField(result, "notes", "They are the source of the [[blueberry|blueberries]].")
        return result
    }

    fun spell(): Spell = Spell(
        name = "Light Healing",
        type = SpellType.Instant,
        subclass = SpellSubclass.Healing,
        words = "exura",
        premium = YesNo.NO_LOWERCASE,
        mana = 20,
        levelrequired = 8,
        cooldown = 1,
        cooldowngroup = 1,
        voc = "[[Paladin]]s, [[Druid]]s and [[Sorcerer]]s",
        druidAbDendriel = "[[Maealil]]",
        paladinAbDendriel = "[[Maealil]]",
        spellcost = 0,
        effect = "Restores a small amount of [[HP|health]]. (Cures [[paralysis]].)",
        notes = "A weak, but popular healing spell."
    )

    fun quest(): Quest = Quest(
        name = "The Paradox Tower Quest",
        aka = "Riddler Quest, Mathemagics Quest",
        reward = "Up to two of the following:",
        location = "[[Paradox Tower]] near [[Kazordoon]]",
        lvl = 30,
        lvlrec = 50,
        log = YesNo.YES_LOWERCASE,
        premium = YesNo.YES_LOWERCASE,
        transcripts = YesNo.YES_LOWERCASE,
        dangers = "[[Wyvern]]s<br /> ([[Mintwallin]]): [[Minotaur]]s,",
        legend = "Surpass the wrath of a madman and subject yourself to his twisted taunting.",
        implemented = "6.61-6.97"
    )

    fun street(): Street = Street(
        name = "Sugar Street",
        implemented = "7.8",
        city = City.LIBERTY_BAY,
        notes = "{{StreetStyles|Sugar Street}} is in west"
    )
}
