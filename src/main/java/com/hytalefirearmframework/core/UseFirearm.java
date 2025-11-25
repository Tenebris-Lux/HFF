package com.hytalefirearmframework.core;

// TODO: needs to be exchanged with the Hytale one
import org.w3c.dom.events.Event;

// TODO: some imports for Hytale here
/**
 * UseFirearm describes all actions that a user of a firearm can perform with
 * it.
 */
public interface UseFirearm {

    public FireResult trigger();

    public Event reload();
}
