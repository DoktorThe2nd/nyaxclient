package com.doktorthe2nd.nyax.luaj;

import com.doktorthe2nd.nyax.Consts;
import com.doktorthe2nd.nyax.luaj.loaders.LuaFromImportedLoader;
import com.doktorthe2nd.nyax.luaj.loaders.LuaFromTrustedLoader;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class Require extends OneArgFunction {
    private static final LuaFromTrustedLoader TRUSTED_LOADER = new LuaFromTrustedLoader();
    private static final LuaFromImportedLoader IMPORTED_LOADER = new LuaFromImportedLoader();

    private final boolean skip_check;
    private final List<String> allowed;
    private final ExecutableScript parent;

    public Require(ExecutableScript parent, boolean skip_check) {
        this(parent, new ArrayList<>(), skip_check);
    }
    public Require(ExecutableScript parent, List<String> allowed) {
        this(parent, allowed, false);
    }

    public Require(ExecutableScript parent, List<String> allowed, boolean skip_check) {
        this.allowed = allowed;
        this.skip_check = skip_check;
        this.parent = parent;
    }

    private static String locateAndGive(String module) {
        String path = module.replace('.', '/') + ".lua";
        InputStream trusted_fis = TRUSTED_LOADER.findResource(path);
        if (trusted_fis != null) return Consts.readInputStream(trusted_fis);
        InputStream sandbox_fis = IMPORTED_LOADER.findResource(path);
        if (sandbox_fis != null) return Consts.readInputStream(sandbox_fis);
        return null;
    }

    @Override
    public LuaValue call(LuaValue arg) {
        String module = arg.checkjstring();
        if (!skip_check && !allowed.contains(module))
            throw parent.exception("'"+module+"' is not allowed (you forgot to add it to metadata?)");
        if (ExecutableScript.cache_has_module(module))
            return ExecutableScript.from_cache(module);
        String data = locateAndGive(module);
        if (data == null)
            throw parent.exception("'"+module+"' not found");
        ExecutableScript script = ExecutableScript.of(parent.parent, new Script(module, data));
        return ExecutableScript.run_with_cache(script);
    }
}
