package DataDefinitions;


import java.io.File;

public class DataFile extends  DataDefinition<File>
{
    private File data;

    public DataFile(String name)
    {
        super(name, "DataFile");
    }

    public File getData()
    {
        return data;
    }

    public void setData(File data)
    {
        this.data = data;
    }

    @Override
    public String toString() {
        return data.getAbsolutePath();
    }
}

