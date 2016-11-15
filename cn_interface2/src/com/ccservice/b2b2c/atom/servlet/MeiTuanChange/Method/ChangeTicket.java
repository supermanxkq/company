package com.ccservice.b2b2c.atom.servlet.MeiTuanChange.Method;

@SuppressWarnings("serial")
public class ChangeTicket implements java.io.Serializable {
  //id
  private long ticketId;
  //
  private String ticketNo;
  //
  private String passengerName;
  //
  private String certificateNo;
  //
  private String seatType;
  //
  private String ticketType;
  //
  private String ticketPrice;
  //
  private String coachNo;
  //
  private String seatNo;
  public long getTicketId() {
  return ticketId;
  }
  public String getTicketPrice() {
  return ticketPrice;
  }
  public void setTicketPrice(String ticketPrice) {
  this.ticketPrice = ticketPrice;
  }
  public String getCoachNo() {
  return coachNo;
  }
  public void setCoachNo(String coachNo) {
  this.coachNo = coachNo;
  }
  public String getSeatNo() {
  return seatNo;
  }
  public void setSeatNo(String seatNo) {
  this.seatNo = seatNo;
  }
  public void setTicketId(long ticketId) {
  this.ticketId = ticketId;
  }
  public String getTicketNo() {
  return ticketNo;
  }
  public void setTicketNo(String ticketNo) {
  this.ticketNo = ticketNo;
  }
  public String getPassengerName() {
  return passengerName;
  }
  public void setPassengerName(String passengerName) {
  this.passengerName = passengerName;
  }
  public String getCertificateNo() {
  return certificateNo;
  }
  public void setCertificateNo(String certificateNo) {
  this.certificateNo = certificateNo;
  }
  public String getSeatType() {
  return seatType;
  }
  public void setSeatType(String seatType) {
  this.seatType = seatType;
  }
  public String getTicketType() {
  return ticketType;
  }
  public void setTicketType(String ticketType) {
  this.ticketType = ticketType;
  }
  @Override
  public String toString() {
  return "ChangeTicket{" +
  "ticketId=" + ticketId +
  ", ticketNo='" + ticketNo + '\'' +
  ", passengerName='" + passengerName + '\'' +
  ", certificateNo='" + certificateNo + '\'' +
  ", seatType='" + seatType + '\'' +
  ", ticketType='" + ticketType + '\'' +
  ", ticketPrice=" + ticketPrice +
  ", coachNo='" + coachNo + '\'' +
  ", seatNo='" + seatNo + '\'' +
  '}';
  }
  }
