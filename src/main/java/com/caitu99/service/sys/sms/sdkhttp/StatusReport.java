/**
 * StatusReport.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.caitu99.service.sys.sms.sdkhttp;

public class StatusReport  implements java.io.Serializable {
    private String errorCode;

    private String memo;

    private String mobile;

    private String receiveDate;

    private int reportStatus;

    private long seqID;

    private String serviceCodeAdd;

    private String submitDate;

    public StatusReport() {
    }

    public StatusReport(
           String errorCode,
           String memo,
           String mobile,
           String receiveDate,
           int reportStatus,
           long seqID,
           String serviceCodeAdd,
           String submitDate) {
           this.errorCode = errorCode;
           this.memo = memo;
           this.mobile = mobile;
           this.receiveDate = receiveDate;
           this.reportStatus = reportStatus;
           this.seqID = seqID;
           this.serviceCodeAdd = serviceCodeAdd;
           this.submitDate = submitDate;
    }


    /**
     * Gets the errorCode value for this StatusReport.
     *
     * @return errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this StatusReport.
     *
     * @param errorCode
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the memo value for this StatusReport.
     *
     * @return memo
     */
    public String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this StatusReport.
     *
     * @param memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }


    /**
     * Gets the mobile value for this StatusReport.
     *
     * @return mobile
     */
    public String getMobile() {
        return mobile;
    }


    /**
     * Sets the mobile value for this StatusReport.
     *
     * @param mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    /**
     * Gets the receiveDate value for this StatusReport.
     *
     * @return receiveDate
     */
    public String getReceiveDate() {
        return receiveDate;
    }


    /**
     * Sets the receiveDate value for this StatusReport.
     *
     * @param receiveDate
     */
    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }


    /**
     * Gets the reportStatus value for this StatusReport.
     *
     * @return reportStatus
     */
    public int getReportStatus() {
        return reportStatus;
    }


    /**
     * Sets the reportStatus value for this StatusReport.
     *
     * @param reportStatus
     */
    public void setReportStatus(int reportStatus) {
        this.reportStatus = reportStatus;
    }


    /**
     * Gets the seqID value for this StatusReport.
     *
     * @return seqID
     */
    public long getSeqID() {
        return seqID;
    }


    /**
     * Sets the seqID value for this StatusReport.
     *
     * @param seqID
     */
    public void setSeqID(long seqID) {
        this.seqID = seqID;
    }


    /**
     * Gets the serviceCodeAdd value for this StatusReport.
     *
     * @return serviceCodeAdd
     */
    public String getServiceCodeAdd() {
        return serviceCodeAdd;
    }


    /**
     * Sets the serviceCodeAdd value for this StatusReport.
     *
     * @param serviceCodeAdd
     */
    public void setServiceCodeAdd(String serviceCodeAdd) {
        this.serviceCodeAdd = serviceCodeAdd;
    }


    /**
     * Gets the submitDate value for this StatusReport.
     *
     * @return submitDate
     */
    public String getSubmitDate() {
        return submitDate;
    }


    /**
     * Sets the submitDate value for this StatusReport.
     *
     * @param submitDate
     */
    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof StatusReport)) return false;
        StatusReport other = (StatusReport) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.errorCode==null && other.getErrorCode()==null) ||
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.memo==null && other.getMemo()==null) ||
             (this.memo!=null &&
              this.memo.equals(other.getMemo()))) &&
            ((this.mobile==null && other.getMobile()==null) ||
             (this.mobile!=null &&
              this.mobile.equals(other.getMobile()))) &&
            ((this.receiveDate==null && other.getReceiveDate()==null) ||
             (this.receiveDate!=null &&
              this.receiveDate.equals(other.getReceiveDate()))) &&
            this.reportStatus == other.getReportStatus() &&
            this.seqID == other.getSeqID() &&
            ((this.serviceCodeAdd==null && other.getServiceCodeAdd()==null) ||
             (this.serviceCodeAdd!=null &&
              this.serviceCodeAdd.equals(other.getServiceCodeAdd()))) &&
            ((this.submitDate==null && other.getSubmitDate()==null) ||
             (this.submitDate!=null &&
              this.submitDate.equals(other.getSubmitDate())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getMemo() != null) {
            _hashCode += getMemo().hashCode();
        }
        if (getMobile() != null) {
            _hashCode += getMobile().hashCode();
        }
        if (getReceiveDate() != null) {
            _hashCode += getReceiveDate().hashCode();
        }
        _hashCode += getReportStatus();
        _hashCode += new Long(getSeqID()).hashCode();
        if (getServiceCodeAdd() != null) {
            _hashCode += getServiceCodeAdd().hashCode();
        }
        if (getSubmitDate() != null) {
            _hashCode += getSubmitDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StatusReport.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://sdkhttp.eucp.b2m.cn/", "statusReport"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("memo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobile");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mobile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("receiveDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "receiveDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reportStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reportStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("seqID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "seqID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceCodeAdd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serviceCodeAdd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("submitDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "submitDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
