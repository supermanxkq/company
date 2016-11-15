/**
 * Hotel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class Hotel  implements java.io.Serializable {
    private java.lang.String hotelCode;

    private java.lang.String chn_name;

    private java.lang.String eng_name;

    private java.lang.String country;

    private java.lang.String cooperateChannel;

    private java.lang.String state;

    private java.lang.String city;

    private java.lang.String zone;

    private java.lang.String biz_zone;

    private java.lang.String layer_high;

    private java.lang.String layer_count;

    private java.lang.String hotel_star;

    private java.lang.String website;

    private java.lang.String chn_address;

    private java.lang.String eng_address;

    private java.lang.String pracice_date;

    private java.lang.String fitment_date;

    private java.lang.String fitment_degree;

    private java.lang.String telephone;

    private java.lang.String post_code;

    private java.lang.String email;

    private java.lang.String working_fax;

    private java.lang.String language;

    private java.lang.String other_language;

    private java.lang.String credit_card_info;

    private java.lang.String other_credit;

    private java.lang.String hotel_type;

    private java.lang.String accept_custom;

    private java.lang.String auto_introduce;

    private java.lang.String chn_hotel_introduce;

    private java.lang.String parent_hotel_group;

    private java.lang.String travel_check;

    private java.lang.String tax_per;

    private java.lang.String card_need_tax;

    private java.lang.String checkin_time;

    private java.lang.String checkout_time;

    private java.lang.String room_fixtrue;

    private java.lang.String handicapped_fixtrue;

    private java.lang.String meal_fixtrue;

    private java.lang.String others_notes;

    private java.lang.String around_view;

    private java.lang.String self_notes;

    private java.lang.String alert_message;

    private java.lang.String hotel_chain;

    private java.lang.String has_contract;

    private java.lang.String hotel_status;

    private java.lang.String foreign_info;

    private java.lang.String has_foreign;

    private java.lang.String hotelcodename;

    private java.lang.String nosmokingfloor;

    private java.lang.String iscontract;

    private java.lang.String free_service;

    private java.lang.String pictures;

    private java.lang.String longitude;

    private java.lang.String latitude;

    private com.mango.hotel.RoomType[] roomTypeList;

    public Hotel() {
    }

    public Hotel(
           java.lang.String hotelCode,
           java.lang.String chn_name,
           java.lang.String eng_name,
           java.lang.String country,
           java.lang.String cooperateChannel,
           java.lang.String state,
           java.lang.String city,
           java.lang.String zone,
           java.lang.String biz_zone,
           java.lang.String layer_high,
           java.lang.String layer_count,
           java.lang.String hotel_star,
           java.lang.String website,
           java.lang.String chn_address,
           java.lang.String eng_address,
           java.lang.String pracice_date,
           java.lang.String fitment_date,
           java.lang.String fitment_degree,
           java.lang.String telephone,
           java.lang.String post_code,
           java.lang.String email,
           java.lang.String working_fax,
           java.lang.String language,
           java.lang.String other_language,
           java.lang.String credit_card_info,
           java.lang.String other_credit,
           java.lang.String hotel_type,
           java.lang.String accept_custom,
           java.lang.String auto_introduce,
           java.lang.String chn_hotel_introduce,
           java.lang.String parent_hotel_group,
           java.lang.String travel_check,
           java.lang.String tax_per,
           java.lang.String card_need_tax,
           java.lang.String checkin_time,
           java.lang.String checkout_time,
           java.lang.String room_fixtrue,
           java.lang.String handicapped_fixtrue,
           java.lang.String meal_fixtrue,
           java.lang.String others_notes,
           java.lang.String around_view,
           java.lang.String self_notes,
           java.lang.String alert_message,
           java.lang.String hotel_chain,
           java.lang.String has_contract,
           java.lang.String hotel_status,
           java.lang.String foreign_info,
           java.lang.String has_foreign,
           java.lang.String hotelcodename,
           java.lang.String nosmokingfloor,
           java.lang.String iscontract,
           java.lang.String free_service,
           java.lang.String pictures,
           java.lang.String longitude,
           java.lang.String latitude,
           com.mango.hotel.RoomType[] roomTypeList) {
           this.hotelCode = hotelCode;
           this.chn_name = chn_name;
           this.eng_name = eng_name;
           this.country = country;
           this.cooperateChannel = cooperateChannel;
           this.state = state;
           this.city = city;
           this.zone = zone;
           this.biz_zone = biz_zone;
           this.layer_high = layer_high;
           this.layer_count = layer_count;
           this.hotel_star = hotel_star;
           this.website = website;
           this.chn_address = chn_address;
           this.eng_address = eng_address;
           this.pracice_date = pracice_date;
           this.fitment_date = fitment_date;
           this.fitment_degree = fitment_degree;
           this.telephone = telephone;
           this.post_code = post_code;
           this.email = email;
           this.working_fax = working_fax;
           this.language = language;
           this.other_language = other_language;
           this.credit_card_info = credit_card_info;
           this.other_credit = other_credit;
           this.hotel_type = hotel_type;
           this.accept_custom = accept_custom;
           this.auto_introduce = auto_introduce;
           this.chn_hotel_introduce = chn_hotel_introduce;
           this.parent_hotel_group = parent_hotel_group;
           this.travel_check = travel_check;
           this.tax_per = tax_per;
           this.card_need_tax = card_need_tax;
           this.checkin_time = checkin_time;
           this.checkout_time = checkout_time;
           this.room_fixtrue = room_fixtrue;
           this.handicapped_fixtrue = handicapped_fixtrue;
           this.meal_fixtrue = meal_fixtrue;
           this.others_notes = others_notes;
           this.around_view = around_view;
           this.self_notes = self_notes;
           this.alert_message = alert_message;
           this.hotel_chain = hotel_chain;
           this.has_contract = has_contract;
           this.hotel_status = hotel_status;
           this.foreign_info = foreign_info;
           this.has_foreign = has_foreign;
           this.hotelcodename = hotelcodename;
           this.nosmokingfloor = nosmokingfloor;
           this.iscontract = iscontract;
           this.free_service = free_service;
           this.pictures = pictures;
           this.longitude = longitude;
           this.latitude = latitude;
           this.roomTypeList = roomTypeList;
    }


    /**
     * Gets the hotelCode value for this Hotel.
     * 
     * @return hotelCode
     */
    public java.lang.String getHotelCode() {
        return hotelCode;
    }


    /**
     * Sets the hotelCode value for this Hotel.
     * 
     * @param hotelCode
     */
    public void setHotelCode(java.lang.String hotelCode) {
        this.hotelCode = hotelCode;
    }


    /**
     * Gets the chn_name value for this Hotel.
     * 
     * @return chn_name
     */
    public java.lang.String getChn_name() {
        return chn_name;
    }


    /**
     * Sets the chn_name value for this Hotel.
     * 
     * @param chn_name
     */
    public void setChn_name(java.lang.String chn_name) {
        this.chn_name = chn_name;
    }


    /**
     * Gets the eng_name value for this Hotel.
     * 
     * @return eng_name
     */
    public java.lang.String getEng_name() {
        return eng_name;
    }


    /**
     * Sets the eng_name value for this Hotel.
     * 
     * @param eng_name
     */
    public void setEng_name(java.lang.String eng_name) {
        this.eng_name = eng_name;
    }


    /**
     * Gets the country value for this Hotel.
     * 
     * @return country
     */
    public java.lang.String getCountry() {
        return country;
    }


    /**
     * Sets the country value for this Hotel.
     * 
     * @param country
     */
    public void setCountry(java.lang.String country) {
        this.country = country;
    }


    /**
     * Gets the cooperateChannel value for this Hotel.
     * 
     * @return cooperateChannel
     */
    public java.lang.String getCooperateChannel() {
        return cooperateChannel;
    }


    /**
     * Sets the cooperateChannel value for this Hotel.
     * 
     * @param cooperateChannel
     */
    public void setCooperateChannel(java.lang.String cooperateChannel) {
        this.cooperateChannel = cooperateChannel;
    }


    /**
     * Gets the state value for this Hotel.
     * 
     * @return state
     */
    public java.lang.String getState() {
        return state;
    }


    /**
     * Sets the state value for this Hotel.
     * 
     * @param state
     */
    public void setState(java.lang.String state) {
        this.state = state;
    }


    /**
     * Gets the city value for this Hotel.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }


    /**
     * Sets the city value for this Hotel.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }


    /**
     * Gets the zone value for this Hotel.
     * 
     * @return zone
     */
    public java.lang.String getZone() {
        return zone;
    }


    /**
     * Sets the zone value for this Hotel.
     * 
     * @param zone
     */
    public void setZone(java.lang.String zone) {
        this.zone = zone;
    }


    /**
     * Gets the biz_zone value for this Hotel.
     * 
     * @return biz_zone
     */
    public java.lang.String getBiz_zone() {
        return biz_zone;
    }


    /**
     * Sets the biz_zone value for this Hotel.
     * 
     * @param biz_zone
     */
    public void setBiz_zone(java.lang.String biz_zone) {
        this.biz_zone = biz_zone;
    }


    /**
     * Gets the layer_high value for this Hotel.
     * 
     * @return layer_high
     */
    public java.lang.String getLayer_high() {
        return layer_high;
    }


    /**
     * Sets the layer_high value for this Hotel.
     * 
     * @param layer_high
     */
    public void setLayer_high(java.lang.String layer_high) {
        this.layer_high = layer_high;
    }


    /**
     * Gets the layer_count value for this Hotel.
     * 
     * @return layer_count
     */
    public java.lang.String getLayer_count() {
        return layer_count;
    }


    /**
     * Sets the layer_count value for this Hotel.
     * 
     * @param layer_count
     */
    public void setLayer_count(java.lang.String layer_count) {
        this.layer_count = layer_count;
    }


    /**
     * Gets the hotel_star value for this Hotel.
     * 
     * @return hotel_star
     */
    public java.lang.String getHotel_star() {
        return hotel_star;
    }


    /**
     * Sets the hotel_star value for this Hotel.
     * 
     * @param hotel_star
     */
    public void setHotel_star(java.lang.String hotel_star) {
        this.hotel_star = hotel_star;
    }


    /**
     * Gets the website value for this Hotel.
     * 
     * @return website
     */
    public java.lang.String getWebsite() {
        return website;
    }


    /**
     * Sets the website value for this Hotel.
     * 
     * @param website
     */
    public void setWebsite(java.lang.String website) {
        this.website = website;
    }


    /**
     * Gets the chn_address value for this Hotel.
     * 
     * @return chn_address
     */
    public java.lang.String getChn_address() {
        return chn_address;
    }


    /**
     * Sets the chn_address value for this Hotel.
     * 
     * @param chn_address
     */
    public void setChn_address(java.lang.String chn_address) {
        this.chn_address = chn_address;
    }


    /**
     * Gets the eng_address value for this Hotel.
     * 
     * @return eng_address
     */
    public java.lang.String getEng_address() {
        return eng_address;
    }


    /**
     * Sets the eng_address value for this Hotel.
     * 
     * @param eng_address
     */
    public void setEng_address(java.lang.String eng_address) {
        this.eng_address = eng_address;
    }


    /**
     * Gets the pracice_date value for this Hotel.
     * 
     * @return pracice_date
     */
    public java.lang.String getPracice_date() {
        return pracice_date;
    }


    /**
     * Sets the pracice_date value for this Hotel.
     * 
     * @param pracice_date
     */
    public void setPracice_date(java.lang.String pracice_date) {
        this.pracice_date = pracice_date;
    }


    /**
     * Gets the fitment_date value for this Hotel.
     * 
     * @return fitment_date
     */
    public java.lang.String getFitment_date() {
        return fitment_date;
    }


    /**
     * Sets the fitment_date value for this Hotel.
     * 
     * @param fitment_date
     */
    public void setFitment_date(java.lang.String fitment_date) {
        this.fitment_date = fitment_date;
    }


    /**
     * Gets the fitment_degree value for this Hotel.
     * 
     * @return fitment_degree
     */
    public java.lang.String getFitment_degree() {
        return fitment_degree;
    }


    /**
     * Sets the fitment_degree value for this Hotel.
     * 
     * @param fitment_degree
     */
    public void setFitment_degree(java.lang.String fitment_degree) {
        this.fitment_degree = fitment_degree;
    }


    /**
     * Gets the telephone value for this Hotel.
     * 
     * @return telephone
     */
    public java.lang.String getTelephone() {
        return telephone;
    }


    /**
     * Sets the telephone value for this Hotel.
     * 
     * @param telephone
     */
    public void setTelephone(java.lang.String telephone) {
        this.telephone = telephone;
    }


    /**
     * Gets the post_code value for this Hotel.
     * 
     * @return post_code
     */
    public java.lang.String getPost_code() {
        return post_code;
    }


    /**
     * Sets the post_code value for this Hotel.
     * 
     * @param post_code
     */
    public void setPost_code(java.lang.String post_code) {
        this.post_code = post_code;
    }


    /**
     * Gets the email value for this Hotel.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this Hotel.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the working_fax value for this Hotel.
     * 
     * @return working_fax
     */
    public java.lang.String getWorking_fax() {
        return working_fax;
    }


    /**
     * Sets the working_fax value for this Hotel.
     * 
     * @param working_fax
     */
    public void setWorking_fax(java.lang.String working_fax) {
        this.working_fax = working_fax;
    }


    /**
     * Gets the language value for this Hotel.
     * 
     * @return language
     */
    public java.lang.String getLanguage() {
        return language;
    }


    /**
     * Sets the language value for this Hotel.
     * 
     * @param language
     */
    public void setLanguage(java.lang.String language) {
        this.language = language;
    }


    /**
     * Gets the other_language value for this Hotel.
     * 
     * @return other_language
     */
    public java.lang.String getOther_language() {
        return other_language;
    }


    /**
     * Sets the other_language value for this Hotel.
     * 
     * @param other_language
     */
    public void setOther_language(java.lang.String other_language) {
        this.other_language = other_language;
    }


    /**
     * Gets the credit_card_info value for this Hotel.
     * 
     * @return credit_card_info
     */
    public java.lang.String getCredit_card_info() {
        return credit_card_info;
    }


    /**
     * Sets the credit_card_info value for this Hotel.
     * 
     * @param credit_card_info
     */
    public void setCredit_card_info(java.lang.String credit_card_info) {
        this.credit_card_info = credit_card_info;
    }


    /**
     * Gets the other_credit value for this Hotel.
     * 
     * @return other_credit
     */
    public java.lang.String getOther_credit() {
        return other_credit;
    }


    /**
     * Sets the other_credit value for this Hotel.
     * 
     * @param other_credit
     */
    public void setOther_credit(java.lang.String other_credit) {
        this.other_credit = other_credit;
    }


    /**
     * Gets the hotel_type value for this Hotel.
     * 
     * @return hotel_type
     */
    public java.lang.String getHotel_type() {
        return hotel_type;
    }


    /**
     * Sets the hotel_type value for this Hotel.
     * 
     * @param hotel_type
     */
    public void setHotel_type(java.lang.String hotel_type) {
        this.hotel_type = hotel_type;
    }


    /**
     * Gets the accept_custom value for this Hotel.
     * 
     * @return accept_custom
     */
    public java.lang.String getAccept_custom() {
        return accept_custom;
    }


    /**
     * Sets the accept_custom value for this Hotel.
     * 
     * @param accept_custom
     */
    public void setAccept_custom(java.lang.String accept_custom) {
        this.accept_custom = accept_custom;
    }


    /**
     * Gets the auto_introduce value for this Hotel.
     * 
     * @return auto_introduce
     */
    public java.lang.String getAuto_introduce() {
        return auto_introduce;
    }


    /**
     * Sets the auto_introduce value for this Hotel.
     * 
     * @param auto_introduce
     */
    public void setAuto_introduce(java.lang.String auto_introduce) {
        this.auto_introduce = auto_introduce;
    }


    /**
     * Gets the chn_hotel_introduce value for this Hotel.
     * 
     * @return chn_hotel_introduce
     */
    public java.lang.String getChn_hotel_introduce() {
        return chn_hotel_introduce;
    }


    /**
     * Sets the chn_hotel_introduce value for this Hotel.
     * 
     * @param chn_hotel_introduce
     */
    public void setChn_hotel_introduce(java.lang.String chn_hotel_introduce) {
        this.chn_hotel_introduce = chn_hotel_introduce;
    }


    /**
     * Gets the parent_hotel_group value for this Hotel.
     * 
     * @return parent_hotel_group
     */
    public java.lang.String getParent_hotel_group() {
        return parent_hotel_group;
    }


    /**
     * Sets the parent_hotel_group value for this Hotel.
     * 
     * @param parent_hotel_group
     */
    public void setParent_hotel_group(java.lang.String parent_hotel_group) {
        this.parent_hotel_group = parent_hotel_group;
    }


    /**
     * Gets the travel_check value for this Hotel.
     * 
     * @return travel_check
     */
    public java.lang.String getTravel_check() {
        return travel_check;
    }


    /**
     * Sets the travel_check value for this Hotel.
     * 
     * @param travel_check
     */
    public void setTravel_check(java.lang.String travel_check) {
        this.travel_check = travel_check;
    }


    /**
     * Gets the tax_per value for this Hotel.
     * 
     * @return tax_per
     */
    public java.lang.String getTax_per() {
        return tax_per;
    }


    /**
     * Sets the tax_per value for this Hotel.
     * 
     * @param tax_per
     */
    public void setTax_per(java.lang.String tax_per) {
        this.tax_per = tax_per;
    }


    /**
     * Gets the card_need_tax value for this Hotel.
     * 
     * @return card_need_tax
     */
    public java.lang.String getCard_need_tax() {
        return card_need_tax;
    }


    /**
     * Sets the card_need_tax value for this Hotel.
     * 
     * @param card_need_tax
     */
    public void setCard_need_tax(java.lang.String card_need_tax) {
        this.card_need_tax = card_need_tax;
    }


    /**
     * Gets the checkin_time value for this Hotel.
     * 
     * @return checkin_time
     */
    public java.lang.String getCheckin_time() {
        return checkin_time;
    }


    /**
     * Sets the checkin_time value for this Hotel.
     * 
     * @param checkin_time
     */
    public void setCheckin_time(java.lang.String checkin_time) {
        this.checkin_time = checkin_time;
    }


    /**
     * Gets the checkout_time value for this Hotel.
     * 
     * @return checkout_time
     */
    public java.lang.String getCheckout_time() {
        return checkout_time;
    }


    /**
     * Sets the checkout_time value for this Hotel.
     * 
     * @param checkout_time
     */
    public void setCheckout_time(java.lang.String checkout_time) {
        this.checkout_time = checkout_time;
    }


    /**
     * Gets the room_fixtrue value for this Hotel.
     * 
     * @return room_fixtrue
     */
    public java.lang.String getRoom_fixtrue() {
        return room_fixtrue;
    }


    /**
     * Sets the room_fixtrue value for this Hotel.
     * 
     * @param room_fixtrue
     */
    public void setRoom_fixtrue(java.lang.String room_fixtrue) {
        this.room_fixtrue = room_fixtrue;
    }


    /**
     * Gets the handicapped_fixtrue value for this Hotel.
     * 
     * @return handicapped_fixtrue
     */
    public java.lang.String getHandicapped_fixtrue() {
        return handicapped_fixtrue;
    }


    /**
     * Sets the handicapped_fixtrue value for this Hotel.
     * 
     * @param handicapped_fixtrue
     */
    public void setHandicapped_fixtrue(java.lang.String handicapped_fixtrue) {
        this.handicapped_fixtrue = handicapped_fixtrue;
    }


    /**
     * Gets the meal_fixtrue value for this Hotel.
     * 
     * @return meal_fixtrue
     */
    public java.lang.String getMeal_fixtrue() {
        return meal_fixtrue;
    }


    /**
     * Sets the meal_fixtrue value for this Hotel.
     * 
     * @param meal_fixtrue
     */
    public void setMeal_fixtrue(java.lang.String meal_fixtrue) {
        this.meal_fixtrue = meal_fixtrue;
    }


    /**
     * Gets the others_notes value for this Hotel.
     * 
     * @return others_notes
     */
    public java.lang.String getOthers_notes() {
        return others_notes;
    }


    /**
     * Sets the others_notes value for this Hotel.
     * 
     * @param others_notes
     */
    public void setOthers_notes(java.lang.String others_notes) {
        this.others_notes = others_notes;
    }


    /**
     * Gets the around_view value for this Hotel.
     * 
     * @return around_view
     */
    public java.lang.String getAround_view() {
        return around_view;
    }


    /**
     * Sets the around_view value for this Hotel.
     * 
     * @param around_view
     */
    public void setAround_view(java.lang.String around_view) {
        this.around_view = around_view;
    }


    /**
     * Gets the self_notes value for this Hotel.
     * 
     * @return self_notes
     */
    public java.lang.String getSelf_notes() {
        return self_notes;
    }


    /**
     * Sets the self_notes value for this Hotel.
     * 
     * @param self_notes
     */
    public void setSelf_notes(java.lang.String self_notes) {
        this.self_notes = self_notes;
    }


    /**
     * Gets the alert_message value for this Hotel.
     * 
     * @return alert_message
     */
    public java.lang.String getAlert_message() {
        return alert_message;
    }


    /**
     * Sets the alert_message value for this Hotel.
     * 
     * @param alert_message
     */
    public void setAlert_message(java.lang.String alert_message) {
        this.alert_message = alert_message;
    }


    /**
     * Gets the hotel_chain value for this Hotel.
     * 
     * @return hotel_chain
     */
    public java.lang.String getHotel_chain() {
        return hotel_chain;
    }


    /**
     * Sets the hotel_chain value for this Hotel.
     * 
     * @param hotel_chain
     */
    public void setHotel_chain(java.lang.String hotel_chain) {
        this.hotel_chain = hotel_chain;
    }


    /**
     * Gets the has_contract value for this Hotel.
     * 
     * @return has_contract
     */
    public java.lang.String getHas_contract() {
        return has_contract;
    }


    /**
     * Sets the has_contract value for this Hotel.
     * 
     * @param has_contract
     */
    public void setHas_contract(java.lang.String has_contract) {
        this.has_contract = has_contract;
    }


    /**
     * Gets the hotel_status value for this Hotel.
     * 
     * @return hotel_status
     */
    public java.lang.String getHotel_status() {
        return hotel_status;
    }


    /**
     * Sets the hotel_status value for this Hotel.
     * 
     * @param hotel_status
     */
    public void setHotel_status(java.lang.String hotel_status) {
        this.hotel_status = hotel_status;
    }


    /**
     * Gets the foreign_info value for this Hotel.
     * 
     * @return foreign_info
     */
    public java.lang.String getForeign_info() {
        return foreign_info;
    }


    /**
     * Sets the foreign_info value for this Hotel.
     * 
     * @param foreign_info
     */
    public void setForeign_info(java.lang.String foreign_info) {
        this.foreign_info = foreign_info;
    }


    /**
     * Gets the has_foreign value for this Hotel.
     * 
     * @return has_foreign
     */
    public java.lang.String getHas_foreign() {
        return has_foreign;
    }


    /**
     * Sets the has_foreign value for this Hotel.
     * 
     * @param has_foreign
     */
    public void setHas_foreign(java.lang.String has_foreign) {
        this.has_foreign = has_foreign;
    }


    /**
     * Gets the hotelcodename value for this Hotel.
     * 
     * @return hotelcodename
     */
    public java.lang.String getHotelcodename() {
        return hotelcodename;
    }


    /**
     * Sets the hotelcodename value for this Hotel.
     * 
     * @param hotelcodename
     */
    public void setHotelcodename(java.lang.String hotelcodename) {
        this.hotelcodename = hotelcodename;
    }


    /**
     * Gets the nosmokingfloor value for this Hotel.
     * 
     * @return nosmokingfloor
     */
    public java.lang.String getNosmokingfloor() {
        return nosmokingfloor;
    }


    /**
     * Sets the nosmokingfloor value for this Hotel.
     * 
     * @param nosmokingfloor
     */
    public void setNosmokingfloor(java.lang.String nosmokingfloor) {
        this.nosmokingfloor = nosmokingfloor;
    }


    /**
     * Gets the iscontract value for this Hotel.
     * 
     * @return iscontract
     */
    public java.lang.String getIscontract() {
        return iscontract;
    }


    /**
     * Sets the iscontract value for this Hotel.
     * 
     * @param iscontract
     */
    public void setIscontract(java.lang.String iscontract) {
        this.iscontract = iscontract;
    }


    /**
     * Gets the free_service value for this Hotel.
     * 
     * @return free_service
     */
    public java.lang.String getFree_service() {
        return free_service;
    }


    /**
     * Sets the free_service value for this Hotel.
     * 
     * @param free_service
     */
    public void setFree_service(java.lang.String free_service) {
        this.free_service = free_service;
    }


    /**
     * Gets the pictures value for this Hotel.
     * 
     * @return pictures
     */
    public java.lang.String getPictures() {
        return pictures;
    }


    /**
     * Sets the pictures value for this Hotel.
     * 
     * @param pictures
     */
    public void setPictures(java.lang.String pictures) {
        this.pictures = pictures;
    }


    /**
     * Gets the longitude value for this Hotel.
     * 
     * @return longitude
     */
    public java.lang.String getLongitude() {
        return longitude;
    }


    /**
     * Sets the longitude value for this Hotel.
     * 
     * @param longitude
     */
    public void setLongitude(java.lang.String longitude) {
        this.longitude = longitude;
    }


    /**
     * Gets the latitude value for this Hotel.
     * 
     * @return latitude
     */
    public java.lang.String getLatitude() {
        return latitude;
    }


    /**
     * Sets the latitude value for this Hotel.
     * 
     * @param latitude
     */
    public void setLatitude(java.lang.String latitude) {
        this.latitude = latitude;
    }


    /**
     * Gets the roomTypeList value for this Hotel.
     * 
     * @return roomTypeList
     */
    public com.mango.hotel.RoomType[] getRoomTypeList() {
        return roomTypeList;
    }


    /**
     * Sets the roomTypeList value for this Hotel.
     * 
     * @param roomTypeList
     */
    public void setRoomTypeList(com.mango.hotel.RoomType[] roomTypeList) {
        this.roomTypeList = roomTypeList;
    }

    public com.mango.hotel.RoomType getRoomTypeList(int i) {
        return this.roomTypeList[i];
    }

    public void setRoomTypeList(int i, com.mango.hotel.RoomType _value) {
        this.roomTypeList[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Hotel)) return false;
        Hotel other = (Hotel) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.hotelCode==null && other.getHotelCode()==null) || 
             (this.hotelCode!=null &&
              this.hotelCode.equals(other.getHotelCode()))) &&
            ((this.chn_name==null && other.getChn_name()==null) || 
             (this.chn_name!=null &&
              this.chn_name.equals(other.getChn_name()))) &&
            ((this.eng_name==null && other.getEng_name()==null) || 
             (this.eng_name!=null &&
              this.eng_name.equals(other.getEng_name()))) &&
            ((this.country==null && other.getCountry()==null) || 
             (this.country!=null &&
              this.country.equals(other.getCountry()))) &&
            ((this.cooperateChannel==null && other.getCooperateChannel()==null) || 
             (this.cooperateChannel!=null &&
              this.cooperateChannel.equals(other.getCooperateChannel()))) &&
            ((this.state==null && other.getState()==null) || 
             (this.state!=null &&
              this.state.equals(other.getState()))) &&
            ((this.city==null && other.getCity()==null) || 
             (this.city!=null &&
              this.city.equals(other.getCity()))) &&
            ((this.zone==null && other.getZone()==null) || 
             (this.zone!=null &&
              this.zone.equals(other.getZone()))) &&
            ((this.biz_zone==null && other.getBiz_zone()==null) || 
             (this.biz_zone!=null &&
              this.biz_zone.equals(other.getBiz_zone()))) &&
            ((this.layer_high==null && other.getLayer_high()==null) || 
             (this.layer_high!=null &&
              this.layer_high.equals(other.getLayer_high()))) &&
            ((this.layer_count==null && other.getLayer_count()==null) || 
             (this.layer_count!=null &&
              this.layer_count.equals(other.getLayer_count()))) &&
            ((this.hotel_star==null && other.getHotel_star()==null) || 
             (this.hotel_star!=null &&
              this.hotel_star.equals(other.getHotel_star()))) &&
            ((this.website==null && other.getWebsite()==null) || 
             (this.website!=null &&
              this.website.equals(other.getWebsite()))) &&
            ((this.chn_address==null && other.getChn_address()==null) || 
             (this.chn_address!=null &&
              this.chn_address.equals(other.getChn_address()))) &&
            ((this.eng_address==null && other.getEng_address()==null) || 
             (this.eng_address!=null &&
              this.eng_address.equals(other.getEng_address()))) &&
            ((this.pracice_date==null && other.getPracice_date()==null) || 
             (this.pracice_date!=null &&
              this.pracice_date.equals(other.getPracice_date()))) &&
            ((this.fitment_date==null && other.getFitment_date()==null) || 
             (this.fitment_date!=null &&
              this.fitment_date.equals(other.getFitment_date()))) &&
            ((this.fitment_degree==null && other.getFitment_degree()==null) || 
             (this.fitment_degree!=null &&
              this.fitment_degree.equals(other.getFitment_degree()))) &&
            ((this.telephone==null && other.getTelephone()==null) || 
             (this.telephone!=null &&
              this.telephone.equals(other.getTelephone()))) &&
            ((this.post_code==null && other.getPost_code()==null) || 
             (this.post_code!=null &&
              this.post_code.equals(other.getPost_code()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.working_fax==null && other.getWorking_fax()==null) || 
             (this.working_fax!=null &&
              this.working_fax.equals(other.getWorking_fax()))) &&
            ((this.language==null && other.getLanguage()==null) || 
             (this.language!=null &&
              this.language.equals(other.getLanguage()))) &&
            ((this.other_language==null && other.getOther_language()==null) || 
             (this.other_language!=null &&
              this.other_language.equals(other.getOther_language()))) &&
            ((this.credit_card_info==null && other.getCredit_card_info()==null) || 
             (this.credit_card_info!=null &&
              this.credit_card_info.equals(other.getCredit_card_info()))) &&
            ((this.other_credit==null && other.getOther_credit()==null) || 
             (this.other_credit!=null &&
              this.other_credit.equals(other.getOther_credit()))) &&
            ((this.hotel_type==null && other.getHotel_type()==null) || 
             (this.hotel_type!=null &&
              this.hotel_type.equals(other.getHotel_type()))) &&
            ((this.accept_custom==null && other.getAccept_custom()==null) || 
             (this.accept_custom!=null &&
              this.accept_custom.equals(other.getAccept_custom()))) &&
            ((this.auto_introduce==null && other.getAuto_introduce()==null) || 
             (this.auto_introduce!=null &&
              this.auto_introduce.equals(other.getAuto_introduce()))) &&
            ((this.chn_hotel_introduce==null && other.getChn_hotel_introduce()==null) || 
             (this.chn_hotel_introduce!=null &&
              this.chn_hotel_introduce.equals(other.getChn_hotel_introduce()))) &&
            ((this.parent_hotel_group==null && other.getParent_hotel_group()==null) || 
             (this.parent_hotel_group!=null &&
              this.parent_hotel_group.equals(other.getParent_hotel_group()))) &&
            ((this.travel_check==null && other.getTravel_check()==null) || 
             (this.travel_check!=null &&
              this.travel_check.equals(other.getTravel_check()))) &&
            ((this.tax_per==null && other.getTax_per()==null) || 
             (this.tax_per!=null &&
              this.tax_per.equals(other.getTax_per()))) &&
            ((this.card_need_tax==null && other.getCard_need_tax()==null) || 
             (this.card_need_tax!=null &&
              this.card_need_tax.equals(other.getCard_need_tax()))) &&
            ((this.checkin_time==null && other.getCheckin_time()==null) || 
             (this.checkin_time!=null &&
              this.checkin_time.equals(other.getCheckin_time()))) &&
            ((this.checkout_time==null && other.getCheckout_time()==null) || 
             (this.checkout_time!=null &&
              this.checkout_time.equals(other.getCheckout_time()))) &&
            ((this.room_fixtrue==null && other.getRoom_fixtrue()==null) || 
             (this.room_fixtrue!=null &&
              this.room_fixtrue.equals(other.getRoom_fixtrue()))) &&
            ((this.handicapped_fixtrue==null && other.getHandicapped_fixtrue()==null) || 
             (this.handicapped_fixtrue!=null &&
              this.handicapped_fixtrue.equals(other.getHandicapped_fixtrue()))) &&
            ((this.meal_fixtrue==null && other.getMeal_fixtrue()==null) || 
             (this.meal_fixtrue!=null &&
              this.meal_fixtrue.equals(other.getMeal_fixtrue()))) &&
            ((this.others_notes==null && other.getOthers_notes()==null) || 
             (this.others_notes!=null &&
              this.others_notes.equals(other.getOthers_notes()))) &&
            ((this.around_view==null && other.getAround_view()==null) || 
             (this.around_view!=null &&
              this.around_view.equals(other.getAround_view()))) &&
            ((this.self_notes==null && other.getSelf_notes()==null) || 
             (this.self_notes!=null &&
              this.self_notes.equals(other.getSelf_notes()))) &&
            ((this.alert_message==null && other.getAlert_message()==null) || 
             (this.alert_message!=null &&
              this.alert_message.equals(other.getAlert_message()))) &&
            ((this.hotel_chain==null && other.getHotel_chain()==null) || 
             (this.hotel_chain!=null &&
              this.hotel_chain.equals(other.getHotel_chain()))) &&
            ((this.has_contract==null && other.getHas_contract()==null) || 
             (this.has_contract!=null &&
              this.has_contract.equals(other.getHas_contract()))) &&
            ((this.hotel_status==null && other.getHotel_status()==null) || 
             (this.hotel_status!=null &&
              this.hotel_status.equals(other.getHotel_status()))) &&
            ((this.foreign_info==null && other.getForeign_info()==null) || 
             (this.foreign_info!=null &&
              this.foreign_info.equals(other.getForeign_info()))) &&
            ((this.has_foreign==null && other.getHas_foreign()==null) || 
             (this.has_foreign!=null &&
              this.has_foreign.equals(other.getHas_foreign()))) &&
            ((this.hotelcodename==null && other.getHotelcodename()==null) || 
             (this.hotelcodename!=null &&
              this.hotelcodename.equals(other.getHotelcodename()))) &&
            ((this.nosmokingfloor==null && other.getNosmokingfloor()==null) || 
             (this.nosmokingfloor!=null &&
              this.nosmokingfloor.equals(other.getNosmokingfloor()))) &&
            ((this.iscontract==null && other.getIscontract()==null) || 
             (this.iscontract!=null &&
              this.iscontract.equals(other.getIscontract()))) &&
            ((this.free_service==null && other.getFree_service()==null) || 
             (this.free_service!=null &&
              this.free_service.equals(other.getFree_service()))) &&
            ((this.pictures==null && other.getPictures()==null) || 
             (this.pictures!=null &&
              this.pictures.equals(other.getPictures()))) &&
            ((this.longitude==null && other.getLongitude()==null) || 
             (this.longitude!=null &&
              this.longitude.equals(other.getLongitude()))) &&
            ((this.latitude==null && other.getLatitude()==null) || 
             (this.latitude!=null &&
              this.latitude.equals(other.getLatitude()))) &&
            ((this.roomTypeList==null && other.getRoomTypeList()==null) || 
             (this.roomTypeList!=null &&
              java.util.Arrays.equals(this.roomTypeList, other.getRoomTypeList())));
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
        if (getHotelCode() != null) {
            _hashCode += getHotelCode().hashCode();
        }
        if (getChn_name() != null) {
            _hashCode += getChn_name().hashCode();
        }
        if (getEng_name() != null) {
            _hashCode += getEng_name().hashCode();
        }
        if (getCountry() != null) {
            _hashCode += getCountry().hashCode();
        }
        if (getCooperateChannel() != null) {
            _hashCode += getCooperateChannel().hashCode();
        }
        if (getState() != null) {
            _hashCode += getState().hashCode();
        }
        if (getCity() != null) {
            _hashCode += getCity().hashCode();
        }
        if (getZone() != null) {
            _hashCode += getZone().hashCode();
        }
        if (getBiz_zone() != null) {
            _hashCode += getBiz_zone().hashCode();
        }
        if (getLayer_high() != null) {
            _hashCode += getLayer_high().hashCode();
        }
        if (getLayer_count() != null) {
            _hashCode += getLayer_count().hashCode();
        }
        if (getHotel_star() != null) {
            _hashCode += getHotel_star().hashCode();
        }
        if (getWebsite() != null) {
            _hashCode += getWebsite().hashCode();
        }
        if (getChn_address() != null) {
            _hashCode += getChn_address().hashCode();
        }
        if (getEng_address() != null) {
            _hashCode += getEng_address().hashCode();
        }
        if (getPracice_date() != null) {
            _hashCode += getPracice_date().hashCode();
        }
        if (getFitment_date() != null) {
            _hashCode += getFitment_date().hashCode();
        }
        if (getFitment_degree() != null) {
            _hashCode += getFitment_degree().hashCode();
        }
        if (getTelephone() != null) {
            _hashCode += getTelephone().hashCode();
        }
        if (getPost_code() != null) {
            _hashCode += getPost_code().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getWorking_fax() != null) {
            _hashCode += getWorking_fax().hashCode();
        }
        if (getLanguage() != null) {
            _hashCode += getLanguage().hashCode();
        }
        if (getOther_language() != null) {
            _hashCode += getOther_language().hashCode();
        }
        if (getCredit_card_info() != null) {
            _hashCode += getCredit_card_info().hashCode();
        }
        if (getOther_credit() != null) {
            _hashCode += getOther_credit().hashCode();
        }
        if (getHotel_type() != null) {
            _hashCode += getHotel_type().hashCode();
        }
        if (getAccept_custom() != null) {
            _hashCode += getAccept_custom().hashCode();
        }
        if (getAuto_introduce() != null) {
            _hashCode += getAuto_introduce().hashCode();
        }
        if (getChn_hotel_introduce() != null) {
            _hashCode += getChn_hotel_introduce().hashCode();
        }
        if (getParent_hotel_group() != null) {
            _hashCode += getParent_hotel_group().hashCode();
        }
        if (getTravel_check() != null) {
            _hashCode += getTravel_check().hashCode();
        }
        if (getTax_per() != null) {
            _hashCode += getTax_per().hashCode();
        }
        if (getCard_need_tax() != null) {
            _hashCode += getCard_need_tax().hashCode();
        }
        if (getCheckin_time() != null) {
            _hashCode += getCheckin_time().hashCode();
        }
        if (getCheckout_time() != null) {
            _hashCode += getCheckout_time().hashCode();
        }
        if (getRoom_fixtrue() != null) {
            _hashCode += getRoom_fixtrue().hashCode();
        }
        if (getHandicapped_fixtrue() != null) {
            _hashCode += getHandicapped_fixtrue().hashCode();
        }
        if (getMeal_fixtrue() != null) {
            _hashCode += getMeal_fixtrue().hashCode();
        }
        if (getOthers_notes() != null) {
            _hashCode += getOthers_notes().hashCode();
        }
        if (getAround_view() != null) {
            _hashCode += getAround_view().hashCode();
        }
        if (getSelf_notes() != null) {
            _hashCode += getSelf_notes().hashCode();
        }
        if (getAlert_message() != null) {
            _hashCode += getAlert_message().hashCode();
        }
        if (getHotel_chain() != null) {
            _hashCode += getHotel_chain().hashCode();
        }
        if (getHas_contract() != null) {
            _hashCode += getHas_contract().hashCode();
        }
        if (getHotel_status() != null) {
            _hashCode += getHotel_status().hashCode();
        }
        if (getForeign_info() != null) {
            _hashCode += getForeign_info().hashCode();
        }
        if (getHas_foreign() != null) {
            _hashCode += getHas_foreign().hashCode();
        }
        if (getHotelcodename() != null) {
            _hashCode += getHotelcodename().hashCode();
        }
        if (getNosmokingfloor() != null) {
            _hashCode += getNosmokingfloor().hashCode();
        }
        if (getIscontract() != null) {
            _hashCode += getIscontract().hashCode();
        }
        if (getFree_service() != null) {
            _hashCode += getFree_service().hashCode();
        }
        if (getPictures() != null) {
            _hashCode += getPictures().hashCode();
        }
        if (getLongitude() != null) {
            _hashCode += getLongitude().hashCode();
        }
        if (getLatitude() != null) {
            _hashCode += getLatitude().hashCode();
        }
        if (getRoomTypeList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRoomTypeList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRoomTypeList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Hotel.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Hotel"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chn_name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "chn_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eng_name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "eng_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "country"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cooperateChannel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "cooperateChannel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("city");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "city"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "zone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("biz_zone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "biz_zone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("layer_high");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "layer_high"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("layer_count");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "layer_count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotel_star");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotel_star"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("website");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "website"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chn_address");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "chn_address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eng_address");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "eng_address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pracice_date");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "pracice_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fitment_date");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "fitment_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fitment_degree");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "fitment_degree"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telephone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "telephone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("post_code");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "post_code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("working_fax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "working_fax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("language");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "language"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("other_language");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "other_language"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("credit_card_info");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "credit_card_info"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("other_credit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "other_credit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotel_type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotel_type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accept_custom");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "accept_custom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auto_introduce");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "auto_introduce"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chn_hotel_introduce");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "chn_hotel_introduce"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parent_hotel_group");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "parent_hotel_group"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("travel_check");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "travel_check"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tax_per");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "tax_per"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("card_need_tax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "card_need_tax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkin_time");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "checkin_time"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkout_time");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "checkout_time"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("room_fixtrue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "room_fixtrue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("handicapped_fixtrue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "handicapped_fixtrue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meal_fixtrue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "meal_fixtrue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("others_notes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "others_notes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("around_view");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "around_view"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("self_notes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "self_notes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alert_message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "alert_message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotel_chain");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotel_chain"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("has_contract");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "has_contract"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotel_status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotel_status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("foreign_info");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "foreign_info"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("has_foreign");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "has_foreign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelcodename");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelcodename"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nosmokingfloor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "nosmokingfloor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iscontract");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "iscontract"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("free_service");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "free_service"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pictures");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "pictures"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("longitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "longitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "latitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomTypeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomTypeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "RoomType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
