/**
 * OrderItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class OrderItem  implements java.io.Serializable {
    private int number;

    private java.lang.Long orderid;

    private java.lang.String ordercdforchannel;

    private java.lang.String roomno;

    private java.lang.String night;

    private java.lang.String guests;

    private java.lang.Integer noteresult;

    private java.lang.String orderstate;

    private java.lang.String roomState;

    private java.lang.String quitdate;

    private java.lang.Float baseprice;

    private java.lang.Float saleprice;

    private java.lang.Float baserate;

    private java.lang.String specialnote;

    private java.lang.Float totalcharges;

    private java.lang.String createtime;

    private java.lang.String modifiedtime;

    public OrderItem() {
    }

    public OrderItem(
           int number,
           java.lang.Long orderid,
           java.lang.String ordercdforchannel,
           java.lang.String roomno,
           java.lang.String night,
           java.lang.String guests,
           java.lang.Integer noteresult,
           java.lang.String orderstate,
           java.lang.String roomState,
           java.lang.String quitdate,
           java.lang.Float baseprice,
           java.lang.Float saleprice,
           java.lang.Float baserate,
           java.lang.String specialnote,
           java.lang.Float totalcharges,
           java.lang.String createtime,
           java.lang.String modifiedtime) {
           this.number = number;
           this.orderid = orderid;
           this.ordercdforchannel = ordercdforchannel;
           this.roomno = roomno;
           this.night = night;
           this.guests = guests;
           this.noteresult = noteresult;
           this.orderstate = orderstate;
           this.roomState = roomState;
           this.quitdate = quitdate;
           this.baseprice = baseprice;
           this.saleprice = saleprice;
           this.baserate = baserate;
           this.specialnote = specialnote;
           this.totalcharges = totalcharges;
           this.createtime = createtime;
           this.modifiedtime = modifiedtime;
    }


    /**
     * Gets the number value for this OrderItem.
     * 
     * @return number
     */
    public int getNumber() {
        return number;
    }


    /**
     * Sets the number value for this OrderItem.
     * 
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
    }


    /**
     * Gets the orderid value for this OrderItem.
     * 
     * @return orderid
     */
    public java.lang.Long getOrderid() {
        return orderid;
    }


    /**
     * Sets the orderid value for this OrderItem.
     * 
     * @param orderid
     */
    public void setOrderid(java.lang.Long orderid) {
        this.orderid = orderid;
    }


    /**
     * Gets the ordercdforchannel value for this OrderItem.
     * 
     * @return ordercdforchannel
     */
    public java.lang.String getOrdercdforchannel() {
        return ordercdforchannel;
    }


    /**
     * Sets the ordercdforchannel value for this OrderItem.
     * 
     * @param ordercdforchannel
     */
    public void setOrdercdforchannel(java.lang.String ordercdforchannel) {
        this.ordercdforchannel = ordercdforchannel;
    }


    /**
     * Gets the roomno value for this OrderItem.
     * 
     * @return roomno
     */
    public java.lang.String getRoomno() {
        return roomno;
    }


    /**
     * Sets the roomno value for this OrderItem.
     * 
     * @param roomno
     */
    public void setRoomno(java.lang.String roomno) {
        this.roomno = roomno;
    }


    /**
     * Gets the night value for this OrderItem.
     * 
     * @return night
     */
    public java.lang.String getNight() {
        return night;
    }


    /**
     * Sets the night value for this OrderItem.
     * 
     * @param night
     */
    public void setNight(java.lang.String night) {
        this.night = night;
    }


    /**
     * Gets the guests value for this OrderItem.
     * 
     * @return guests
     */
    public java.lang.String getGuests() {
        return guests;
    }


    /**
     * Sets the guests value for this OrderItem.
     * 
     * @param guests
     */
    public void setGuests(java.lang.String guests) {
        this.guests = guests;
    }


    /**
     * Gets the noteresult value for this OrderItem.
     * 
     * @return noteresult
     */
    public java.lang.Integer getNoteresult() {
        return noteresult;
    }


    /**
     * Sets the noteresult value for this OrderItem.
     * 
     * @param noteresult
     */
    public void setNoteresult(java.lang.Integer noteresult) {
        this.noteresult = noteresult;
    }


    /**
     * Gets the orderstate value for this OrderItem.
     * 
     * @return orderstate
     */
    public java.lang.String getOrderstate() {
        return orderstate;
    }


    /**
     * Sets the orderstate value for this OrderItem.
     * 
     * @param orderstate
     */
    public void setOrderstate(java.lang.String orderstate) {
        this.orderstate = orderstate;
    }


    /**
     * Gets the roomState value for this OrderItem.
     * 
     * @return roomState
     */
    public java.lang.String getRoomState() {
        return roomState;
    }


    /**
     * Sets the roomState value for this OrderItem.
     * 
     * @param roomState
     */
    public void setRoomState(java.lang.String roomState) {
        this.roomState = roomState;
    }


    /**
     * Gets the quitdate value for this OrderItem.
     * 
     * @return quitdate
     */
    public java.lang.String getQuitdate() {
        return quitdate;
    }


    /**
     * Sets the quitdate value for this OrderItem.
     * 
     * @param quitdate
     */
    public void setQuitdate(java.lang.String quitdate) {
        this.quitdate = quitdate;
    }


    /**
     * Gets the baseprice value for this OrderItem.
     * 
     * @return baseprice
     */
    public java.lang.Float getBaseprice() {
        return baseprice;
    }


    /**
     * Sets the baseprice value for this OrderItem.
     * 
     * @param baseprice
     */
    public void setBaseprice(java.lang.Float baseprice) {
        this.baseprice = baseprice;
    }


    /**
     * Gets the saleprice value for this OrderItem.
     * 
     * @return saleprice
     */
    public java.lang.Float getSaleprice() {
        return saleprice;
    }


    /**
     * Sets the saleprice value for this OrderItem.
     * 
     * @param saleprice
     */
    public void setSaleprice(java.lang.Float saleprice) {
        this.saleprice = saleprice;
    }


    /**
     * Gets the baserate value for this OrderItem.
     * 
     * @return baserate
     */
    public java.lang.Float getBaserate() {
        return baserate;
    }


    /**
     * Sets the baserate value for this OrderItem.
     * 
     * @param baserate
     */
    public void setBaserate(java.lang.Float baserate) {
        this.baserate = baserate;
    }


    /**
     * Gets the specialnote value for this OrderItem.
     * 
     * @return specialnote
     */
    public java.lang.String getSpecialnote() {
        return specialnote;
    }


    /**
     * Sets the specialnote value for this OrderItem.
     * 
     * @param specialnote
     */
    public void setSpecialnote(java.lang.String specialnote) {
        this.specialnote = specialnote;
    }


    /**
     * Gets the totalcharges value for this OrderItem.
     * 
     * @return totalcharges
     */
    public java.lang.Float getTotalcharges() {
        return totalcharges;
    }


    /**
     * Sets the totalcharges value for this OrderItem.
     * 
     * @param totalcharges
     */
    public void setTotalcharges(java.lang.Float totalcharges) {
        this.totalcharges = totalcharges;
    }


    /**
     * Gets the createtime value for this OrderItem.
     * 
     * @return createtime
     */
    public java.lang.String getCreatetime() {
        return createtime;
    }


    /**
     * Sets the createtime value for this OrderItem.
     * 
     * @param createtime
     */
    public void setCreatetime(java.lang.String createtime) {
        this.createtime = createtime;
    }


    /**
     * Gets the modifiedtime value for this OrderItem.
     * 
     * @return modifiedtime
     */
    public java.lang.String getModifiedtime() {
        return modifiedtime;
    }


    /**
     * Sets the modifiedtime value for this OrderItem.
     * 
     * @param modifiedtime
     */
    public void setModifiedtime(java.lang.String modifiedtime) {
        this.modifiedtime = modifiedtime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OrderItem)) return false;
        OrderItem other = (OrderItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.number == other.getNumber() &&
            ((this.orderid==null && other.getOrderid()==null) || 
             (this.orderid!=null &&
              this.orderid.equals(other.getOrderid()))) &&
            ((this.ordercdforchannel==null && other.getOrdercdforchannel()==null) || 
             (this.ordercdforchannel!=null &&
              this.ordercdforchannel.equals(other.getOrdercdforchannel()))) &&
            ((this.roomno==null && other.getRoomno()==null) || 
             (this.roomno!=null &&
              this.roomno.equals(other.getRoomno()))) &&
            ((this.night==null && other.getNight()==null) || 
             (this.night!=null &&
              this.night.equals(other.getNight()))) &&
            ((this.guests==null && other.getGuests()==null) || 
             (this.guests!=null &&
              this.guests.equals(other.getGuests()))) &&
            ((this.noteresult==null && other.getNoteresult()==null) || 
             (this.noteresult!=null &&
              this.noteresult.equals(other.getNoteresult()))) &&
            ((this.orderstate==null && other.getOrderstate()==null) || 
             (this.orderstate!=null &&
              this.orderstate.equals(other.getOrderstate()))) &&
            ((this.roomState==null && other.getRoomState()==null) || 
             (this.roomState!=null &&
              this.roomState.equals(other.getRoomState()))) &&
            ((this.quitdate==null && other.getQuitdate()==null) || 
             (this.quitdate!=null &&
              this.quitdate.equals(other.getQuitdate()))) &&
            ((this.baseprice==null && other.getBaseprice()==null) || 
             (this.baseprice!=null &&
              this.baseprice.equals(other.getBaseprice()))) &&
            ((this.saleprice==null && other.getSaleprice()==null) || 
             (this.saleprice!=null &&
              this.saleprice.equals(other.getSaleprice()))) &&
            ((this.baserate==null && other.getBaserate()==null) || 
             (this.baserate!=null &&
              this.baserate.equals(other.getBaserate()))) &&
            ((this.specialnote==null && other.getSpecialnote()==null) || 
             (this.specialnote!=null &&
              this.specialnote.equals(other.getSpecialnote()))) &&
            ((this.totalcharges==null && other.getTotalcharges()==null) || 
             (this.totalcharges!=null &&
              this.totalcharges.equals(other.getTotalcharges()))) &&
            ((this.createtime==null && other.getCreatetime()==null) || 
             (this.createtime!=null &&
              this.createtime.equals(other.getCreatetime()))) &&
            ((this.modifiedtime==null && other.getModifiedtime()==null) || 
             (this.modifiedtime!=null &&
              this.modifiedtime.equals(other.getModifiedtime())));
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
        _hashCode += getNumber();
        if (getOrderid() != null) {
            _hashCode += getOrderid().hashCode();
        }
        if (getOrdercdforchannel() != null) {
            _hashCode += getOrdercdforchannel().hashCode();
        }
        if (getRoomno() != null) {
            _hashCode += getRoomno().hashCode();
        }
        if (getNight() != null) {
            _hashCode += getNight().hashCode();
        }
        if (getGuests() != null) {
            _hashCode += getGuests().hashCode();
        }
        if (getNoteresult() != null) {
            _hashCode += getNoteresult().hashCode();
        }
        if (getOrderstate() != null) {
            _hashCode += getOrderstate().hashCode();
        }
        if (getRoomState() != null) {
            _hashCode += getRoomState().hashCode();
        }
        if (getQuitdate() != null) {
            _hashCode += getQuitdate().hashCode();
        }
        if (getBaseprice() != null) {
            _hashCode += getBaseprice().hashCode();
        }
        if (getSaleprice() != null) {
            _hashCode += getSaleprice().hashCode();
        }
        if (getBaserate() != null) {
            _hashCode += getBaserate().hashCode();
        }
        if (getSpecialnote() != null) {
            _hashCode += getSpecialnote().hashCode();
        }
        if (getTotalcharges() != null) {
            _hashCode += getTotalcharges().hashCode();
        }
        if (getCreatetime() != null) {
            _hashCode += getCreatetime().hashCode();
        }
        if (getModifiedtime() != null) {
            _hashCode += getModifiedtime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OrderItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "OrderItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("number");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "orderid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ordercdforchannel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ordercdforchannel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomno");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("night");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "night"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guests");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "guests"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("noteresult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "noteresult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderstate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "orderstate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quitdate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "quitdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseprice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "baseprice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("saleprice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "saleprice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baserate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "baserate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("specialnote");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "specialnote"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalcharges");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "totalcharges"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createtime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "createtime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifiedtime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "modifiedtime"));
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
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
