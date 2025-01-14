package org.egov.tl.abm.repo;

import java.util.ArrayList;
import java.util.List;

import org.egov.tl.abm.newservices.contract.NewBankGuaranteeContract;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.builder.NewBankGuaranteeQueryBuilder;
import org.egov.tl.repository.rowmapper.BankGuaranteeAuditRowMapper;
import org.egov.tl.repository.rowmapper.NewBankGuaranteeRowMapper;
import org.egov.tl.web.models.bankguarantee.BankGuaranteeSearchCriteria;
//import org.egov.tl.web.models.bankguarantee.BankGuaranteeSearchCriteria;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class NewBankGuaranteeRepo {

	@Autowired
	private Producer producer;
	@Autowired
	private TLConfiguration tlConfiguration;
	@Autowired
	private NewBankGuaranteeQueryBuilder newBankGuaranteeQueryBuilder;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NewBankGuaranteeRowMapper rowMapper;
	@Autowired
	private BankGuaranteeAuditRowMapper bankGuaranteeAuditRowMapper;

	public void save(NewBankGuaranteeContract newBankGuaranteeContract) {
		producer.push(tlConfiguration.getSaveNewBankGuaranteeTopic(), newBankGuaranteeContract);
	}
	
	public void update(NewBankGuaranteeContract newBankGuaranteeContract) {
		producer.push(tlConfiguration.getUpdateNewBankGuaranteeTopic(), newBankGuaranteeContract);
	}
	
	public List<NewBankGuaranteeRequest> getNewBankGuaranteeData(
			BankGuaranteeSearchCriteria bankGuaranteeSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = newBankGuaranteeQueryBuilder.getNewBankGuaranteeSearchQuery(bankGuaranteeSearchCriteria,
				preparedStmtList);
		log.info("query inside method getNewBankGuaranteeData:" + query);
		log.info("prepareStmtList:" + preparedStmtList);
		List<NewBankGuaranteeRequest> newBankGuaranteeData = jdbcTemplate.query(query, preparedStmtList.toArray(),
				rowMapper);
		return newBankGuaranteeData;
	}
	
	public List<NewBankGuaranteeRequest> getBankGuaranteeAuditEntries(String applicationNumber) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = newBankGuaranteeQueryBuilder.getBankGuaranteeAuditSearchQuery(applicationNumber,
				preparedStmtList);
		List<NewBankGuaranteeRequest> bankGuaranteeAuditData = jdbcTemplate.query(query, preparedStmtList.toArray(),
				bankGuaranteeAuditRowMapper);
		return bankGuaranteeAuditData;
	}
}
