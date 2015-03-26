package com.limpygnome.projectsandbox.ents;

/**
 * The faction / team of an entity.
 * 
 * @author limpygnome
 */
public enum Faction
{
    NONE((short) 0)
    ;

    public final short ID;
    private Faction(short ID)
    {
        this.ID = ID;
    }
    
    public static Faction getById(short ID)
    {
        switch (ID)
        {
            default:
                // TODO: change with log4j
                System.err.println("failed to find faction ID " + ID);
            case 0:
                return NONE;
        }
    }
}