package org.egov.tlcalculator.service;

import java.math.BigDecimal;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCalculationResponse {
	 
    private ResponseInfo responseInfo;
    private List<FeesTypeCalculationDto> feesTypeCalculationDto;
    private BigDecimal totalFee;
	public BigDecimal getTotalFee() {
		return feesTypeCalculationDto.get(0).getTotalFee();
		//return this.totalFee;
	}

}
