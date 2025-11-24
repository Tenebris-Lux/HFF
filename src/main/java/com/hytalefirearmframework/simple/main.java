package com.hytalefirearmframework.simple;


import java.io.IOException;

import org.w3c.dom.events.Event;

import com.hytalefirearmframework.complex.AbstractGun;

public class main {

    public class Pistol extends SimpleGun {

        @Override
        public Event shoot() {
            // TODO Auto-generated method stub
            System.out.print("pow");
            return null;
        }

        @Override
        public Event reload() {
            // TODO Auto-generated method stub
            System.out.print(this.ammoCapacity);
            return null;
        }}
    public static void main(String[] args) {
        
        try {
            Pistol pistol = AbstractGun.jsonInit("../test.json", Pistol.class);

            pistol.shoot();
            pistol.reload();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
