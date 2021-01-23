package org.ituns.network.faker;

import org.ituns.network.core.Config;
import org.ituns.network.core.Logger;

public final class FakerConfig extends Config {
    private FakerAdapter adapter;

    public FakerConfig(Builder builder) {
        super(builder.logger);
        this.adapter = builder.adapter;
    }

    public FakerAdapter adapter() {
        return adapter;
    }

    public static class Builder {
        private Logger logger;
        private FakerAdapter adapter;

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder adapter(FakerAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public FakerConfig build() {
            return new FakerConfig(this);
        }
    }
}
