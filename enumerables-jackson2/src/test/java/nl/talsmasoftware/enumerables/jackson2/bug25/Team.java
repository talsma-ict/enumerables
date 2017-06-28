package nl.talsmasoftware.enumerables.jackson2.bug25;

import nl.talsmasoftware.enumerables.Enumerable;

public class Team extends Enumerable {

    public static final Team RICHARD = new Team("Richard Kroon");
    public static final Team SJOERD = new Team("Sjoerd Talsma");

    private Team(String waarde) {
        super(waarde);
    }

}
