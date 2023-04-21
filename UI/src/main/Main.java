package main;

import api.UIapi;
import enginemanager.Manager;


public class Main {
    public static void main(String[] args) {
        UIapi main = new UIapi(new Manager());
        main.runSystem();
    }

}
