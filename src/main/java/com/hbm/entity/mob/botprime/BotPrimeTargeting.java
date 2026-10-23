package com.hbm.entity.mob.botprime;
import java.util.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
/** Source EntityAITasks3tick scheduler and NearestAttackableTargetNT's unusual resetTask assignment,not a modern nearest-player goal substitution. */
final class BotPrimeTargeting {
 private final BotPrimeBase owner;private final List<Task> tasks=new ArrayList<>();private int ticks,revengeTimestamp;
 BotPrimeTargeting(BotPrimeBase owner){this.owner=owner;}
 void tick(boolean head){if(tasks.isEmpty()){if(head)tasks.add(new Revenge());tasks.add(new Nearest());}if(ticks++%3==0){for(var task:tasks){if(task.running&&(!usable(task)||!task.keep())){task.stop();task.running=false;}if(!task.running&&usable(task)&&task.acquire()){task.start();task.running=true;}}}else for(var task:tasks)if(task.running&&!task.keep()){task.stop();task.running=false;}}
 private boolean usable(Task task){for(var other:tasks){if(other==task)continue;if(task.priority()>=other.priority()&&other.running)return false;}return true;}
 private boolean suitable(LivingEntity target,boolean invulnerable){return target!=null&&target!=owner&&target.isAlive()&&(!(target instanceof Player player)||!player.isSpectator()&&(invulnerable||!player.getAbilities().invulnerable));}
 private boolean continuing(double distance){var t=owner.getTarget();return suitable(t,false)&&owner.distanceToSqr(t)<=distance*distance;}
 private abstract class Task {boolean running;abstract int priority();abstract boolean acquire();abstract boolean keep();void start(){}void stop(){owner.setTarget(null);}}
 private final class Revenge extends Task {LivingEntity selected;@Override int priority(){return 1;}@Override boolean acquire(){selected=owner.getLastHurtByMob();return owner.getLastHurtByMobTimestamp()!=revengeTimestamp&&suitable(selected,true);}@Override boolean keep(){return continuing(16);}@Override void start(){owner.setTarget(selected);revengeTimestamp=owner.getLastHurtByMobTimestamp();}}
 private final class Nearest extends Task {LivingEntity selected;@Override int priority(){return 2;}@Override boolean acquire(){var players=owner.level().getEntitiesOfClass(Player.class,owner.getBoundingBox().inflate(128),p->suitable(p,false));players.sort(Comparator.comparingDouble(owner::distanceToSqr));selected=players.isEmpty()?null:players.getFirst();return selected!=null;}@Override boolean keep(){return continuing(128);}@Override void stop(){owner.setTarget(selected);}}
}
