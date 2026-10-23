package com.hbm.world.structure.templates.radio;


public class NoteBuilder {
    public record Hit(Instrument instrument,Note note,Octave octave){}
	
	private String beat = "";

	public static NoteBuilder start() {
		return new NoteBuilder();
	}
	
	public NoteBuilder add(Instrument instrument, Note note, Octave octave) {
		if(!beat.isEmpty())
			beat += "-";
		
		String result = instrument.ordinal() + ":" + note.ordinal() + ":" + octave.ordinal();
		
		beat += result;
		
		return this;
	}
	
	public String end() {
		return beat;
	}
	
	public static Hit[] translate(String beat) {
		String[] hits = beat.split("-");
		Hit[] notes = new Hit[hits.length];
		
		try {
			for(int i = 0; i < hits.length; i++) {
				String[] components = hits[i].split(":");
				Instrument instrument = Instrument.values()[Integer.parseInt(components[0])];
				Note note = Note.values()[Integer.parseInt(components[1])];
				Octave octave = Octave.values()[Integer.parseInt(components[2])];
				
				notes[i] = new Hit(instrument, note, octave);
			}
			
			return notes;
		} catch(Exception ex) {
			return new Hit[0];
		}
	}
	
	public static enum Instrument {
		PIANO,
		BASSDRUM,
		SNARE,
		CLICKS,
		BASSGUITAR;
	}
	
	public static enum Note {
		F_SHARP,
		G,
		G_SHARP,
		A,
		A_SHARP,
		B,
		C,
		C_SHARP,
		D,
		D_SHARP,
		E,
		F;
	}
	
	public static enum Octave {
		LOW, MID, HIGH;
	}
}
