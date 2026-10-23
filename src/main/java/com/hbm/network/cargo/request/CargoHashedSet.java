package com.hbm.network.cargo.request;
import java.util.*;
import net.minecraft.core.BlockPos;
/** Original HashedSet replaces by hash alone (including collisions); not a regular HashSet. */
public final class CargoHashedSet<T> extends AbstractSet<T> {
 private final HashMap<Integer,T> map=new HashMap<>();public CargoHashedSet(){}public CargoHashedSet(Set<? extends T> source){addAll(source);}public HashMap<Integer,T> getMap(){return map;}
 public static int positionHash(BlockPos p){return(p.getY()+p.getZ()*27644437)*27644437+p.getX();}private static int hash(Object o){return o instanceof BlockPos p?positionHash(p):o.hashCode();}
 @Override public boolean add(T value){boolean contains=contains(value);map.put(hash(value),value);return contains;}@Override public boolean contains(Object value){return map.containsKey(hash(value));}@Override public boolean remove(Object value){return map.remove(hash(value))!=null;}@Override public Iterator<T> iterator(){return map.values().iterator();}@Override public int size(){return map.size();}@Override public void clear(){map.clear();}
 @Override public boolean addAll(Collection<? extends T> values){boolean result=false;for(T value:values)if(add(value))result=true;return result;}@Override public boolean removeAll(Collection<?> values){return false;}@Override public boolean retainAll(Collection<?> values){throw new UnsupportedOperationException("Original HashedSet does not implement retainAll");}@Override public Object[] toArray(){throw new UnsupportedOperationException("Original HashedSet does not implement toArray");}@Override public <E>E[] toArray(E[] target){throw new UnsupportedOperationException("Original HashedSet does not implement toArray");}
}
