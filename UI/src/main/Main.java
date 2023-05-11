package main;

import api.UIapi;
import datadefinition.Relation;
import enginemanager.Manager;
import step.CommandLine;
import step.Zipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;



public class Main {
    public static void main(String[] args) {


        /*
        Zipper zipper =new Zipper("Zipper",false);

        zipper.getInput(0).setData("C:\\Users\\michael\\Desktop\\test\\Folder1.zip");
        zipper.getInput(1).setData("UNZIP");

        zipper.run();

        System.out.println(zipper.getOutput(0).getData());
        */
/*
        CommandLine commandLine= new CommandLine("CommandLine",false);

        commandLine.getInput(0).setData("dir");
        commandLine.getInput(1).setData("C:\\");

        commandLine.run();
        System.out.println(commandLine.getOutput(0).getData());
        System.out.println("-------------------------");
        System.out.println(commandLine.getLog());
        System.out.println("-------------------------");
        System.out.println(commandLine.getStateAfterRun());
        System.out.println("-------------------------");
        System.out.println(commandLine.getRunTime());
*/

        UIapi main = new UIapi(new Manager());
        main.runSystem();
    }


}
