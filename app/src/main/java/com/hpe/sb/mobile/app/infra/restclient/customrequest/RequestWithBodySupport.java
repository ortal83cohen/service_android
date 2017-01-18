package com.hpe.sb.mobile.app.infra.restclient.customrequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.hpe.sb.mobile.app.common.utils.JsonTranslation;

/**
 * Created by malikdav on 30/04/2016.
 */
public abstract class RequestWithBodySupport<T, R> extends Request <T>{

    private final int method;
    private R body;

    public RequestWithBodySupport(int method, String url, Response.ErrorListener listener) {
        this(method, null/*body*/, url, listener);
    }

    /**
     * Extending Volleys request to have a support for a request body object
     * @param method (GET/POST...)
     * @param body the request body object
     * @param url url of the request
     * @param listener error listener
     */
    public RequestWithBodySupport(int method, R body, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        this.method = method;
        this.body = body;
    }


    @Override
    public byte[] getBody() throws AuthFailureError {
        if (body == null) {
            return super.getBody();
        } else if (body.getClass().equals(String.class)) {
            return ((String) body).getBytes();
        } else {
            String jsonStringBody = JsonTranslation.object2JsonString(body);
            return jsonStringBody.getBytes();
        }
    }

    @Override
    public String getBodyContentType() {
        if (method != Method.POST && method != Method.PUT) {
            return super.getBodyContentType();
        } else if (body.getClass().equals(String.class)) {
            return "text/plain; charset=utf-8";
        } else {
            return "application/json; charset=utf-8";
        }
    }
}
