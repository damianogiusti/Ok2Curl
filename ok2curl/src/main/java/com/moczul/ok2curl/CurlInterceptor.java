package com.moczul.ok2curl;

import com.moczul.ok2curl.logger.Loggable;
import com.moczul.ok2curl.modifier.HeaderModifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CurlInterceptor implements Interceptor {

    private static final long DEFAULT_LIMIT = 1024L * 1024L;

    private final Loggable logger;
    private final long limit;
    private final List<HeaderModifier> headerModifiers = new ArrayList<>();

    /**
     * Interceptor responsible for printing curl logs
     *
     * Logs are pushed to stdout with 1MB limit
     *
     * @param logger output of logging
     */
    public CurlInterceptor(Loggable logger) {
        this(logger, DEFAULT_LIMIT, Collections.<HeaderModifier>emptyList());
    }

    /**
     * Interceptor responsible for printing curl logs
     *
     * @param logger output of logging
     * @param headerModifiers list of header modifiers
     */
    public CurlInterceptor(Loggable logger, List<HeaderModifier> headerModifiers) {
        this(logger, DEFAULT_LIMIT, headerModifiers);
    }

    /**
     * Interceptor responsible for printing curl logs
     *
     * @param logger output of logging
     * @param limit limit maximal bytes logged, if negative - non limited
     */
    public CurlInterceptor(Loggable logger, long limit) {
        this(logger, limit, Collections.<HeaderModifier>emptyList());
    }

    /**
     *  Interceptor responsible for printing curl logs
     * @param logger output of logging
     * @param limit limit maximal bytes logged, if negative - non limited
     * @param headerModifiers list of header modifiers
     */
    public CurlInterceptor(Loggable logger, long limit, List<HeaderModifier> headerModifiers) {
        this.logger = logger;
        this.limit = limit;
        this.headerModifiers.addAll(headerModifiers);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();

        final Request copy = request.newBuilder().build();
        final String curl = new CurlBuilder(copy, limit, headerModifiers).build();

        logger.log(curl);

        return chain.proceed(request);
    }
}
