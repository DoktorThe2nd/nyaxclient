package com.doktorthe2nd.min.luaj;

import com.doktorthe2nd.min.luaj.loaders.LuaFromAssetsLoader;
import com.doktorthe2nd.min.luaj.loaders.LuaFromImportedLoader;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * Thread for Luaj engine.
 * All present functions are thread-safe.
 * This is the only correct way to interact with Luaj from anywhere else.
 */
public class LuajThread {
    private final LuajEngine engine;
    private final Thread thread;

    public LuajThread(ScriptAPI api) {
        engine = new LuajEngine(api);
        thread = new Thread(() -> {
            try {
                engine.loop();
            } catch (LuaException e) {
                LuajErrorScreen.set(e.getMessage());
            }
        });
        thread.setDaemon(true);
    }
    
    public void start() {
        engine.setRunning(true);
        thread.start();
    }

    public void interrupt() {
        engine.setRunning(false);
        thread.interrupt();
    }

    public void callEvent(String event_name, Varargs args) {
        engine.push_event(event_name, args);
    }

    public void addEventSubscriber(String event_name, LuaFunction function) {
        engine.add_subscriber(event_name, function);
    }

    public void loadScripts() {
        LuaFromAssetsLoader assetsLoader = new LuaFromAssetsLoader();
        LuaFromImportedLoader importedLoader = new LuaFromImportedLoader();

        List<String> assets_paths = assetsLoader.walk();
        List<String> imported_paths = importedLoader.walk();

        for (String path : assets_paths) {
            System.out.println("Built-in script loading: "+path);
            String module = path.replace('/', '.');
            if (module.endsWith(".lua")) module = module.substring(0, module.length()-4);
            System.out.println("corresponds to module '"+module+"'");
            Events.runModule(module);
        }
        for (String path : imported_paths) {
            System.out.println("Imported script loading: "+path);
            String module = path.replace('/', '.');
            if (module.endsWith(".lua")) module = module.substring(0, module.length()-4);
            System.out.println("corresponds to module '"+module+"'");
            Events.runModule(module);
        }
    }
}
