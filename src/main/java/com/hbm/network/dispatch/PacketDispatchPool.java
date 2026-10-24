package com.hbm.network.dispatch;

import com.hbm.HbmReloaded;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

/** One server's actual source-equivalent dispatch work, not native channel or delivery statistics. */
public final class PacketDispatchPool {
    public static final String THREAD_PREFIX="NTM-Packet-Thread-";
    private final MinecraftServer server;
    private final ThreadPoolExecutor threadPool;
    private final ReentrantLock lock=new ReentrantLock();
    private final Object bookkeeping=new Object();
    private final List<FutureTask<Void>> futures=new ArrayList<>();
    private volatile boolean enabled,stopped,hasTriggered;
    private volatile int totalCnt,clearCnt;
    private volatile long nanoTimeWaited;
    private int debugHoldCount;

    public PacketDispatchPool(MinecraftServer server){
        this.server=Objects.requireNonNull(server);
        var sequence=new AtomicLong();
        threadPool=new ThreadPoolExecutor(1,1,0,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(),task->{
            var thread=new Thread(task,THREAD_PREFIX+sequence.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        });
        enabled=PacketDispatchConfig.workerDispatchEnabled();
        init();
    }

    /** The caller already froze one logical source task and its actual recipients. */
    public void prepare(Runnable groupTask){
        Objects.requireNonNull(groupTask);
        synchronized(bookkeeping){
            if(stopped)return;
            totalCnt++;
            if(enabled&&!isTriggered()){
                var future=new FutureTask<Void>(()->runLocked(groupTask),null);
                futures.add(future);
                try{threadPool.execute(future);}catch(RejectedExecutionException failure){
                    futures.remove(future);
                    future.cancel(false);
                    throw failure;
                }
                return;
            }
        }
        runLocked(groupTask);
    }

    private void runLocked(Runnable groupTask){
        boolean acquired=false;
        try{
            // Commands and synchronous fallback retain ordinary reentrant lock semantics.
            // Workers may be interrupted while waiting when this particular server stops.
            if(server.isSameThread())lock.lock();else lock.lockInterruptibly();
            acquired=true;
            if(!stopped)groupTask.run();
        }catch(InterruptedException interrupted){Thread.currentThread().interrupt();}
        finally{if(acquired)lock.unlock();}
    }

    private void init(){
        threadPool.setKeepAliveTime(50,TimeUnit.MILLISECONDS);
        if(enabled){
            int core=PacketDispatchConfig.coreWorkers(),maximum=PacketDispatchConfig.maximumWorkers();
            if(core<0||maximum<=0){
                HbmReloaded.LOGGER.error("Packet dispatch coreWorkers is negative or maximumWorkers is not positive; using one worker for both.");
                threadPool.setCorePoolSize(1);
                threadPool.setMaximumPoolSize(1);
            }else if(maximum>core){
                HbmReloaded.LOGGER.error("Packet dispatch maximumWorkers exceeds coreWorkers; using one worker for both.");
                threadPool.setCorePoolSize(1);
                threadPool.setMaximumPoolSize(1);
            }else{
                // Preserve the actual application order, including its IllegalArgumentException cases.
                threadPool.setCorePoolSize(core);
                threadPool.setMaximumPoolSize(maximum);
            }
            threadPool.allowCoreThreadTimeOut(false);
        }else{
            threadPool.allowCoreThreadTimeOut(true);
            lock.lock();
            try{
                // These are the real queued FutureTasks. FutureTask.run executes each only once,
                // even when the executor concurrently claims the same task.
                for(var task:threadPool.getQueue())task.run();
                clearThreadPoolTasks();
            }finally{lock.unlock();}
        }
    }

    /** Called at ServerTick.Post; the facade flushes touched native channels afterwards. */
    public void tickEnd(){
        long startTime=System.nanoTime();
        List<FutureTask<Void>> pending;
        synchronized(bookkeeping){pending=List.copyOf(futures);}
        try{
            if(enabled&&!PacketDispatchConfig.errorFallbackBypass()&&!hasTriggered){
                for(var future:pending){
                    nanoTimeWaited=System.nanoTime()-startTime;
                    future.get(50,TimeUnit.MILLISECONDS);
                }
            }
        }catch(ExecutionException ignored){
            // Source semantics: stop this wait pass, then perform the same final cleanup.
        }catch(TimeoutException timeout){
            if(!PacketDispatchConfig.errorFallbackBypass()&&!hasTriggered)
                HbmReloaded.LOGGER.warn("A packet has taken >50ms to process, discarding {}/{} packets to prevent pausing of main thread ({} total futures).",threadPool.getQueue().size(),totalCnt,pending.size());
            clearThreadPoolTasks();
        }catch(InterruptedException interrupted){Thread.currentThread().interrupt();}
        catch(CancellationException cancelled){if(!stopped)throw cancelled;}
        finally{
            synchronized(bookkeeping){futures.clear();}
            if(!threadPool.getQueue().isEmpty()){
                if(!PacketDispatchConfig.errorFallbackBypass()&&!hasTriggered)
                    HbmReloaded.LOGGER.warn("Residual packets in packet threading queue detected, discarding {}/{} packets.",threadPool.getQueue().size(),totalCnt);
                clearThreadPoolTasks();
            }
            totalCnt=0;
        }
    }

    private void clearThreadPoolTasks(){
        if(threadPool.getQueue().isEmpty()){clearCnt=0;return;}
        var removed=new ArrayList<Runnable>();
        threadPool.getQueue().drainTo(removed);
        for(var task:removed)if(task instanceof Future<?> future)future.cancel(false);
        if(!PacketDispatchConfig.errorFallbackBypass()&&!hasTriggered)
            HbmReloaded.LOGGER.warn("Packet work queue cleared forcefully (clear count: {}).",clearCnt);
        clearCnt++;
        if(clearCnt>5&&!isTriggered()){
            HbmReloaded.LOGGER.error("The packet work queue has been forcibly cleared more than five times. Further source dispatch work will run synchronously unless errorFallbackBypass is enabled. Use /ntmpackets resetState to reset the failure trigger.");
            hasTriggered=true;
        }
    }

    public boolean isTriggered(){return hasTriggered&&!PacketDispatchConfig.errorFallbackBypass();}
    public void resetState(){hasTriggered=false;clearCnt=0;}
    public boolean toggleThreadingStatus(){enabled=!enabled;init();return enabled;}

    public void forceLock(String actor){
        if(!server.isSameThread())throw new IllegalStateException("Packet dispatch debug locks must be acquired by the server thread");
        lock.lock();
        debugHoldCount++;
        HbmReloaded.LOGGER.error("Packet thread lock acquired by {}, this may freeze the main thread!",actor);
    }

    public void forceUnlock(String actor){
        // Do not test a flag or swallow the actual wrong-owner/unheld-lock exception.
        lock.unlock();
        if(debugHoldCount>0)debugHoldCount--;
        HbmReloaded.LOGGER.warn("Packet thread lock released by {}.",actor);
    }

    /** The facade invalidates connection epochs before this non-joining shutdown. */
    public void stop(){
        synchronized(bookkeeping){
            if(stopped)return;
            stopped=true;
            for(var future:futures)future.cancel(true);
            futures.clear();
            for(var task:threadPool.shutdownNow())if(task instanceof Future<?> future)future.cancel(false);
            totalCnt=0;
        }
        if(server.isSameThread()){
            while(debugHoldCount>0){lock.unlock();debugHoldCount--;}
        }
    }

    public void info(CommandSourceStack source){
        message(source,ChatFormatting.GOLD,"title");
        message(source,enabled&&!isTriggered()?ChatFormatting.GREEN:ChatFormatting.RED,enabled?(isTriggered()?"errored":"active"):"inactive");
        message(source,ChatFormatting.YELLOW,"pool_title");
        message(source,ChatFormatting.YELLOW,"pool_total",threadPool.getPoolSize());
        message(source,ChatFormatting.YELLOW,"pool_core",threadPool.getCorePoolSize());
        message(source,ChatFormatting.YELLOW,"pool_idle",threadPool.getPoolSize()-threadPool.getActiveCount());
        message(source,ChatFormatting.YELLOW,"pool_maximum",threadPool.getMaximumPoolSize());
        for(var thread:ManagementFactory.getThreadMXBean().dumpAllThreads(false,false)){
            if(!thread.getThreadName().startsWith(THREAD_PREFIX))continue;
            message(source,ChatFormatting.GOLD,"thread_name",thread.getThreadName());
            message(source,ChatFormatting.YELLOW,"thread_id",thread.getThreadId());
            message(source,ChatFormatting.YELLOW,"thread_state",thread.getThreadState().toString());
            // Preserve the original owner-name check followed by lock-name display.
            message(source,ChatFormatting.YELLOW,"locked_by",thread.getLockOwnerName()==null?Component.translatable("commands.hbm.packet_dispatch.none"):Component.literal(String.valueOf(thread.getLockName())));
        }
        int remaining=threadPool.getQueue().size(),total=totalCnt;
        message(source,ChatFormatting.GOLD,"packet_title");
        message(source,ChatFormatting.YELLOW,"packet_total",total);
        message(source,ChatFormatting.YELLOW,"packet_remaining",remaining);
        if(total!=0)message(source,ChatFormatting.YELLOW,"packet_percentage",round((double)remaining/total*100,2));
        message(source,ChatFormatting.YELLOW,"waited",round(TimeUnit.MILLISECONDS.convert(nanoTimeWaited,TimeUnit.NANOSECONDS),4));
    }

    private static double round(double value,int digits){return new BigDecimal(value).setScale(digits,RoundingMode.HALF_UP).doubleValue();}
    private static void message(CommandSourceStack source,ChatFormatting color,String suffix,Object... args){source.sendSystemMessage(Component.translatable("commands.hbm.packet_dispatch."+suffix,args).withStyle(color));}
}
