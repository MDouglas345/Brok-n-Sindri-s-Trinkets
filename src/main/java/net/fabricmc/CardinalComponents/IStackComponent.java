package net.fabricmc.CardinalComponents;

import dev.onyxstudios.cca.api.v3.component.*;

public interface IStackComponent<T> extends Component{
    public void push(T obj);

    public T pop();

    public void remove(int id);

    public T peek();

    public T peek(int id);
}
