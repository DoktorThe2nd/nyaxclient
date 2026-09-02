package com.doktorthe2nd.nyax.luaj.loaders;

import com.doktorthe2nd.nyax.Consts;
import com.doktorthe2nd.nyax.MainActivity;

import org.luaj.vm2.lib.ResourceFinder;

import java.io.IOException;
import java.io.InputStream;

public class LuaFromTrustedLoader implements ResourceFinder {
    @Override
    public InputStream findResource(String filename) {
        try {
            return MainActivity.appContext.getAssets().open(Consts.luaBuiltInModules +"/"+filename);
        } catch (IOException e) {
            return null;
        }
    }
}
