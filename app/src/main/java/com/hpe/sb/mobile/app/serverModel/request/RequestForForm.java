package com.hpe.sb.mobile.app.serverModel.request;

import com.hpe.sb.mobile.app.serverModel.form.Form;

public class RequestForForm {

    public FullRequest request;
    public Form form;

    public RequestForForm() {
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public FullRequest getRequest() {
        return request;
    }

    public void setRequest(FullRequest request) {
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestForForm that = (RequestForForm) o;

        if (request != null ? !request.equals(that.request) : that.request != null) return false;
        return form != null ? form.equals(that.form) : that.form == null;

    }

    @Override
    public int hashCode() {
        int result = request != null ? request.hashCode() : 0;
        result = 31 * result + (form != null ? form.hashCode() : 0);
        return result;
    }
}
