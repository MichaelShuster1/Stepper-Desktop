package DataDefinitions;

import java.util.ArrayList;
import java.util.List;

public class DataList extends DataDefinition<List<DataDefinition>>
{
    List<DataDefinition> data;

    public DataList(String name)
    {
        super(name,"DataList");
        data =new ArrayList<>();
    }


    public void AddElement(DataDefinition element)
    {
        data.add(element);
    }


    @Override
    public String toString()
    {
        int index=1;
        String user_presentation="";
        for(DataDefinition element: data)
        {
            user_presentation = user_presentation + index+".";
            user_presentation = user_presentation + element.toString() +", ";
            index++;
        }
        return user_presentation;
    }

    @Override
    public void setData(List<DataDefinition> data)
    {
        this.data=data;
    }

    @Override
    public List<DataDefinition> getData()
    {
        return  data;
    }
}


