package com.ccservice.b2b2c.atom.test;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.service.IAtomService;

public class TestTianQuInsure {

	public void createOrder() {
		String url = "http://localhost:8080/cn_interface/service/";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			IAtomService servier = (IAtomService) factory.create(
					IAtomService.class, url
							+ IAtomService.class.getSimpleName());
			String ioId = servier.tianQuCreateOrder("贾建磊", "18810285453", "",
					"1", "13052519901125073X", "2013-08-01",
					"2230567110@126.com", "1", "HO1252");
			System.out.println("ioId==" + ioId);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void orderInfo() {
		String url = "http://localhost:8080/cn_interface/service/";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			IAtomService servier = (IAtomService) factory.create(
					IAtomService.class, url
							+ IAtomService.class.getSimpleName());
			String result = servier.tianQuOrderInfo("GJ2013070409262730");
			System.out.println("result==" + result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cancleOrder() {
		String url = "http://localhost:8080/cn_interface/service/";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			IAtomService servier = (IAtomService) factory.create(
					IAtomService.class, url
							+ IAtomService.class.getSimpleName());
			String result = servier.tianQuCancleOrder("GJ2013070216593575");
			System.out.println("result==" + result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void payOrder() {
		String url = "http://localhost:8080/cn_interface/service/";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			IAtomService servier = (IAtomService) factory.create(
					IAtomService.class, url
							+ IAtomService.class.getSimpleName());
			String result = servier.tianQuPayOrder("GJ201307031129186");
			System.out.println("result==" + result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void backOrder() {
		String url = "http://localhost:8080/cn_interface/service/";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			IAtomService servier = (IAtomService) factory.create(
					IAtomService.class, url
							+ IAtomService.class.getSimpleName());
			String result = servier.tianQuBackOrder("932000001942331");
			System.out.println("result==" + result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void payOrderAgain() {
		String url = "http://localhost:8080/cn_interface/service/";
		HessianProxyFactory factory = new HessianProxyFactory();
		try {
			IAtomService servier = (IAtomService) factory.create(
					IAtomService.class, url
							+ IAtomService.class.getSimpleName());
			String result = servier.tianQuPayOrderAgain("GJ201307031118001",
					"水虹彩", "1", "330324199001241469", "18810285453",
					"2230567110@qq.com", "", "HO1252");
			System.out.println("result==" + result);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
