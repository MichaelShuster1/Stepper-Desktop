import enginemanager.Manager;
import enginemanager.Statistics;

import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
        UIapi main = new UIapi(new Manager());
        main.runSystem();
    }

}
