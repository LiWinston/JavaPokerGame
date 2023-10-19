import java.util.List;

public interface IObserverable  {
    public void addObserver(IObserver o);
    public void removeObserver(IObserver o);
    public void notifyObservers();
}
