package org.ituns.network.core.body;

import org.ituns.network.core.temp.Body;
import org.ituns.network.core.temp.MediaType;
import org.ituns.network.core.temp.Sink;

public class MultiBody extends Body {


    @Override
    public MediaType mediaType() {
        return null;
    }

    @Override
    public void writeTo(Sink sink) {
        sink.write(this);
    }
}
