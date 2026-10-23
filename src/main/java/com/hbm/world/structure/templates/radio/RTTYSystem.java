package com.hbm.world.structure.templates.radio;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.math.NumberUtils;

import com.hbm.world.structure.templates.radio.NoteBuilder;
import com.hbm.world.structure.templates.radio.NoteBuilder.Instrument;
import com.hbm.world.structure.templates.radio.NoteBuilder.Note;
import com.hbm.world.structure.templates.radio.NoteBuilder.Octave;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@EventBusSubscriber(modid="hbm")
public class RTTYSystem {
    public record Key(Level level,String channel){}
    @SubscribeEvent public static void tick(ServerTickEvent.Pre event){updateBroadcastQueue(event.getServer());}
    @SubscribeEvent public static void stop(ServerStoppedEvent event){broadcast.clear();newMessages.clear();}

	/** Public frequency band for reading purposes, delayed by one tick */
	public static ConcurrentHashMap<Key, RTTYChannel> broadcast = new ConcurrentHashMap();
	/** New message queue for writing, gets written into readable Map later on */
	public static ConcurrentHashMap<Key, Object> newMessages = new ConcurrentHashMap();

	/** Pushes a new signal to be used next tick. Only the last signal pushed will be used. */
	public static void broadcast(Level world, String channelName, Object signal) {
		Key identifier = new Key(world, channelName);
		
		if(NumberUtils.isNumber("" + signal) && newMessages.containsKey(identifier)) {
			Object existing = newMessages.get(identifier);
			if(NumberUtils.isNumber("" + existing)) {
				try {
					long first = Long.parseLong("" + signal);
					long second = Long.parseLong("" + existing);
					newMessages.put(identifier, "" + (first + second));
					return;
				} catch(Exception ex) { }
			}
		}
		
		newMessages.put(identifier, signal);
	}
	
	/** Returns the RTTY channel with that name, or null */
	public static RTTYChannel listen(Level world, String channelName) {
		RTTYChannel channel = broadcast.get(new Key(world, channelName));
		return channel;
	}
	
	/** Moves all new messages to the broadcast map, adding the appropriate timestamp and clearing the new message queue */
	public static void updateBroadcastQueue(MinecraftServer server) {
		
		for(Entry<Key, Object> worldEntry : newMessages.entrySet()) {
			Key identifier = worldEntry.getKey();
			Object lastSignal = worldEntry.getValue();
			
			RTTYChannel channel = new RTTYChannel();
			channel.timeStamp = identifier.level().getGameTime();
			channel.signal = lastSignal;
			
			broadcast.put(identifier, channel);
		}
		
		HashMap<Key, RTTYChannel> toAdd = new HashMap();
		for(ServerLevel world : server.getAllLevels()) {
			RTTYChannel chan = new RTTYChannel();
			chan.timeStamp = world.getGameTime();
			chan.signal = getTestSender(chan.timeStamp);
			toAdd.put(new Key(world, "2012-08-06"), chan);
		}
		
		broadcast.putAll(toAdd);
		newMessages.clear();
	}
	
		public static class RTTYChannel {
		public long timeStamp = -1; //the totalWorldTime at the time of publishing, happens in the server tick event's PRE-phase. the publishing timestamp is that same number minus one
		public Object signal; // a signal can be anything, a number, an encoded string, an entire blue whale, Steve from accounting, the concept of death, 7492 hot dogs, etc.
	}

	/* Special objects for signifying specific signals to be used with RTTY machines (or telex) */
	public static enum RTTYSpecialSignal {
		BEGIN_TTY,		//start a new message block
		STOP_TTY,		//end the message block
		PRINT_BUFFER	//print message, literally, it makes a paper printout
	}
	
	/* Song of Storms at 300 BPM */
	public static Object getTestSender(long timeStamp) {

		int tempo = 4;
		int time = (int) (timeStamp % (tempo * 160));
		
		Instrument flute = Instrument.PIANO;
		Instrument accordion = Instrument.BASSGUITAR;

		if(time == tempo * 0) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).end();
		if(time == tempo * 2) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();
		if(time == tempo * 4) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();
		
		if(time == tempo * 6) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).end();
		if(time == tempo * 8) return NoteBuilder.start().add(accordion, Note.E, Octave.LOW).add(accordion, Note.G, Octave.LOW).add(accordion, Note.B, Octave.LOW).end();
		
		if(time == tempo * 12) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).end();
		if(time == tempo * 14) return NoteBuilder.start().add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).add(accordion, Note.C, Octave.MID).end();
		if(time == tempo * 16) return NoteBuilder.start().add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).add(accordion, Note.C, Octave.MID).end();
		
		if(time == tempo * 18) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).end();
		if(time == tempo * 20) return NoteBuilder.start().add(accordion, Note.E, Octave.LOW).add(accordion, Note.G, Octave.LOW).add(accordion, Note.B, Octave.LOW).end();
		
		if(time == tempo * 24) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).end();
		if(time == tempo * 26) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();
		if(time == tempo * 28) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();
		//
		if(time == tempo * 30) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).end();
		if(time == tempo * 32) return NoteBuilder.start().add(accordion, Note.E, Octave.LOW).add(accordion, Note.G, Octave.LOW).add(accordion, Note.B, Octave.LOW).end();
		
		if(time == tempo * 36) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).end();
		if(time == tempo * 38) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();
		if(time == tempo * 40) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();
		
		if(time == tempo * 42) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).end();
		if(time == tempo * 44) return NoteBuilder.start().add(accordion, Note.E, Octave.LOW).add(accordion, Note.G, Octave.LOW).add(accordion, Note.B, Octave.LOW).end();

		if(time == tempo * 48) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(flute, Note.D, Octave.LOW).end();
		if(time == tempo * 50) return NoteBuilder.start().add(flute, Note.F, Octave.LOW).end();
		if(time == tempo * 52) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).add(flute, Note.D, Octave.MID).end();
		if(time == tempo * 54) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();

		if(time == tempo * 56) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(flute, Note.D, Octave.LOW).end();
		if(time == tempo * 58) return NoteBuilder.start().add(flute, Note.F, Octave.LOW).end();
		if(time == tempo * 60) return NoteBuilder.start().add(accordion, Note.E, Octave.LOW).add(accordion, Note.G, Octave.LOW).add(accordion, Note.B, Octave.LOW).add(flute, Note.D, Octave.MID).end();

		if(time == tempo * 64) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(flute, Note.E, Octave.MID).end();
		if(time == tempo * 66) return NoteBuilder.start().add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).add(accordion, Note.C, Octave.MID).end();
		if(time == tempo * 67) return NoteBuilder.start().add(flute, Note.F, Octave.MID).end();
		if(time == tempo * 68) return NoteBuilder.start().add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).add(accordion, Note.C, Octave.MID).add(flute, Note.E, Octave.MID).end();
		if(time == tempo * 69) return NoteBuilder.start().add(flute, Note.F, Octave.MID).end();
		//
		if(time == tempo * 70) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(flute, Note.E, Octave.MID).end();
		if(time == tempo * 71) return NoteBuilder.start().add(flute, Note.B, Octave.MID).end();
		if(time == tempo * 72) return NoteBuilder.start().add(accordion, Note.E, Octave.LOW).add(accordion, Note.G, Octave.LOW).add(accordion, Note.B, Octave.LOW).add(flute, Note.A, Octave.MID).end();

		if(time == tempo * 76) return NoteBuilder.start().add(accordion, Note.G, Octave.LOW).add(flute, Note.A, Octave.MID).end();
		if(time == tempo * 78) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.B, Octave.MID).add(flute, Note.D, Octave.LOW).end();
		if(time == tempo * 80) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.B, Octave.MID).add(flute, Note.F, Octave.LOW).end();
		if(time == tempo * 81) return NoteBuilder.start().add(flute, Note.G, Octave.MID).end();

		if(time == tempo * 82) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(flute, Note.A, Octave.MID).end();
		if(time == tempo * 84) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();
		
		if(time == tempo * 88) return NoteBuilder.start().add(accordion, Note.G, Octave.LOW).add(flute, Note.A, Octave.MID).end();
		if(time == tempo * 90) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.B, Octave.MID).add(flute, Note.D, Octave.LOW).end();
		if(time == tempo * 92) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.B, Octave.MID).add(flute, Note.F, Octave.LOW).end();
		if(time == tempo * 93) return NoteBuilder.start().add(accordion, Note.B, Octave.MID).add(flute, Note.G, Octave.MID).end();

		if(time == tempo * 94) return NoteBuilder.start().add(accordion, Note.F, Octave.LOW).add(flute, Note.E, Octave.LOW).end();
		if(time == tempo * 96) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).end();

		if(time == tempo * 100) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(flute, Note.D, Octave.LOW).end();
		if(time == tempo * 101) return NoteBuilder.start().add(flute, Note.F, Octave.LOW).end();
		if(time == tempo * 102) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.B, Octave.MID).add(flute, Note.D, Octave.MID).end();
		if(time == tempo * 104) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.B, Octave.MID).end();
		//
		if(time == tempo * 106) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(flute, Note.D, Octave.LOW).end();
		if(time == tempo * 107) return NoteBuilder.start().add(flute, Note.F, Octave.LOW).end();
		if(time == tempo * 108) return NoteBuilder.start().add(accordion, Note.E, Octave.LOW).add(accordion, Note.G, Octave.LOW).add(accordion, Note.B, Octave.MID).add(flute, Note.D, Octave.MID).end();

		if(time == tempo * 112) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(flute, Note.E, Octave.MID).end();
		if(time == tempo * 114) return NoteBuilder.start().add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).add(accordion, Note.C, Octave.MID).end();
		if(time == tempo * 115) return NoteBuilder.start().add(flute, Note.F, Octave.MID).end();
		if(time == tempo * 116) return NoteBuilder.start().add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.LOW).add(accordion, Note.C, Octave.MID).add(flute, Note.E, Octave.MID).end();
		if(time == tempo * 117) return NoteBuilder.start().add(flute, Note.F, Octave.MID).end();

		if(time == tempo * 118) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(flute, Note.E, Octave.MID).end();
		if(time == tempo * 119) return NoteBuilder.start().add(flute, Note.C, Octave.MID).end();
		if(time == tempo * 120) return NoteBuilder.start().add(accordion, Note.E, Octave.LOW).add(accordion, Note.G, Octave.LOW).add(accordion, Note.B, Octave.MID).add(flute, Note.A, Octave.MID).end();

		if(time == tempo * 124) return NoteBuilder.start().add(accordion, Note.G, Octave.LOW).add(flute, Note.A, Octave.MID).end();
		if(time == tempo * 126) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.MID).add(flute, Note.D, Octave.LOW).end();
		if(time == tempo * 128) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.MID).add(flute, Note.F, Octave.LOW).end();
		if(time == tempo * 129) return NoteBuilder.start().add(flute, Note.G, Octave.MID).end();

		if(time == tempo * 130) return NoteBuilder.start().add(accordion, Note.F, Octave.LOW).add(flute, Note.A, Octave.MID).end();
		if(time == tempo * 132) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(accordion, Note.E, Octave.LOW).add(accordion, Note.A, Octave.MID).add(accordion, Note.G, Octave.LOW).end();
		if(time == tempo * 134) return NoteBuilder.start().add(flute, Note.A, Octave.MID).end();

		if(time == tempo * 136) return NoteBuilder.start().add(accordion, Note.C, Octave.LOW).add(flute, Note.D, Octave.LOW).end();
		if(time == tempo * 138) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.MID).end();
		if(time == tempo * 140) return NoteBuilder.start().add(accordion, Note.D, Octave.LOW).add(accordion, Note.F, Octave.LOW).add(accordion, Note.A, Octave.MID).end();

		
		return "";
	}
}
