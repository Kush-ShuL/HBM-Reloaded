package com.hbm.worldgen.bedrockresource;

/** The six source ItemBedrockOreNew ordinals used by raw-resource density, in original order. */
public enum BedrockOreType {
    LIGHT_METAL("light"),HEAVY_METAL("heavy"),RARE_EARTH("rare"),ACTINIDE("actinide"),NON_METAL("nonmetal"),CRYSTALLINE("crystal");
    public final String suffix;
    BedrockOreType(String suffix){this.suffix=suffix;}
}
