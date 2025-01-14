package org.egov.tlcalculator.service;

import java.math.BigDecimal;

public interface Calculator {

	public BigDecimal AREA = new BigDecimal("4047");
	public BigDecimal PERCENTAGE1 = new BigDecimal(0.96);
	public BigDecimal PERCENTAGE2 = new BigDecimal(0.04);
	public BigDecimal PERCENTAGE0995 = new BigDecimal(0.995);
	public BigDecimal PERCENTAGE5 = new BigDecimal(0.005);
	public BigDecimal PERCENTAGE25 = new BigDecimal(2.5);
	public BigDecimal PERCENTAGE125 = new BigDecimal(1.25);
	public BigDecimal PERCENTAGE175 = new BigDecimal(1.75);
	public BigDecimal PERCENTAGE075 = new BigDecimal(0.75);
	public BigDecimal RATE2 = new BigDecimal(2);
	public BigDecimal RATE5 = new BigDecimal(5);
	public BigDecimal RATE7 = new BigDecimal(7);
	public BigDecimal RATE100000 = new BigDecimal(100000);
	public BigDecimal RATE10000 = new BigDecimal(10000);
	public BigDecimal RATE = new BigDecimal(1250000);
	public BigDecimal RATE95 = new BigDecimal(950000);
	public BigDecimal RATE950 = new BigDecimal(950000);
	public BigDecimal RATE250 = new BigDecimal(2500000);
	public BigDecimal RATE625 = new BigDecimal(625000);
	public BigDecimal RATE125 = new BigDecimal(125000);
	public BigDecimal RATE270 = new BigDecimal(27000000);
	public BigDecimal RATE210 = new BigDecimal(21000000);
	public BigDecimal RATE19 = new BigDecimal(1900000);
	public BigDecimal RATE1 = new BigDecimal(34000000);
	public BigDecimal RATE10 = new BigDecimal(10);
	public BigDecimal RATE40 = new BigDecimal(4000000);
	public BigDecimal RATE25 = new BigDecimal(250000);
	public BigDecimal RATE0 = new BigDecimal(0);
	public BigDecimal RATE62500 = new BigDecimal(62500);
	public BigDecimal RATE12500 = new BigDecimal(12500);
	public BigDecimal RATE235 = new BigDecimal(23500000);
	public BigDecimal RATE14000 = new BigDecimal(14000000);
	public BigDecimal RATE6250 = new BigDecimal(6250000);
	public BigDecimal RATE_10100= new BigDecimal(10100);
	
	public BigDecimal RATE_158= new BigDecimal(158);
	public BigDecimal RATE_125= new BigDecimal(125);
	public BigDecimal RATE_80= new BigDecimal(80);
	public BigDecimal RATE_20= new BigDecimal(20);
	public BigDecimal RATE_1260= new BigDecimal(1260);
	public BigDecimal RATE_1050= new BigDecimal(1050);
	public BigDecimal RATE_600= new BigDecimal(600);
	public BigDecimal RATE_150= new BigDecimal(150);
	public BigDecimal RATE_100= new BigDecimal(100);
	public BigDecimal RATE_50= new BigDecimal(50);
	public BigDecimal RATE_30= new BigDecimal(30);
	
	public BigDecimal RATE104 =new BigDecimal(10410000);
	public BigDecimal RATE09 =new BigDecimal(0.9);
	public BigDecimal RATE07 =new BigDecimal(0.7);
	public BigDecimal RATE06 =new BigDecimal(0.6);
	public BigDecimal RATE05 =new BigDecimal(0.5);
	public BigDecimal RATE04 =new BigDecimal(0.4);
	public BigDecimal RATE467=new BigDecimal(4.67);
	public BigDecimal RATE167=new BigDecimal(1.67);
	public BigDecimal RATE334=new BigDecimal(3.34);
	public BigDecimal RATE01=new BigDecimal(0.1);
	public BigDecimal RATE4=new BigDecimal(4);
	
	public BigDecimal RATE500=new BigDecimal(500);
	public BigDecimal RATE_250=new BigDecimal(250);
	public BigDecimal RATE375=new BigDecimal(375);
	public BigDecimal RATE70=new BigDecimal(70);
	public BigDecimal RATE_625=new BigDecimal(625);
	public BigDecimal RATE_1000=new BigDecimal(1000);
	public BigDecimal RATE460=new BigDecimal(460);
	public BigDecimal RATE_750=new BigDecimal(750);
	public BigDecimal RATE320=new BigDecimal(320);
	public BigDecimal RATE90=new BigDecimal(90);
	public BigDecimal RATE_190=new BigDecimal(190);
	public BigDecimal RATE_500=new BigDecimal(500);
	public BigDecimal RATE35=new BigDecimal(35);
	public BigDecimal PART1 = new BigDecimal(0.8);
	public BigDecimal PART2 = new BigDecimal(0.15);
	public BigDecimal PART3 = new BigDecimal(0.05);

	String PURPOSE_AGH = "AGH";
	String PURPOSE_DDJAY_APHP = "DDJAY_APHP";
	String PURPOSE_CICS = "CICS";
	String PURPOSE_CIRS = "CIRS";
	String PURPOSE_CPCS = "CPCS";
	String PURPOSE_CPRS = "CPRS";
	String PURPOSE_IPULP = "IPULP";
	String PURPOSE_IPA = "IPA";
	String PURPOSE_ITC = "ITC";
	String PURPOSE_ITP = "ITP";
	String PURPOSE_LDEF = "LDEF";
	String PURPOSE_MLU_CZ = "MLU-CZ";
	String PURPOSE_NILPC = "NILPC";
	String PURPOSE_NILP = "NILP";
	String PURPOSE_RGP = "RGP";
	String PURPOSE_RPL = "RPL";
	String PURPOSE_RHP = "RHP";
	String PURPOSE_TODCOMM = "TODCOMM";
	String PURPOSE_TODIT = "TODIT";
	String PURPOSE_TODGH = "TODGH";
	String PURPOSE_TODMUD = "TODMUD";
	String PURPOSE_TODMGH = "TODMGH";

//	String ZONE_HYPER="HYP";
//	String ZONE_HIG1="HIG1";
//	String ZONE_HIG2="HIG2";
//	String ZONE_LOW = "LOW";
//	String ZONE_LOW2="LOW2";

	String ZONE_HYPER = "Hyper";
	String ZONE_HIG1 = "High";
	String ZONE_HIG2 = "High Potential";
	String ZONE_MDM = "Medium";
	String ZONE_LOW = "Low";
	String ZONE_LOW2 = "Low Potential";

}
