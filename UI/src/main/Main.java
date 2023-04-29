package main;

import api.UIapi;
import datadefinition.Relation;
import enginemanager.Manager;
import step.Zipper;


public class Main {
    public static void main(String[] args) {

        Zipper zipper =new Zipper("Zipper",false);

        zipper.getInput(0).setData("C:\\Users\\michael\\Desktop\\test\\Folder1.zip");
        zipper.getInput(1).setData("UNZIP");

        zipper.run();

        System.out.println(zipper.getOutput(0).getData());

        //UIapi main = new UIapi(new Manager());
        //main.runSystem();
    }

}
