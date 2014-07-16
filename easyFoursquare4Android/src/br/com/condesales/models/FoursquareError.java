package br.com.condesales.models;

/**
 * Cr8 Event - Compubits Solutions
 * #Class description
 * Created by Felipe Conde <felipe@compubits.com>
 * On 15/07/14.
 */
public class FoursquareError {

    private long code;
    private String errorType;
    private String errorDetail;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

}
