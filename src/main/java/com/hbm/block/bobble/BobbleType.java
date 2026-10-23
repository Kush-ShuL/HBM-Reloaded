package com.hbm.block.bobble;
/** Exact source order, names, engraving, contribution, inscription, layers and scrap mapping. */
public enum BobbleType {
NONE(			"null",								"null",				null,														null,																								false,	"BOARD_BLANK"),
		STRENGTH(		"Strength",							"Strength",			null,														"It's essential to give your arguments impact.",													false,	"BRIDGE_BIOS"),
		PERCEPTION(		"Perception",						"Perception",		null,														"Only through observation will you perceive weakness.",												false,	"BRIDGE_NORTH"),
		ENDURANCE(		"Endurance",						"Endurance",		null,														"Always be ready to take one for the team.",														false,	"BRIDGE_SOUTH"),
		CHARISMA(		"Charisma",							"Charisma",			null,														"Nothing says pizzaz like a winning smile.",														false,	"BRIDGE_IO"),
		INTELLIGENCE(	"Intelligence",						"Intelligence",		null,														"It takes the smartest individuals to realize$there's always more to learn.",						false,	"BRIDGE_BUS"),
		AGILITY(		"Agility",							"Agility",			null,														"Never be afraid to dodge the sensitive issues.",													false,	"BRIDGE_CHIPSET"),
		LUCK(			"Luck",								"Luck",				null,														"There's only one way to give 110%.",																false,	"BRIDGE_CMOS"),
		BOB(			"Robert \"The Bobcat\" Katzinsky",	"HbMinecraft",		"Hbm's Nuclear Tech Mod",									"I know where you live, " + System.getProperty("user.name"),									false,	"CPU_SOCKET"),
		FRIZZLE(		"Frooz",							"Frooz",			"Weapon models",											"BLOOD IS FUEL",																					true,	"CPU_CLOCK"),
		PU238(			"Pu-238",							"Pu-238",			"Improved Tom impact mechanics",							null,																								false,	"CPU_REGISTER"),
		VT(				"VT-6/24",							"VT-6/24",			"Balefire warhead model and general texturework",			"You cannot unfuck a horse.",																		true,	"CPU_EXT"),
		DOC(			"The Doctor",						"Doctor17PH",		"Russian localization, lunar miner",						"Perhaps the moon rocks were too expensive",														true,	"CPU_CACHE"),
		BLUEHAT(		"The Blue Hat",						"The Blue Hat",		"Textures",													"payday 2's deagle freeaim champ of the year 2022",													true,	"MEM_16K_A"),
		PHEO(			"Pheo",								"Pheonix",			"Deuterium machines, tantalium textures, Reliant Rocket",	"RUN TO THE BEDROOM, ON THE SUITCASE ON THE LEFT,$YOU'LL FIND MY FAVORITE AXE",						true,	"MEM_16K_B"),
		ADAM29(			"Adam29",							"Adam29",			"Ethanol, liquid petroleum gas",							"You know, nukes are really quite beatiful.$It's like watching a star be born for a split second.",	true,	"MEM_16K_C"),
		UFFR(			"UFFR",								"UFFR",				"All sorts of things from his PR",							"fried shrimp",																						false,	"MEM_SOCKET"),
		VAER(			"vaer",								"vaer",				"ZIRNOX",													"taken de family out to the weekend cigarette festival",											true,	"MEM_16K_D"),
		NOS(			"Dr Nostalgia",						"Dr Nostalgia",		"SSG and Vortex models",									"Take a picture, I'ma pose, paparazzi$I've been drinking, moving like a zombie",					true,	"BOARD_TRANSISTOR"),
		DRILLGON(		"Drillgon200",						"Drillgon200",		"1.12 Port",												null,																								false,	"CPU_LOGIC"),
		CIRNO(			"Cirno",							"Cirno",			"the only multi layered skin i had",						"No brain. Head empty.",																			true,	"BOARD_BLANK"),
		MICROWAVE(		"Microwave",						"Microwave",		"OC Compatibility and massive RBMK/packet optimizations",	"they call me the food heater$john optimization",													true,	"BOARD_CONVERTER"),
		PEEP(			"Peep",								"LePeeperSauvage",	"Coilgun, Leadburster and Congo Lake models, BDCL QC",		"Fluffy ears can't hide in ash, nor snow.",															true,	"CARD_BOARD"),
		MELLOW(			"MELLOWARPEGGIATION",				"Mellow",			"NBT Structures, industrial lighting, animation tools",		"Make something cool now, ask for permission later.",												true,	"CARD_PROCESSOR"),
		ABEL(			"Abel1502", 						"Abel1502", 		"Abilities GUI, optimizations and many QoL improvements", 	"NANTO SUBARASHII",																				true,	"CPU_REGISTER");
    public final String name,label,contribution,inscription,scrap;
    public final boolean skinLayers;
    BobbleType(String name,String label,String contribution,String inscription,boolean layers,String scrap){this.name=name;this.label=label;this.contribution=contribution;this.inscription=inscription;this.skinLayers=layers;this.scrap=scrap;}
    public static BobbleType fromMetadata(int metadata){return values()[(int)(Math.abs((long)metadata)%values().length)];}
    public String key(String field){return "gui.hbm.bobblehead."+name().toLowerCase(java.util.Locale.ROOT)+"."+field;}
}
