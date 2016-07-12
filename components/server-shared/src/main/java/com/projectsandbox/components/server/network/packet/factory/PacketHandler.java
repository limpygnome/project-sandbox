package com.projectsandbox.components.server.network.packet.factory;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to declare a class is an inbound packet handler.
 *
 * When a type is decorated with this class and an inbound packet matches the main/sub-type, the singleton bean instance
 * of this type is used to handle the parsing and actions of that packet.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface PacketHandler
{
    byte mainType();
    byte subType();
}
