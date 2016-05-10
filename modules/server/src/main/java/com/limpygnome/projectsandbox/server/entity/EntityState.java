package com.limpygnome.projectsandbox.server.entity;

/**
* State changes in regards to the entity's slotState in the world.
*/
public enum EntityState
{
    /**
    * Indicates no change to entity.
    */
   NONE(false),

   /**
    * Indicates a world update needs to be sent out before the entity
    * can be deleted.
    */
   PENDING_DELETED(true),

   /**
    * Indicates the entity can now be deleted.
    */
   DELETED(true),

   /**
    * Indicates the entity's slotState has been updated.
    */
   UPDATED(false),

   /**
    * Indicates the entity has been created
    */
    CREATED(true)

    ;

    public final boolean GLOBAL_STATE;

    EntityState(boolean GLOBAL_STATE)
    {
        this.GLOBAL_STATE = GLOBAL_STATE;
    }
}
