package Steps;

import DataDefinitions.*;

public class FilesRenamer extends Step
{


    public FilesRenamer(String name)
    {
        super(name, false);

        DataList dataList=new DataList("FILES_TO_RENAME");
        inputs.add(new Input(dataList,false,true));

        DataString dataString=new DataString("PREFIX");
        inputs.add(new Input(dataString,true,false));

        dataString=new DataString("SUFFIX");
        inputs.add(new Input(dataString,true,false));

        DataRelation dataRelation = new DataRelation("RENAME_RESULT");
        outputs.add(new Output(dataRelation));
    }



    @Override
    public void Run() {

    }
}
