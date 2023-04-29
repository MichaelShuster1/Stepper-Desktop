package main;

import api.UIapi;
import datadefinition.Relation;
import enginemanager.Manager;
import step.Zipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Main {
    public static void main(String[] args) {
        Zipper step = new Zipper("name",false);
        step.getInput(0).setData("C:\\Users\\Igal\\Desktop\\New folder\\first\\test.txt");
        step.getInput(1).setData("ZIP");
        step.run();
        UIapi main = new UIapi(new Manager());
        main.runSystem();
    }


}
