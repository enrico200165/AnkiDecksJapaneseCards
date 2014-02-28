package com.enrico_viali.jacn.common;


public enum FieldUpdate {

	OVERWRITE(0, "Overwrite"), NOOVWR_ERROR(1, "NoOvwrError"), NOOVWR_QUIET(2, "NoOvwrQuiet"), NOOVWR_WARN(3, "noOvwrWarn"), NOOVWR_SAVECOORD_EVKANJI(4, "noOvwrSaveCoordEVKanji"), SYSTEM_EXIT(9,
			"System.exit(0)"), PROMPT(99, "Prompt");

	public final int id;
	public final String label;

	FieldUpdate(final int id, final String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return label;
	}

	public static FieldUpdate fromString(final String str) {
		for (FieldUpdate e : FieldUpdate.values()) {
			if (e.toString().equalsIgnoreCase(str)) {
				return e;
			}
		}
		return null;
	}

	public static FieldUpdate fromId(final int id) {
		for (FieldUpdate e : FieldUpdate.values()) {
			if (e.id == id) {
				return e;
			}
		}
		return null;
	}
}
