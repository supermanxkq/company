package com.ccservice.elong.inter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.ccservice.b2b2c.base.creditcard.Creditcard;
import com.ccservice.b2b2c.base.guest.Contacter;

import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.elong.base.NorthBoundAPIServiceStub;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.ArrayOfContacterForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.ArrayOfGuestForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.ArrayOfRoomForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.ContacterForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.CreditCardForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.FaxForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.GuestForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.HotelOrderForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.PhoneForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.RoomForSubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.SubmitHotelOrder;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.SubmitHotelOrderRequest;
import com.ccservice.elong.base.NorthBoundAPIServiceStub.SubmitHotelOrderResponseE;

/**
 * 提交订单接口
 * 
 * @author 师卫林
 * 
 */
public class ELongSubmitHotelOrder {
	public static NorthBoundAPIServiceStub.SubmitHotelOrderResponseE submitOrder(String HotelId, String RoomTypeID, int RoomAmount,
			String RatePlanID, String checkindate, String checkoutdate, String arrivalearlytime, String arrivalatetime, String GuestTypeCode,
			int GuestAmount, String PaymentTypeCode, String CurrencyCode, double TotalPrice, String ConfirmTypeCode, String ConfirmLanguageCode,
			Contacter Contacters, Creditcard creditCard, List<Guest> Guests) throws Exception {
		NorthBoundAPIServiceStub stub = new NorthBoundAPIServiceStub();
		RoomForSubmitHotelOrder roomForSubmitHotelOrder = new RoomForSubmitHotelOrder();
		ContacterForSubmitHotelOrder contacterForSubmitHotelOrder = new ContacterForSubmitHotelOrder();
		PhoneForSubmitHotelOrder phoneForSubmitHotelOrder = new PhoneForSubmitHotelOrder();
		FaxForSubmitHotelOrder faxForSubmitHotelOrder = new FaxForSubmitHotelOrder();
		ArrayOfContacterForSubmitHotelOrder arrayOfContacterForSubmitHotelOrder = new ArrayOfContacterForSubmitHotelOrder();
		CreditCardForSubmitHotelOrder cardForSubmitHotelOrder = new CreditCardForSubmitHotelOrder();
		ArrayOfGuestForSubmitHotelOrder arrayOfGuestForSubmitHotelOrder = new ArrayOfGuestForSubmitHotelOrder();
		SubmitHotelOrderResponseE response = new SubmitHotelOrderResponseE();
		SubmitHotelOrderRequest request = new SubmitHotelOrderRequest();
		HotelOrderForSubmitHotelOrder hotelOrderForSubmitHotelOrder = new HotelOrderForSubmitHotelOrder();
		ArrayOfRoomForSubmitHotelOrder arrayOfRoomForSubmitHotelOrder = new ArrayOfRoomForSubmitHotelOrder();
		SubmitHotelOrder submitHotelOrder = new SubmitHotelOrder();

		// 入住时间
		Calendar CheckInDate = DateSwitch.SwitchCalendar(checkindate);
		// 离店时间
		Calendar CheckOutDate = DateSwitch.SwitchCalendar(checkoutdate);

		Calendar ArrivalEarlyTime = DateSwitch.SwitchCalendar(arrivalearlytime);
		Calendar ArrivalLateTime = DateSwitch.SwitchCalendar(arrivalatetime);

		// 国内区号
		phoneForSubmitHotelOrder.setAreaCode(0);
		// 分机号
		phoneForSubmitHotelOrder.setExt(0);
		// 国际区号
		phoneForSubmitHotelOrder.setInterCode(0);
		// 电话号码
		phoneForSubmitHotelOrder.setNmber(0);

		// 国内区号
		faxForSubmitHotelOrder.setAreaCode(0);
		// 分机号
		faxForSubmitHotelOrder.setExt(0);
		// 国际区号
		faxForSubmitHotelOrder.setInterCode(0);
		// 电话号码
		faxForSubmitHotelOrder.setNmber(0);
		if (Contacters.getEmail() == null) {
			contacterForSubmitHotelOrder.setEmail("");
		} else {
			contacterForSubmitHotelOrder.setEmail(Contacters.getEmail());
		}
		if (Contacters.getIdNumber() == null) {
			contacterForSubmitHotelOrder.setIdNumber("");
		} else {
			contacterForSubmitHotelOrder.setIdNumber(Contacters.getIdNumber());
		}
		if (Contacters.getIdTypeCode() == (null)) {
			contacterForSubmitHotelOrder.setIdTypeCode("");
		} else {
			contacterForSubmitHotelOrder.setIdTypeCode(Contacters.getIdTypeCode());
		}
		if (Contacters.getMobile() == (null)) {
			contacterForSubmitHotelOrder.setMobile("");
		} else {
			contacterForSubmitHotelOrder.setMobile(Contacters.getMobile());
		}
		contacterForSubmitHotelOrder.setName(Contacters.getName());
		contacterForSubmitHotelOrder.setPhone(phoneForSubmitHotelOrder);
		contacterForSubmitHotelOrder.setFax(faxForSubmitHotelOrder);
		contacterForSubmitHotelOrder.setGenderCode(Contacters.getGenderCode());

		NorthBoundAPIServiceStub.ContacterForSubmitHotelOrder[] arr = { contacterForSubmitHotelOrder };
		arrayOfContacterForSubmitHotelOrder.setContacter(arr);
		if(creditCard!=null){
			if (creditCard.getCreditcheckcode() == null) {
				cardForSubmitHotelOrder.setVeryfyCode("");
			} else {
				cardForSubmitHotelOrder.setVeryfyCode(creditCard.getCreditcheckcode());
			}
			if(creditCard.getCreditexpiry()!=null){
				Timestamp temp=creditCard.getCreditexpiry();
				cardForSubmitHotelOrder.setValidYear(1900+temp.getYear());
				cardForSubmitHotelOrder.setValidMonth(temp.getMonth()+1);
			}else{
				cardForSubmitHotelOrder.setValidYear(0);
				cardForSubmitHotelOrder.setValidMonth(0);
			}
			if (creditCard.getCreditnumber() == null) {
				cardForSubmitHotelOrder.setNumber("");
			} else {
				cardForSubmitHotelOrder.setNumber(creditCard.getCreditnumber());
			}
			if (creditCard.getIdTypeCode() == null) {
				cardForSubmitHotelOrder.setIdTypeCode("");
			} else {
				cardForSubmitHotelOrder.setIdTypeCode(creditCard.getIdTypeCode());
			}
			if (creditCard.getIdNumber() == null) {
				cardForSubmitHotelOrder.setIdNumber("");
			} else {
				cardForSubmitHotelOrder.setIdNumber(creditCard.getIdNumber());
			}
			if (creditCard.getCardholdername() == null) {
				cardForSubmitHotelOrder.setCardHolderName("");
			} else {
				cardForSubmitHotelOrder.setCardHolderName(creditCard.getCardholdername());
			}
		}
		NorthBoundAPIServiceStub.GuestForSubmitHotelOrder[] arry = new NorthBoundAPIServiceStub.GuestForSubmitHotelOrder[Guests.size()];
		for (int i = 0; i < Guests.size(); i++) {
			Guest guest = Guests.get(i);
			GuestForSubmitHotelOrder guestForSubmitHotelOrder = new GuestForSubmitHotelOrder();
			if (guest.getSex() == null) {
				guestForSubmitHotelOrder.setGenderCode("2");
			} else {
				guestForSubmitHotelOrder.setGenderCode(guest.getSex().toString());
			}
			if (guest.getEmail() == null) {
				guestForSubmitHotelOrder.setEmail("");
			} else {
				guestForSubmitHotelOrder.setEmail(guest.getEmail());
			}
			if (guest.getIdNumber() == null) {
				guestForSubmitHotelOrder.setIdNumber("");
			} else {
				guestForSubmitHotelOrder.setIdNumber(guest.getIdNumber());
			}
			if (guest.getIdTypeCode() == null) {
				guestForSubmitHotelOrder.setIdTypeCode("");
			} else {
				guestForSubmitHotelOrder.setIdTypeCode(guest.getIdTypeCode());
			}
			if (guest.getMobile() == null) {
				guestForSubmitHotelOrder.setMobile("");
			} else {
				guestForSubmitHotelOrder.setMobile(guest.getMobile());
			}
			guestForSubmitHotelOrder.setFax(faxForSubmitHotelOrder);
			guestForSubmitHotelOrder.setName(guest.getName());
			guestForSubmitHotelOrder.setNationality(guest.getNationality());
			guestForSubmitHotelOrder.setPhone(phoneForSubmitHotelOrder);
			arry[i] = guestForSubmitHotelOrder;

		}
		arrayOfGuestForSubmitHotelOrder.setGuest(arry);

		roomForSubmitHotelOrder.setArrivalEarlyTime(ArrivalEarlyTime);
		roomForSubmitHotelOrder.setArrivalLateTime(ArrivalLateTime);
		roomForSubmitHotelOrder.setCheckInDate(CheckInDate);
		roomForSubmitHotelOrder.setCheckOutDate(CheckOutDate);
		roomForSubmitHotelOrder.setConfirmLanguageCode(ConfirmLanguageCode);
		roomForSubmitHotelOrder.setConfirmTypeCode(ConfirmTypeCode);
		roomForSubmitHotelOrder.setContacters(arrayOfContacterForSubmitHotelOrder);
		roomForSubmitHotelOrder.setCreditCard(cardForSubmitHotelOrder);
		roomForSubmitHotelOrder.setCurrencyCode(CurrencyCode);
		roomForSubmitHotelOrder.setElongCardNo("");
		roomForSubmitHotelOrder.setGuestAmount(GuestAmount);
		roomForSubmitHotelOrder.setGuests(arrayOfGuestForSubmitHotelOrder);
		roomForSubmitHotelOrder.setGuestTypeCode(GuestTypeCode);
		roomForSubmitHotelOrder.setHotelId(HotelId);
		roomForSubmitHotelOrder.setNoteToElong("");
		roomForSubmitHotelOrder.setNoteToHotel("");
		roomForSubmitHotelOrder.setPaymentTypeCode(PaymentTypeCode);
		roomForSubmitHotelOrder.setRatePlanCode("");
		roomForSubmitHotelOrder.setRatePlanID(Integer.valueOf(RatePlanID));
		roomForSubmitHotelOrder.setRoomAmount(RoomAmount);
		roomForSubmitHotelOrder.setRoomTypeId(RoomTypeID);
		roomForSubmitHotelOrder.setTotalPrice(BigDecimal.valueOf(TotalPrice));

		NorthBoundAPIServiceStub.RoomForSubmitHotelOrder[] rooms = { roomForSubmitHotelOrder };
		arrayOfRoomForSubmitHotelOrder.setRoom(rooms);
		hotelOrderForSubmitHotelOrder.setRoomGroups(arrayOfRoomForSubmitHotelOrder);
		// System.out.println("GUID:"+CreateGUID.createGUID());
		request.setRequestHead(ElongRequestHead.getRequestHead(CreateGUID.createGUID()));
		request.setHotelOrder(hotelOrderForSubmitHotelOrder);

		submitHotelOrder.setSubmitHotelOrderRequest(request);
		response = stub.submitHotelOrder(submitHotelOrder);

		// System.out.println(response.getSubmitHotelOrderResult().getResponseHead().getResultCode());
		// System.out.println(response.getSubmitHotelOrderResult().getResponseHead().getResultMessage());
		// NorthBoundAPIServiceStub.SubmitHotelOrderResult result =
		// response.getSubmitHotelOrderResult().getSubmitHotelOrderResult();
		// // 订单ID
		// System.out.println("订单ID:" + result.getHotelOrderID());
		// // 最晚取消时间
		// System.out.println("最晚取消时间:" +
		// DateTimeSwitch.SwitchString(result.getCancelDeadline()));
		// //担保金额
		// // System.out.println(result.getGuaranteeMoney());
		return response;
	}
}
