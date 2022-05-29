package net.fabricmc.CardinalComponents;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface IHashMapComponent<K,V> extends Component{

    public int Push(K key, V value);

    public V Pop(K key);

    public void Remove(K key, int id);

    public V Peek(K key);

    public V Peek(K key, int id);

    public void Reset();
    
}
