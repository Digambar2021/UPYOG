package org.egov.tlcalculator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.utils.LandUtil;
import org.egov.tlcalculator.validator.LandMDMSValidator;
import org.egov.tlcalculator.web.models.CalculatorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalculatorImpl implements Calculator {

	@Autowired
	LandUtil landUtil;
	@Autowired
	LandMDMSValidator valid;
	@Autowired
	Calculator calculator;

	private BigDecimal areaInSqmtr(String arce) {
		return (AREA.multiply(new BigDecimal(arce)));
	}

	private BigDecimal areaInSqmtr(BigDecimal arce) {
		return (AREA.multiply(arce));
	}

	public FeesTypeCalculationDto feesTypeCalculation(RequestInfo requestInfo, CalculatorRequest calculatorRequest) {
		BigDecimal arce = new BigDecimal(calculatorRequest.getTotalLandSize());
		BigDecimal AREA1 = (PERCENTAGE1.multiply(new BigDecimal(calculatorRequest.getTotalLandSize())));
		BigDecimal AREA2 = PERCENTAGE2.multiply(new BigDecimal(calculatorRequest.getTotalLandSize()));
		BigDecimal far = new BigDecimal(1.0);
		if (calculatorRequest.getFar() != null)
			far = new BigDecimal(calculatorRequest.getFar());

		// ----------------------------

		Map<String, List<String>> mdmsData;
		FeesTypeCalculationDto feesTypeCalculationDto = new FeesTypeCalculationDto();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeCode = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
				.mDMSCallPurposeCode(requestInfo, requestInfo.getUserInfo().getTenantId(),
						calculatorRequest.getPurposeCode());

		mdmsData = valid.getAttributeValues(mDMSCallPurposeCode);
		List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");
		BigDecimal externalDevelopmentCharges = null;
		BigDecimal scrutinyFeeCharges = null;
		BigDecimal conversionCharges = null;
		BigDecimal licenseFeeCharges = null;
		BigDecimal stateInfrastructureDevelopmentCharges = null;
		String purposeName = "";
		String active="";
		scrutinyFeeCharges = far;
		licenseFeeCharges = far;
		conversionCharges = far;
		externalDevelopmentCharges = far;
		stateInfrastructureDevelopmentCharges = far;
		for (Map<String, Object> mm : msp) {
			purposeName = String.valueOf(mm.get("name"));
			 active =String.valueOf(mm.get("isActive"));
			// scrutinyFeeCharges = new
			// BigDecimal(String.valueOf(mm.get("scrutinyFeeCharges")));
			// externalDevelopmentCharges = new
			// BigDecimal(String.valueOf(mm.get("externalDevelopmentCharges")));
			// conversionCharges = new
			// BigDecimal(String.valueOf(mm.get("conversionCharges")));
			// licenseFeeCharges = new
			// BigDecimal(String.valueOf(mm.get("licenseFeeCharges")));
//			stateInfrastructureDevelopmentCharges = new BigDecimal(
//					String.valueOf(mm.get("stateInfrastructureDevelopmentCharges")));
		}
		feesTypeCalculationDto.setPurpose(purposeName);
		if(active.equals("1"))
		switch (calculatorRequest.getPotenialZone()) {
//--//----------hyper----------//
		case ZONE_HYPER:

			switch (calculatorRequest.getPurposeCode()) {

			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_158));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE500));

				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_ITC:

				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_250));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_1260));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_250));
				break;
			case PURPOSE_IPA:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE167));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_100));
				feesTypeCalculationDto
						.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE_250));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE40));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_158));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_625));

				break;

			case PURPOSE_DDJAY_APHP:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_158));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE500));
				break;

			case PURPOSE_NILPC:
				break;

			case PURPOSE_TODGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));

				feesTypeCalculationDto.setLicenseFeeChargesCal(
						(PERCENTAGE0995.multiply(RATE40).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0))
								.add(RATE1.multiply(PERCENTAGE5).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0)));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(new BigDecimal(0.995).multiply(
						(new BigDecimal(calculatorRequest.getTotalLandSize()).multiply(new BigDecimal(416.385))
								.multiply(new BigDecimal(100000)).multiply(licenseFeeCharges))
								.divide(new BigDecimal(1.75), 0)));
				feesTypeCalculationDto.setConversionChargesCal(
						(new BigDecimal(0.995).multiply(new BigDecimal(158)).multiply(AREA).multiply(conversionCharges))
								.divide(new BigDecimal(1.75), 0)
								.add(new BigDecimal(0.005).multiply(new BigDecimal(1470)).multiply(AREA)
										.multiply(conversionCharges).divide(new BigDecimal(1.75), 0)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(new BigDecimal(0.995)
						.multiply(new BigDecimal(625)).multiply(AREA).multiply(stateInfrastructureDevelopmentCharges)
						.add(new BigDecimal(0.005).multiply(new BigDecimal(1000)).multiply(AREA)
								.multiply(stateInfrastructureDevelopmentCharges)));

				break;

			case PURPOSE_MLU_CZ:

//				if (maximumPermissible.compareTo(new BigDecimal(70)) > 0
//						|| maximumPermissible.equals(new BigDecimal(100))) {
//
//					feesTypeCalculationDto.setScrutinyFeeChargesCal(mixArea.multiply(RATE1));
//					feesTypeCalculationDto.setLicenseFeeChargesCal(mixArea.multiply(RATE1));
//					feesTypeCalculationDto.setConversionChargesCal(mixArea.multiply(RATE1));
//				}
//				if(!maximumPermissible.equals(new BigDecimal(100))) {
//					feesTypeCalculationDto.setScrutinyFeeChargesCal(mixArea.multiply(RATE1));
//					feesTypeCalculationDto.setLicenseFeeChargesCal(mixArea.multiply(RATE1));
//					feesTypeCalculationDto.setConversionChargesCal(mixArea.multiply(RATE1));
//				}
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE250));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE01));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_158));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(RATE500).multiply(RATE2));

				break;

			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE40).multiply(RATE5).divide(RATE7, 0));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_158));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_625)
								.multiply(RATE5)).divide(RATE7));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));
				feesTypeCalculationDto
						.setLicenseFeeChargesCal((RATE1).multiply(licenseFeeCharges).divide(PERCENTAGE175, 0));
//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE1));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE1));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE1));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(
						(new BigDecimal(calculatorRequest.getTotalLandSize()).multiply(new BigDecimal(486.13))
								.multiply(new BigDecimal(100000)).multiply(externalDevelopmentCharges))
								.divide(new BigDecimal(1.75), 0));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_158));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(RATE_1000).multiply(areaInSqmtr(arce)).multiply(stateInfrastructureDevelopmentCharges));

				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE270);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_10100))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_1260)));
				
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_1000));

				break;

			case PURPOSE_CPRS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE270);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_10100))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_1260)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_1000));
				break;
			case PURPOSE_CICS:

				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE1);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_100))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_158))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_1260)));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_1000));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE1);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_100))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_158))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_1260)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_1000));
				break;
			case PURPOSE_RHP:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_158));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
				break;
			}
			break;
//---------------------------------HIGH 1----------------------------------------------------//
		case ZONE_HIG1: {
			switch (calculatorRequest.getPurposeCode()) {

			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE375));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_1050));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE167));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_80));
				feesTypeCalculationDto
						.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE_190));

				break;
			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE40));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE460));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE).multiply(PERCENTAGE075));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(PERCENTAGE075));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(RATE375).multiply(PERCENTAGE075));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
				break;

			case PURPOSE_MLU_CZ:

				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE250));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE01));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(RATE375).multiply(RATE2));

				break;

			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(RATE104.multiply(RATE09).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE460)
								.multiply(RATE5)).divide(RATE7));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_125));
//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE270));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE270));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE270));
				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE235);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE235);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE270);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_125))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE270);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE09).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_125))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));

				break;
			case PURPOSE_RHP:
				break;
			}
			break;
		}

		// ------------------------------HIGH 2-----------------------------//
		case ZONE_HIG2: {
			switch (calculatorRequest.getPurposeCode()) {

			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE95));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE375));

				break;

			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_1050));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_1050));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE167));

				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_80));
				feesTypeCalculationDto
						.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE_190));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE19));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE460));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE950).multiply(PERCENTAGE075));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(PERCENTAGE075));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(RATE375).multiply(PERCENTAGE075));

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_125));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
				break;

			case PURPOSE_MLU_CZ:

				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE01));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(RATE375).multiply(RATE2));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE19).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_125));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE460)
								.multiply(RATE5)).divide(RATE7));
				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_125));
//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE210));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE210));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE210));
				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE14000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE14000);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE210);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_125))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE210);

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE07).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_125))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_1050)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_750));

				break;

			case PURPOSE_RHP:
				break;
			}
			break;

		}

		// ---------------------medium--------------------///
		case ZONE_MDM:

		{
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE625));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_80));
				feesTypeCalculationDto
						.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE_250));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE62500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_600));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_125));

				break;
			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE62500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_600));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_125));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE62500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE167));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_50));
				feesTypeCalculationDto
						.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE_125));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE95));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE4));

				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_80));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE320));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE10000));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(PERCENTAGE075));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_80));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_80));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE01));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_80));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(RATE_250).multiply(RATE2));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE95).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_80));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE320)
								.multiply(RATE5)).divide(RATE7));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_80));

//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE95));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE95));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE95));
				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE6250);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE4));

				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_50))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_600)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_500));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE6250);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE4));

				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_50))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_600)));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_500));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE95);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_50))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_600)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_500));

				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE95);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE06).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_50))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_80))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_600)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_500));

				break;
			case PURPOSE_RHP:
				break;
			}
			break;

		}

		// -----------------low1----------------------//
		case ZONE_LOW: {
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(new BigDecimal(10)));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE70));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE35));

				break;

			case PURPOSE_IPULP:
				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE35));

				break;
			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE167));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_30));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE35));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE90));

				break;
			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE10000));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(PERCENTAGE075));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_20));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE01));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(RATE70).multiply(RATE2));

				break;
			case PURPOSE_NILP:

				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE90)
								.multiply(RATE5)).divide(RATE7));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_20));

//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE19));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE19));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE19));
				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(
						areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(new BigDecimal(10)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_30))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_30))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;

			case PURPOSE_RHP:
				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE19);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_30))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_20))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE19);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE05).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_30))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_20))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			}

			break;
		}

		/// ------------------low2-------------------------------//
		case ZONE_LOW2: {
			switch (calculatorRequest.getPurposeCode()) {
			case PURPOSE_RPL:

				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));

				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE125));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE70));

				break;
			case PURPOSE_ITP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE35));

				break;
			case PURPOSE_ITC:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(PERCENTAGE25).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));

				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE334));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_150));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE35));

				break;
			case PURPOSE_IPULP:
				break;

			case PURPOSE_IPA:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE12500));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE167));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_30));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(areaInSqmtr(arce).multiply(RATE35));

				break;

			case PURPOSE_RGP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(PERCENTAGE0995).multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE90));

				break;

			case PURPOSE_DDJAY_APHP:
				feesTypeCalculationDto.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE10000));
				feesTypeCalculationDto
						.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(PERCENTAGE075));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);

				break;
			case PURPOSE_NILPC:
				break;
			case PURPOSE_TODGH:
				break;

			case PURPOSE_AGH:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE0);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04));
				feesTypeCalculationDto.setConversionChargesCal(areaInSqmtr(arce).multiply(RATE_20));

				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(RATE0);
				break;

			case PURPOSE_MLU_CZ:
				break;

			case PURPOSE_LDEF:
				feesTypeCalculationDto.setScrutinyFeeChargesCal((RATE2).multiply(RATE10).multiply(areaInSqmtr(arce)));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE01));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(RATE70).multiply(RATE2));

				break;
			case PURPOSE_NILP:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(PERCENTAGE125));
				feesTypeCalculationDto.setLicenseFeeChargesCal(arce.multiply(RATE25).multiply(RATE5).divide(RATE7, 0));
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_20));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						(areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE90)
								.multiply(RATE5)).divide(RATE7));

				break;
			case PURPOSE_TODCOMM:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(RATE10).multiply(scrutinyFeeCharges));
				feesTypeCalculationDto.setConversionChargesCal(RATE2.multiply(areaInSqmtr(arce)).multiply(RATE_20));

//				feesTypeCalculationDto.setScrutinyFeeChargesCal(totalFar.multiply(RATE19));
//				feesTypeCalculationDto.setLicenseFeeChargesCal(totalFar.multiply(RATE19));
//				feesTypeCalculationDto.setConversionChargesCal(totalFar.multiply(RATE19));
				break;
			case PURPOSE_TODIT:
				break;
			case PURPOSE_TODMUD:
				break;
			case PURPOSE_CPCS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_30))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			case PURPOSE_CPRS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE4));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PERCENTAGE1).multiply(RATE_30))
						.add(areaInSqmtr(arce).multiply(PERCENTAGE2).multiply(RATE_150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));

				break;
			case PURPOSE_RHP:
				break;
			case PURPOSE_CIRS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE19);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_30))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_20))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
				break;
			case PURPOSE_CICS:
				feesTypeCalculationDto
						.setScrutinyFeeChargesCal(areaInSqmtr(arce).multiply(scrutinyFeeCharges).multiply(RATE10));
				feesTypeCalculationDto.setLicenseFeeChargesCal(RATE19);
				feesTypeCalculationDto.setExternalDevelopmentChargesCal(arce.multiply(RATE104).multiply(RATE04).multiply(RATE467));
				feesTypeCalculationDto.setConversionChargesCal((areaInSqmtr(arce).multiply(PART1).multiply(RATE_30))
						.add(areaInSqmtr(arce).multiply(PART2).multiply(RATE_20))
						.add(areaInSqmtr(arce).multiply(PART3).multiply(RATE_150)));
				feesTypeCalculationDto.setStateInfrastructureDevelopmentChargesCal(
						areaInSqmtr(arce).multiply(stateInfrastructureDevelopmentCharges).multiply(RATE_190));
				break;
			}

			break;
		}
		}

		return feesTypeCalculationDto;

	}

}