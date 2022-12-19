package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque{
    public MaxArrayDeque(Comparator<T> c){
        this.c=c;
    }
    public T max(){
        return max(c);
    }
    public T max(Comparator<T> c){
        Iterator<T> t=this.iterator();
        T ans=null;
        if(t.hasNext()){
            ans=t.next();
        }
        while(t.hasNext()){
            T temp=t.next();
            if(c.compare(ans,temp)<0){
                ans=temp;
            }
        }
        return ans;
    }
    private Comparator<T> c;
}
