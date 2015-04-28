package com.limpygnome.projectsandbox.server.ents.enums;

/**
* State changes in regards to the entity's state in the world.
*/
public enum StateChange
{
    /**
    * Indicates no change to ent state.
    */
   NONE,
   /**
    * Indicates a world update needs to be sent out before the entity
    * can be deleted.
    */
   PENDING_DELETED,
   /**
    * Indicates the entity can now be deleted.
    */
   DELETED,
   /**
    * Indicates the entity's state has been updated.
    */
   UPDATED,
   /**
    * Indicates the entity has been created
    */
   CREATED
}
