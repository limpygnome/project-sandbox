package com.projectsandbox.components.server.entity;

/**
* State changes in regards to the entity's slotState in the world.
*/
public enum EntityState
{
    /**
     * Indicates no change to entity.
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
     * Indicates the entity's slotState has been updated.
     */
    UPDATED,

    /**
     * Indicates the entity has been created
     */
    CREATED

}
