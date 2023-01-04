package org.egov.tl.abm.newservices.controller;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.abm.newservices.contract.NewBankGuaranteeContract;
import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.service.BankGuaranteeService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank")
public class BankGuaranteeController {

	@Autowired private ResponseInfoFactory responseInfoFactory;
	@Autowired BankGuaranteeService bankGuaranteeService;
	
	@PostMapping("/guarantee/_create")
	public ResponseEntity<NewBankGuaranteeResponse> create(@RequestBody NewBankGuaranteeContract newBankGuaranteeContract){
		
		NewBankGuarantee newBankguarantee = bankGuaranteeService.createAndUpdate(newBankGuaranteeContract);
		List<NewBankGuarantee> newBankGuaranteeList = new ArrayList<NewBankGuarantee>();
		newBankGuaranteeList.add(newBankguarantee);
		NewBankGuaranteeResponse newBankGuaranteeResponse = NewBankGuaranteeResponse.builder().newBankGuaranteeList(newBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(newBankGuaranteeContract.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(newBankGuaranteeResponse, HttpStatus.OK);	
	}
	
	
	
	/*
	@PostMapping("/renew/_create")
	public ResponseEntity<RenewBankGuaranteeResponse> renewCreate(@RequestBody RenewBankGuaranteeContract renewBankGuaranteeContract){
		
		RenewBankGuarantee renewBankguarantee = bankGuaranteeService.createRenewBankGuarantee(renewBankGuaranteeContract);
		List<RenewBankGuarantee> renewBankGuaranteeList = new ArrayList<RenewBankGuarantee>();
		renewBankGuaranteeList.add(renewBankguarantee);
		RenewBankGuaranteeResponse renewBankGuaranteeResponse = RenewBankGuaranteeResponse.builder().renewBankGuarantee(renewBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(renewBankGuaranteeContract.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(renewBankGuaranteeResponse, HttpStatus.OK);	
	}
	
	
	@PostMapping("/renew/_search")
	public ResponseEntity<RenewBankGuaranteeResponse> renewSearch(@RequestBody RequestInfoWrapper requestInfoWrapper){
		
		RenewBankGuarantee renewBankguarantee = bankGuaranteeService.searchRenewBankGuarantee(requestInfoWrapper.getRenewBankGuaranateeId());
		List<RenewBankGuarantee> renewBankGuaranteeList = new ArrayList<RenewBankGuarantee>();
		renewBankGuaranteeList.add(renewBankguarantee);
		RenewBankGuaranteeResponse renewBankGuaranteeResponse = RenewBankGuaranteeResponse.builder().renewBankGuarantee(renewBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(renewBankGuaranteeResponse, HttpStatus.OK);	
	}
	
	@PostMapping("/release/_create")
	public ResponseEntity<ReleaseBankGuaranteeResponse> releaseBankCreate(@RequestBody ReleaseBankGuaranteeContract releaseBankGuaranteeContract){
		
		ReleaseBankGuarantee releaseBankGuarantee = bankGuaranteeService.createReleaseBankGuarantee(releaseBankGuaranteeContract);
		List<ReleaseBankGuarantee> releaseBankGuaranteeList = new ArrayList<ReleaseBankGuarantee>();
		releaseBankGuaranteeList.add(releaseBankGuarantee);
		ReleaseBankGuaranteeResponse releaseBankGuaranteeResponse = ReleaseBankGuaranteeResponse.builder().releaseBankGuarantees(releaseBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(releaseBankGuaranteeContract.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(releaseBankGuaranteeResponse, HttpStatus.OK);	
	}
	
	@PostMapping("/release/_search")
	public ResponseEntity<ReleaseBankGuaranteeResponse> releaseSearch(@RequestBody RequestInfoWrapper requestInfoWrapper){
		
		ReleaseBankGuarantee releaseBankGuarantee = bankGuaranteeService.searchReleaseBankGuarantee(requestInfoWrapper.getReleaseBankId());
		List<ReleaseBankGuarantee> releaseBankGuaranteeList = new ArrayList<ReleaseBankGuarantee>();
		releaseBankGuaranteeList.add(releaseBankGuarantee);
		ReleaseBankGuaranteeResponse releaseBankGuaranteeResponse = ReleaseBankGuaranteeResponse.builder().releaseBankGuarantees(releaseBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(releaseBankGuaranteeResponse, HttpStatus.OK);	
	}
	
	
	@PostMapping("/replace/_create")
	public ResponseEntity<ReplaceBankGuaranteeResponse> replaceBankCreate(@RequestBody ReplaceBankGuaranteeContract replaceBankGuaranteeContract){
		
		ReplaceBankGuarantee replaceBankGuarantee = bankGuaranteeService.createReplaceBankGuarantee(replaceBankGuaranteeContract);
		List<ReplaceBankGuarantee> replaceBankGuaranteeList = new ArrayList<ReplaceBankGuarantee>();
		replaceBankGuaranteeList.add(replaceBankGuarantee);
		ReplaceBankGuaranteeResponse replaceBankGuaranteeResponse = ReplaceBankGuaranteeResponse.builder().replaceBankGuarantees(replaceBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(replaceBankGuaranteeContract.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(replaceBankGuaranteeResponse, HttpStatus.OK);	
	}
	
	@PostMapping("/replace/_search")
	public ResponseEntity<ReplaceBankGuaranteeResponse> replaceSearch(@RequestBody RequestInfoWrapper requestInfoWrapper){
		
		ReplaceBankGuarantee replaceBankGuarantee = bankGuaranteeService.searchReplaceBankGuarantee(requestInfoWrapper.getReplaceBankId());
		List<ReplaceBankGuarantee> replaceBankGuaranteeList = new ArrayList<ReplaceBankGuarantee>();
		replaceBankGuaranteeList.add(replaceBankGuarantee);
		ReplaceBankGuaranteeResponse replaceBankGuaranteeResponse = ReplaceBankGuaranteeResponse.builder().replaceBankGuarantees(replaceBankGuaranteeList)
											.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
											.build();
		
		return new ResponseEntity<>(replaceBankGuaranteeResponse, HttpStatus.OK);	
	}
	*/
	
	
	
}