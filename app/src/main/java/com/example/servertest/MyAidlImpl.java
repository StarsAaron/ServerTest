package com.example.servertest;

import android.os.RemoteException;

/**
 * Created by aaron on 16-6-11.
 */
public class MyAidlImpl extends IMyAidlInterface.Stub{
    @Override
    public int plus(int a, int b) throws RemoteException {
        return a+b;
    }

    @Override
    public String toUpperCase(String str) throws RemoteException {
        return str.toUpperCase();
    }
}
