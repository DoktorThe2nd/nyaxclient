package com.doktorthe2nd.nyax.luaj;

public class Script {
    public final String data;
    public final String module_id;
    public final Metadata meta;

    public Script(String module, String data) {
        this.data = data;
        this.meta = Metadata.gather(data);
        this.module_id = module;
    }
}
