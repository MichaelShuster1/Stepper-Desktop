package main;

import api.UIapi;
import enginemanager.Manager;

import java.util.ArrayList;
import java.util.List;



public class Main {
    public static void main(String[] args) {
        UIapi main = new UIapi(new Manager());
        main.runSystem();
    }


}
