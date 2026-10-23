package com.hbm.world.structure.meteor;

import java.util.ArrayList;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/** Source connector choice only; vanilla still joins, positions and creates every piece. */
public final class MeteorConnectorPolicy {
    public record Selection(StructureTemplate.StructureBlockInfo connector,Rotation rotation) {
        public StructureTemplate.StructureBlockInfo transformed(BlockPos origin) {
            return new StructureTemplate.StructureBlockInfo(connector.pos().rotate(rotation).offset(origin),connector.state().rotate(rotation),connector.nbt());
        }
    }

    private static List<StructureTemplate.StructureBlockInfo> connectors(StructurePoolElement element,StructureTemplateManager manager,BlockPos origin,Rotation rotation) {
        if(!(element instanceof MeteorTemplateAccess access))throw new IllegalStateException("Meteor source pool must contain real vanilla single templates");
        var result=new ArrayList<>(access.hbm$meteorTemplate(manager).filterBlocks(origin,new StructurePlaceSettings().setRotation(rotation),Blocks.JIGSAW,true));
        result.sort(Comparator.comparingInt(info->info.nbt()==null?0:info.nbt().getInt("hbm_source_order")));
        return result;
    }

    public static List<StructureTemplate.StructureBlockInfo> outgoing(PoolElementStructurePiece parent,StructureTemplateManager manager,RandomSource random,Consumer<StructureTemplate.StructureBlockInfo> entered) {
        var values=connectors(parent.getElement(),manager,parent.getPosition(),parent.getRotation());
        values.sort(Comparator.comparingInt((StructureTemplate.StructureBlockInfo info)->info.nbt()==null?0:info.nbt().getInt("selection_priority")).reversed());
        var entrance=MeteorJigsawLimits.entrance(parent);
        int count=values.size()-(int)values.stream().filter(info->info.pos().equals(entrance)).count();
        return new AbstractList<>() {
            private final List<StructureTemplate.StructureBlockInfo> prepared=new ArrayList<>();
            private int first;
            @Override public int size(){return count;}
            @Override public StructureTemplate.StructureBlockInfo get(int index){
                if(index<0||index>=count)throw new IndexOutOfBoundsException(index);
                while(prepared.size()<=index) {
                    int end=first+1,priority=values.get(first).nbt().getInt("selection_priority");
                    while(end<values.size()&&values.get(end).nbt().getInt("selection_priority")==priority)end++;
                    //Do not spend the next group's shuffle draws before this group's children.
                    var group=new ArrayList<>(values.subList(first,end));
                    for(int size=group.size();size>1;size--) {
                        int selected=random.nextInt(size),last=size-1;
                        var previous=group.set(last,group.get(selected));group.set(selected,previous);
                    }
                    //The source skips its used entrance only after shuffling that group.
                    group.removeIf(info->info.pos().equals(entrance));prepared.addAll(group);first=end;
                }
                var info=prepared.get(index);entered.accept(info);return info;
            }
        };
    }

    public static Selection incoming(StructureTemplate.StructureBlockInfo from,Rotation parentRotation,StructurePoolElement candidate,StructureTemplateManager manager,RandomSource random) {
        var direction=JigsawBlock.getFrontFacing(from.state());
        var target=from.nbt().getString("target");
        var options=new ArrayList<StructureTemplate.StructureBlockInfo>();
        for(var info:connectors(candidate,manager,BlockPos.ZERO,Rotation.NONE)) {
            if(info.nbt()==null||!target.equals(info.nbt().getString("name")))continue;
            var incoming=JigsawBlock.getFrontFacing(info.state());
            boolean compatible=direction.getAxis()==Direction.Axis.Y?incoming==direction.getOpposite():incoming.getAxis()!=Direction.Axis.Y;
            if(compatible)options.add(info);
        }
        if(options.isEmpty())return null;
        var selected=options.get(random.nextInt(options.size()));
        Rotation rotation;
        if(direction.getAxis()==Direction.Axis.Y) {
            rotation="rollable".equals(from.nbt().getString("joint"))?Rotation.values()[random.nextInt(4)]:parentRotation;
        } else {
            var incoming=JigsawBlock.getFrontFacing(selected.state());
            rotation=Rotation.NONE;
            for(var value:Rotation.values())if(value.rotate(incoming)==direction.getOpposite()){rotation=value;break;}
        }
        return new Selection(selected,rotation);
    }
    private MeteorConnectorPolicy(){}
}
