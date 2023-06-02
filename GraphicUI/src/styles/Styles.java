package styles;


import java.util.ArrayList;
import java.util.List;

public enum Styles {
    DEFAULT("DEFAULT"),
    DARK("DARK"),
    MIDNIGHT("MIDNIGHT");

    private final String style;

    Styles(String style)
    {
        this.style=style;
    }

    @Override
    public String toString(){
        return style;
    }

    public static List<String> getStyles()
    {
        List<String> styles=new ArrayList<>();


        for(Styles style:Styles.values())
            styles.add(style.toString());

        return styles;
    }

}
