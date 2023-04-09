package DataDefinitions;

import java.util.ArrayList;
import java.util.List;

public class DataList<T> extends DataDefinition<List>
{
    List<T> data;

    public DataList(String name)
    {
        super(name,"DataList");
        data =new ArrayList<>();
    }


    public void AddElement(T element)
    {
        data.add(element);
    }


    @Override
    public String toString()
    {
        int index=1;
        String user_presentation="";
        for(T element: data)
        {
            user_presentation = user_presentation + index+".";
            user_presentation = user_presentation + element.toString() +", ";
            index++;
        }
        return user_presentation;
    }

    @Override
    public void setData(List data)
    {
        this.data=data;
    }


    @Override
    public List<T> getData()
    {
        return  data;
    }
}


