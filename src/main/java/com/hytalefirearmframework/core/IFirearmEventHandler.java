package com.hytalefirearmframework.core;

public interface IFirearmEventHandler {

    void onReloadStart(AbstractGun gun);

    void onReloadComplete(AbstractGun gun);

    void onAmmoChanged(AbstractGun gun, int newAmount);
}
