package anxinsign.vo;

public class ReservedPdfVO {
    private byte[] pdfData;
    private String hashValue;
    private String calendarLocalString;
    private String signatureAttr;

    public byte[] getPdfData() {
        return pdfData;
    }

    public void setPdfData(byte[] pdfData) {
        this.pdfData = pdfData;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public String getCalendarLocalString() {
        return calendarLocalString;
    }

    public void setCalendarLocalString(String calendarLocalString) {
        this.calendarLocalString = calendarLocalString;
    }

    public String getSignatureAttr() {
        return signatureAttr;
    }

    public void setSignatureAttr(String signatureAttr) {
        this.signatureAttr = signatureAttr;
    }
}
