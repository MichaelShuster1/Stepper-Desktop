package DataDefinitions;

import java.util.ArrayList;
import java.util.List;

public class DataList<T> extends DataDefinition
{
    List<T> list;

    public DataList(String name,String typeStream ,boolean mandatory)
    {
        super(name,"DataList",typeStream,mandatory,false);
        list=new ArrayList<>();
    }


    public void AddElement(T element)
    {
        list.add(element);
    }


    @Override
    public String toString()
    {
        int index=1;
        String user_presentation="";
        for(T element:list)
        {
            user_presentation = user_presentation + index+".";
            user_presentation = user_presentation + element.toString() +", ";
            index++;
        }
        return user_presentation;
    }
}
