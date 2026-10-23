package com.hbm.client.pwrprinter;

import com.hbm.item.pwrprinter.*;
import java.util.*;
import net.minecraft.client.Minecraft;

/** Source opens only for client players still holding the genuine printer, after the complete atomic snapshot. */
public final class PwrPrinterClient {
    private static final Map<UUID,Pending> PENDING=new HashMap<>();
    public static void receive(PwrPrinterPackets.SliceData packet){
        var client=Minecraft.getInstance();long now=System.currentTimeMillis();PENDING.values().removeIf(pending->now-pending.created>30000);
        if(client.level==null||client.player==null||!client.player.getMainHandItem().is(PwrPrinterContent.ITEM.get()))return;
        var job=PENDING.computeIfAbsent(packet.job(),key->new Pending(packet,now));
        if(!job.header.min().equals(packet.min())||!job.header.max().equals(packet.max())||job.header.parts()!=packet.parts()||job.header.direction()!=packet.direction()){PENDING.remove(packet.job());return;}
        job.fragments.put(packet.part(),packet.cells());if(job.fragments.size()!=packet.parts())return;
        var cells=new ArrayList<PwrPrintSnapshot.Cell>();for(int i=0;i<packet.parts();i++)cells.addAll(job.fragments.get(i));PENDING.remove(packet.job());
        client.setScreen(new PwrSliceScreen(new PwrPrintSnapshot(packet.min(),packet.max(),packet.direction(),cells)));
    }
    private static final class Pending{final PwrPrinterPackets.SliceData header;final long created;final Map<Integer,List<PwrPrintSnapshot.Cell>> fragments=new HashMap<>();Pending(PwrPrinterPackets.SliceData header,long created){this.header=header;this.created=created;}}
    private PwrPrinterClient(){}
}
