package com.hbm.client.qmaw;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.world.item.ItemStack;

public class QuickManualAndWiki {
	public static final String EN_US = "en_us";
	
	public String name;
	/** Resource identity for the native standard language keys; not another content store. */
	public String languageKey;
	public ItemStack icon;

	/** Removes this manual from the calculator search feature */
	public boolean noIndex = false;

	public HashMap<String, String> title = new HashMap();
	public HashMap<String, String> contents = new HashMap();
	
	public QuickManualAndWiki(String name) {
		this.name = name;
	}
	
	public QuickManualAndWiki setIcon(ItemStack stack) {
		this.icon = stack;
		return this;
	}
	
	public QuickManualAndWiki addTitle(String lang, String title) {
		this.title.put(lang, title);
		return this;
	}
	
	public QuickManualAndWiki addLang(String lang, String contents) {
		this.contents.put(lang, contents);
		return this;
	}
	
	public QuickManualAndWiki noIndex() {
		this.noIndex = true;
		return this;
	}

	/** Search, indexes and the page itself use the same standard-language-first title. */
	public String getTitle(LanguageManager language) {
		String value = localized(title, "title", language.getSelected());
		return value == null ? "Missing Localization!" : value;
	}

	String localized(Map<String, String> literals, String field, String locale) {
		if(languageKey != null) {
			String key = languageKey + "." + field;
			var language = net.minecraft.locale.Language.getInstance();
			if(language.has(key)) return language.getOrDefault(key);
		}
		String value = literals.get(locale);
		return value != null ? value : literals.get(EN_US);
	}
}
