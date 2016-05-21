package com.limpygnome.projectsandbox.server.entity;

/**
* Used to indicate which params have been updated. Avoids sending lots of
* unchanged data.
*/
public enum UpdateMasks
{

   /**
    * Indicates X of position has been updated.
    */
   X(1),

   /**
    * Indicates Y of position has been updated.
    */
   Y(2),

   /**
    * The rotation has been updated.
    */
   ROTATION(4),

   /**
    * The health of the player has been updated.
    */
   HEALTH(8),

   /**
    * All of the masks combined.
    */
   ALL_MASKS(1+2+4+8)
   ;

   public final int MASK;

   UpdateMasks(int MASK)
   {
       this.MASK = MASK;
   }

}
