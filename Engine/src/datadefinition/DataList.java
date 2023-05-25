package datadefinition;

import java.util.ArrayList;
import java.util.List;

public class DataList<T> extends DataDefinition<List<T>> {
    List<T> data;

    public DataList(String name) {
        super(name, "DataList");
    }


    public void AddElement(T element) {
        data.add(element);
    }


    @Override
    public String toString() {
        int index = 1;
        String user_presentation = "";
        if(data == null)
            return "[ ]";
        else if (data.size() == 0)
            return "[ ]";
        for (T element : data) {
            user_presentation = user_presentation + index + ".";
            user_presentation = user_presentation + element.toString() + "\n";
            index++;
        }
        return user_presentation;
    }

    @Override
    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public List<T> getData() {
        return data;
    }
}


