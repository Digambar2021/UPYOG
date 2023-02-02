package org.egov.tl.service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tl.abm.newservices.contract.NewBankGuaranteeContract;
import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.LandUtil;
import org.egov.tl.util.TLConstants;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.Transaction;

import org.egov.tl.web.models.UserResponse;
import org.egov.tl.web.models.UserSearchCriteria;

import org.egov.tl.web.models.UserType;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeRequest;
import org.egov.tl.web.models.calculation.CalulationCriteria;
import org.egov.tl.web.models.calculation.FeeAndBillingSlabIds;
import org.hibernate.internal.build.AllowSysOut;
import org.egov.tl.web.models.BankGuaranteeCalculationCriteria;
import org.egov.tl.web.models.CalculationRes;
import org.egov.tl.web.models.CalculatorRequest;
import org.egov.tl.web.models.GenerateLoiNumberResponse;
import org.egov.tl.web.models.GuranteeCalculatorResponse;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceRequest;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ResponseTransaction;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@Service
@Slf4j
public class LicenseService {

	@Value("${tcp.payment.host}")
	private String paymentHost;
	@Value("${citizen.payment.success}")
	private String paymentSuccess;
	@Value("${egov.pg-service.host}")
	private String pgHost;
	@Value("${egov.pg-service.path}")
	private String pgpath;
	@Value("${egov.pg-service.search.path}")
	private String pgSearchPath;
	@Value("${egov.tl.calculator.gurantee.endpoint}")
	private String guranteeEndPoint;
	@Value("${egov.tl.calculator.host}")
	private String guranteeHost;
	@Value("${egov.tl.calculator.calculate.endpoint}")
	private String calculatorEndPoint;
	@Value("${egov.user.host}")
	private String userHost;
	@Value("${egov.user.search.path}")
	private String userSearchPath;

	@Autowired
	LandUtil landUtil;

	@Autowired
	RestTemplate rest;

	@Autowired
	TLConfiguration config;

	@Autowired
	LandMDMSValidator valid;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ThirPartyAPiCall thirPartyAPiCall;
	@Autowired
	LicenseServiceRepo newServiceInfoRepo;
	@Autowired
	EntityManager em;
	private long id = 1;
	@Autowired
	TradeLicenseService tradeLicenseService;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	BankGuaranteeService bankGuaranteeService;

	@Transactional
	public LicenseServiceResponseInfo createNewServic(LicenseServiceRequest newServiceInfo)
			throws JsonProcessingException {

		LicenseServiceResponseInfo objLicenseServiceRequestInfo = new LicenseServiceResponseInfo();
		LicenseServiceDao newServiceIn = new LicenseServiceDao();
		List<LicenseDetails> newServiceInfoDatas = null;
		User user = newServiceInfo.getRequestInfo().getUserInfo();
		// if (newServiceInfo.getId() != null && newServiceInfo.getId() > 0) {
		TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();
		if (!StringUtils.isEmpty(newServiceInfo.getApplicationNumber())) {
			tradeLicenseRequest.setApplicationNumber(newServiceInfo.getApplicationNumber());
			List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest,
					newServiceInfo.getRequestInfo());
			for (TradeLicense tradeLicense : tradeLicenses) {

				ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
				});
				// newServiceIn = em.find(LicenseServiceDao.class, newServiceInfo.getId());
				try {
					newServiceInfoDatas = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// newServiceInfoDatas = newServiceIn.getNewServiceInfoData();
				float cv = tradeLicense.getTradeLicenseDetail().getCurrentVersion() + 0.1f;

				for (LicenseDetails newobj : newServiceInfoDatas) {

					if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {

						switch (newServiceInfo.getPageName()) {
						case "ApplicantInfo": {
							newobj.setApplicantInfo(newServiceInfo.getLicenseDetails().getApplicantInfo());
							break;
						}
						case "ApplicantPurpose": {
							newobj.setApplicantPurpose(newServiceInfo.getLicenseDetails().getApplicantPurpose());
							break;
						}
						case "LandSchedule": {
							newobj.setLandSchedule(newServiceInfo.getLicenseDetails().getLandSchedule());
							break;
						}
						case "DetailsofAppliedLand": {
							newobj.setDetailsofAppliedLand(
									newServiceInfo.getLicenseDetails().getDetailsofAppliedLand());
							break;
						}
						case "FeesAndCharges": {
							newobj.setFeesAndCharges(newServiceInfo.getLicenseDetails().getFeesAndCharges());
							break;
						}
						}

						newobj.setVer(cv);
						newServiceInfoDatas.add(newobj);
						newServiceIn.setNewServiceInfoData(newServiceInfoDatas);
						break;
					}
				}
				String data = mapper.writeValueAsString(newServiceInfoDatas);
				JsonNode jsonNode = mapper.readTree(data);
				// tradeLicense.setAuditDetails(null);
				tradeLicense.getTradeLicenseDetail().setAdditionalDetail(jsonNode);
				newServiceIn.setTenantId(newServiceInfo.getRequestInfo().getUserInfo().getTenantId());
				newServiceIn.setUpdatedDate(new Date());
				// newServiceIn.setApplicationStatus(tradeLicense.getStatus());
				newServiceIn.setApplicationNumber(tradeLicense.getApplicationNumber());
				newServiceIn.setUpdateddBy(newServiceInfo.getRequestInfo().getUserInfo().getUuid());
				newServiceIn.setCurrentVersion(cv);
				tradeLicense.getTradeLicenseDetail().setCurrentVersion(cv);
				tradeLicense.setAction(newServiceInfo.getAction());
				tradeLicense.setWorkflowCode("NewTL");
				switch (tradeLicense.getAction()) {
				case "INITIATE": {
					tradeLicense.setStatus("INITIATED");
					break;
				}
				case "PURPOSE": {
					tradeLicense.setStatus("PURPOSE");
					break;
				}
				case "LANDSCHEDULE": {
					tradeLicense.setStatus("LANDSCHEDULE");
					break;
				}
				case "LANDDETAILS": {
					tradeLicense.setStatus("LANDDETAILS");
					break;
				}
				case "FEESANDCHARGES": {
					tradeLicense.setStatus("FEESANDCHARGES");
					break;
				}
				case "PAID": {
					tradeLicense.setStatus("PAID");
					break;
				}
				}

				// tradeLicense.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));

				TradeLicenseRequest tradeLicenseRequests = new TradeLicenseRequest();

				tradeLicenseRequests.addLicensesItem(tradeLicense);
				tradeLicenseRequests.setRequestInfo(newServiceInfo.getRequestInfo());
				tradeLicenseService.update(tradeLicenseRequests, "TL");
			}
		}

		// }
		else {
			newServiceInfoDatas = new ArrayList<>();
			// newServiceIn = new LicenseServiceDao();
			newServiceIn.setCreatedBy(newServiceInfo.getRequestInfo().getUserInfo().getUuid());
			newServiceIn.setCreatedDate(new Date());
			newServiceIn.setUpdatedDate(new Date());
			newServiceIn.setTenantId(newServiceInfo.getRequestInfo().getUserInfo().getTenantId());

			newServiceInfo.getLicenseDetails().setVer(0.1f);
			newServiceIn.setUpdateddBy(newServiceInfo.getRequestInfo().getUserInfo().getUuid());
			newServiceInfoDatas.add(newServiceInfo.getLicenseDetails());
			newServiceIn.setNewServiceInfoData(newServiceInfoDatas);
			newServiceIn.setCurrentVersion(0.1f);

			TradeLicenseRequest request = new TradeLicenseRequest();
			request.setRequestInfo(newServiceInfo.getRequestInfo());

			TradeLicense tradeLicense = new TradeLicense();

			TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
			tradeLicense.setId(String.valueOf(newServiceInfo.getId()));
			// tradeLicense.setStatus(newServiceInfo.getApplicationStatus());
			tradeLicense.setAction(newServiceInfo.getAction());
			tradeLicense.setApplicationDate(new Date().getTime());
			// tradeLicense.getApplicationNumber();
			tradeLicense.setApplicationType(TradeLicense.ApplicationTypeEnum.NEW);
			// tradeLicense.getAssignee();
			// tradeLicense.getAuditDetails();
			tradeLicense.setBusinessService("TL");
			// tradeLicense.getCalculation();
			// tradeLicense.getComment();
			// tradeLicense.getFileStoreId();
			tradeLicense.setFinancialYear("2022-23");
			tradeLicense.setIssuedDate(new Date().getTime());
			// tradeLicense.setStatus("INITIATED");
			switch (tradeLicense.getAction()) {
			case "INITIATE": {
				tradeLicense.setStatus("INITIATED");
				break;
			}
			case "PURPOSE": {
				tradeLicense.setStatus("PURPOSE");
				break;
			}
			case "LANDSCHEDULE": {
				tradeLicense.setStatus("LANDSCHEDULE");
				break;
			}
			case "LANDDETAILS": {
				tradeLicense.setStatus("LANDDETAILS");
				break;
			}
			case "FEESANDCHARGES": {
				tradeLicense.setStatus("FEESANDCHARGES");
				break;
			}
			case "PAID": {
				tradeLicense.setStatus("PAID");
				break;
			}

			}

			// tradeLicense.getLicenseNumber();
			tradeLicense.setLicenseType(TradeLicense.LicenseTypeEnum.PERMANENT);
			tradeLicense.setTenantId(newServiceInfo.getRequestInfo().getUserInfo().getTenantId());
			if (newServiceInfo.getLicenseDetails().getApplicantPurpose() != null)
				tradeLicense.setTradeName(newServiceInfo.getLicenseDetails().getApplicantPurpose().getPurpose());

			tradeLicense.setAccountId(newServiceInfo.getRequestInfo().getUserInfo().getUuid());

//			tradeLicense.setValidFrom();
//			tradeLicense.setValidTo();
//			tradeLicense.setWfDocuments();
			tradeLicense.setWorkflowCode("NewTL");

			tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
			tradeLicenseDetail.setId(String.valueOf(newServiceInfo.getId()));
			tradeLicenseDetail.getAdditionalDetail();
			tradeLicenseDetail.getApplicationDocuments();
			tradeLicenseDetail.getChannel();
			tradeLicenseDetail.getOwners();
			tradeLicenseDetail.getVerificationDocuments();
			tradeLicenseDetail.setTradeType("NewTL");
			tradeLicenseDetail.setCurrentVersion(newServiceIn.getCurrentVersion());

			String data = mapper.writeValueAsString(newServiceInfoDatas);
			JsonNode jsonNode = mapper.readTree(data);
			tradeLicenseDetail.setAdditionalDetail(jsonNode);

			request.addLicensesItem(tradeLicense);
			List<TradeLicense> tradelicenses = tradeLicenseService.create(request, TLConstants.businessService_TL);
			// request.getLicenses().clear();
			request.setLicenses(tradelicenses);
			// tradeLicenseService.update(request, TLConstants.businessService_TL);
			newServiceIn.setApplicationNumber(tradelicenses.get(0).getApplicationNumber());
			newServiceIn.setApplicationStatus(tradeLicense.getStatus());
			// objLicenseServiceRequestInfo.setApplication_Status();
		}
		// newServiceIn = newServiceInfoRepo.save(newServiceIn);
		try {
			BeanUtils.copyProperties(objLicenseServiceRequestInfo, newServiceIn);

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		objLicenseServiceRequestInfo.setBusinessService(TLConstants.businessService_TL);
		return objLicenseServiceRequestInfo;
	}

	private String genrateTransactionNUmber(User user) {
		Map<String, Object> authtoken = new HashMap<String, Object>();
		Map<String, Object> mapTNum = new HashMap<String, Object>();

		authtoken.put("UserId", user.getId());
		authtoken.put("TpUserId", user.getId());
		authtoken.put("EmailId", user.getEmailId());

		mapTNum.put("UserLoginId", user.getId());
		mapTNum.put("TpUserId", user.getId());
		mapTNum.put("EmailId", user.getEmailId());
		mapTNum.put("MobNo", user.getMobileNumber());

		String transactionNumber;

		transactionNumber = thirPartyAPiCall.generateTransactionNumber(mapTNum, authtoken).getBody().get("Value")
				.toString();
		log.info("TransactionNumber\t" + transactionNumber);
		return transactionNumber;
	}

	public LicenseServiceResponseInfo getNewServicesInfoById1(String applicationNumber, RequestInfo info) {
		LicenseServiceResponseInfo licenseServiceResponseInfo = new LicenseServiceResponseInfo();
		LicenseServiceDao newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		try {
			BeanUtils.copyProperties(licenseServiceResponseInfo, newServiceInfo);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return licenseServiceResponseInfo;
	}

	public LicenseServiceResponseInfo getNewServicesInfoById(String applicationNumber, RequestInfo info) {
		LicenseServiceResponseInfo licenseServiceResponseInfo = new LicenseServiceResponseInfo();
		// LicenseServiceDao newServiceInfo = new
		// LicenseServiceDao();//newServiceInfoRepo.findByAppNumber(applicationNumber);

		TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();
		tradeLicenseRequest.setApplicationNumber(applicationNumber);

		List<LicenseDetails> newServiceInfoData = null;
		List<LicenseDetails> licenseDetails = new ArrayList<LicenseDetails>();
		List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest, info);
		licenseServiceResponseInfo.setWorkFlowCode(tradeLicenses.get(0).getWorkflowCode());
		for (TradeLicense tradeLicense : tradeLicenses) {

			ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
			});

			Map<String, Object> authtoken = new HashMap<String, Object>();
			authtoken.put("UserId", "39");
			authtoken.put("TpUserId", "12356");
			authtoken.put("EmailId", "mkthakur84@gmail.com");

			// List<LicenseDetails> newServiceInfoData = null;
			try {
				newServiceInfoData = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// LicenseServiceDao newServiceIn = em.find(LicenseServiceDao.class,
			// applicationNumber);

			// newServiceInfoData = newServiceIn.getNewServiceInfoData();

			for (LicenseDetails newobj : newServiceInfoData) {

				if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {
					licenseDetails.add(newobj);
					licenseServiceResponseInfo.setNewServiceInfoData(licenseDetails);
					licenseServiceResponseInfo.setApplicationStatus(tradeLicense.getStatus());
					licenseServiceResponseInfo.setApplicationNumber(applicationNumber);
					licenseServiceResponseInfo.setBusinessService(tradeLicense.getBusinessService());
					licenseServiceResponseInfo.setCaseNumber(tradeLicense.getTcpCaseNumber());
					licenseServiceResponseInfo.setDairyNumber(tradeLicense.getTcpDairyNumber());

					break;
					// licenseServiceResponseInfo.setNewServiceInfoData(newServiceInfoData);
				}
			}
		}

		return licenseServiceResponseInfo;
	}

	public List<LicenseServiceDao> getNewServicesInfoAll() {
		return newServiceInfoRepo.findAll();
	}

	public List<String> getApplicantsNumber() {
		return this.newServiceInfoRepo.getApplicantsNumber();
	}

	public ResponseEntity<Object> postTransactionDeatil(MultiValueMap<String, String> requestParam) {

		// String applicationNumber = requestParam.get(new
		// String("Applicationnumber")).get(0);
		String transactionId = requestParam.get(new String("Applicationnumber")).get(0);
		String grn = requestParam.get(new String("GRN")).get(0);
		String status = requestParam.get(new String("status")).get(0);
		String CIN = requestParam.get(new String("CIN")).get(0);
		String tdate = requestParam.get(new String("tdate")).get(0);
		String paymentType = requestParam.get(new String("payment_type")).get(0);
		String bankcode = requestParam.get(new String("bankcode")).get(0);
		String amount = requestParam.get(new String("amount")).get(0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.now();
		String date = formatter.format(localDateTime);
		String dairyNumber;
		String caseNumber;
		String applicationNmber;
		String saveTransaction;
		String returnURL = "";
		RequestInfo info = new RequestInfo();

		if (!status.isEmpty() && status.equalsIgnoreCase("Success")) {

			Map<String, Object> request = new HashMap<>();
			request.put("txnId", transactionId);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, httpHeaders);
			Object paymentSearch = null;

			String uri = pgHost + pgSearchPath;
			paymentSearch = rest.postForObject(uri, entity, Map.class);
			log.info("search payment data" + paymentSearch);

			String data = null;
			try {
				data = mapper.writeValueAsString(paymentSearch);
			} catch (JsonProcessingException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

			ResponseTransaction transaction = null;
			ObjectReader reader = mapper.readerFor(new TypeReference<ResponseTransaction>() {
			});
			try {
				transaction = reader.readValue(data);
			} catch (IOException e) {

				e.printStackTrace();
			}

			log.info("transaction" + transaction);
			String txnId = transaction.getTransaction().get(0).getTxnId();
			String applicationNumber = transaction.getTransaction().get(0).getApplicationNumber();

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

			params.put("eg_pg_txnid", Collections.singletonList(txnId));

			String paymentUrl = paymentHost + paymentSuccess + "TL" + "/" + applicationNumber + "/" + "hr";
			String returnPaymentUrl = paymentUrl + "?" + params;
			log.info("returnPaymentUrl" + returnPaymentUrl);
			httpHeaders.setLocation(
					UriComponentsBuilder.fromHttpUrl(returnPaymentUrl.toString()).build().encode().toUri());
			return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);

		} else if (!status.isEmpty() && status.equalsIgnoreCase("Failure")) {

			// --------------payment--------------//
			Map<String, Object> request = new HashMap<>();
			request.put("txnId", transactionId);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, httpHeaders);
			Object paymentSearch = null;

			String uri = pgHost + pgSearchPath;
			paymentSearch = rest.postForObject(uri, entity, Map.class);
			log.info("search payment data" + paymentSearch);

			String data = null;
			try {
				data = mapper.writeValueAsString(paymentSearch);
			} catch (JsonProcessingException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

			ResponseTransaction transaction = null;
			ObjectReader objectReader = mapper.readerFor(new TypeReference<ResponseTransaction>() {
			});
			try {
				transaction = objectReader.readValue(data);
			} catch (IOException e) {

				e.printStackTrace();
			}

			log.info("transaction" + transaction);
			String txnId = transaction.getTransaction().get(0).getTxnId();
			String applicationNumber = transaction.getTransaction().get(0).getConsumerCode();
			String uuid = transaction.getTransaction().get(0).getUser().getUuid();
			String tennatId = transaction.getTransaction().get(0).getUser().getTenantId();
			String mobileNumber = transaction.getTransaction().get(0).getUser().getMobileNumber();

			UserSearchCriteria userSearchCriteria = new UserSearchCriteria();

			userSearchCriteria.setMobileNumber(mobileNumber);
			userSearchCriteria.setTenantId(tennatId);

			StringBuilder url = new StringBuilder(userHost);
			url.append(userSearchPath);

			Object searchUser = serviceRequestRepository.fetchResult(url, userSearchCriteria);

			log.info("searchUsers" + searchUser);
			String data1 = null;

			try {
				data1 = mapper.writeValueAsString(searchUser);
			} catch (JsonProcessingException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
			UserResponse userData = null;
			ObjectReader readerData = mapper.readerFor(new TypeReference<UserResponse>() {
			});
			try {
				userData = readerData.readValue(data1);
			} catch (IOException e) {

				e.printStackTrace();
			}
			log.info("userData" + userData);

			String type = userData.getUser().get(0).getType().toString();
			String email = userData.getUser().get(0).getEmailId();
			Long userId = userData.getUser().get(0).getId();
			String mobNo = userData.getUser().get(0).getMobileNumber();

			List<Role> roles = new ArrayList<>();			
			int length =userData.getUser().get(0).getRoles().size();
			for (int i = 0; i < length; i++) {
				Role role = new Role();
				try {
				role.setCode(userData.getUser().get(0).getRoles().get(i).getCode());
				role.setTenantId(userData.getUser().get(0).getRoles().get(i).getTenantId());
				role.setName(userData.getUser().get(0).getRoles().get(i).getName());
				}catch(NullPointerException e) {
					e.printStackTrace();
				}
				roles.add(role);
			}

			User user = new User();

			user.setType(type);
			user.setUuid(uuid);
			user.setTenantId(tennatId);
			user.setRoles(roles);
			info.setUserInfo(user);

			// ------------------payment end----------------//
			TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();
			tradeLicenseRequest.setApplicationNumber(applicationNumber);
			List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest, info);

			for (TradeLicense tradeLicense : tradeLicenses) {

				ObjectReader reader = mapper.readerFor(new TypeReference<List<LicenseDetails>>() {
				});

				Map<String, Object> authtoken = new HashMap<String, Object>();
				authtoken.put("UserId", "39");
				authtoken.put("TpUserId", userId);
				authtoken.put("EmailId", "mkthakur84@gmail.com");

				List<LicenseDetails> newServiceInfoData = null;
				try {
					newServiceInfoData = reader.readValue(tradeLicense.getTradeLicenseDetail().getAdditionalDetail());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// LicenseServiceDao newServiceIn = em.find(LicenseServiceDao.class,
				// applicationNumber);

				// newServiceInfoData = newServiceIn.getNewServiceInfoData();

				for (LicenseDetails newobj : newServiceInfoData) {

					if (newobj.getVer() == tradeLicense.getTradeLicenseDetail().getCurrentVersion()) {

						/****************
						 * Dairy Number End Here
						 ***********/
						LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>> mDMSCallPurposeId = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Object>>>) landUtil
								.mDMSCallPurposeCode(info, tradeLicense.getTenantId(),
										newobj.getApplicantPurpose().getPurpose());

						Map<String, List<String>> mdmsData;
						mdmsData = valid.getAttributeValues(mDMSCallPurposeId);

						List<Map<String, Object>> msp = (List) mdmsData.get("Purpose");

						int purposeId = 0;

						for (Map<String, Object> mm : msp) {

							purposeId = Integer.valueOf(String.valueOf(mm.get("purposeId")));
							log.info("purposeId" + purposeId);

						}

						Map<String, Object> mapDNo = new HashMap<String, Object>();

						mapDNo.put("Village",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapDNo.put("DiaryDate", date);
						mapDNo.put("ReceivedFrom", "");
						mapDNo.put("UserId", "1234");
						mapDNo.put("DistrictCode",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
						mapDNo.put("UserLoginId", "39");
						dairyNumber = thirPartyAPiCall.generateDiaryNumber(mapDNo, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpDairyNumber(dairyNumber);

						/****************
						 * End Here
						 ***********/
						// case number
						Map<String, Object> mapCNO = new HashMap<String, Object>();
						mapCNO.put("DiaryNo", dairyNumber);
						mapCNO.put("DiaryDate", date);
						mapCNO.put("DeveloperId", 2);
						mapCNO.put("PurposeId", purposeId);
						mapCNO.put("StartDate", date);
						mapCNO.put("DistrictCode",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
						mapCNO.put("Village",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapCNO.put("ChallanAmount", newobj.getFeesAndCharges().getPayableNow());
						mapCNO.put("UserId", "2");
						mapCNO.put("UserLoginId", "39");
						caseNumber = thirPartyAPiCall.generateCaseNumber(mapCNO, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpCaseNumber(caseNumber);

						/****************
						 * End Here
						 ***********/
						// application number
						Map<String, Object> mapANo = new HashMap<String, Object>();
						mapANo.put("DiaryNo", dairyNumber);
						mapANo.put("DiaryDate", date);
						mapANo.put("TotalArea", newobj.getFeesAndCharges().getTotalArea());
						mapANo.put("Village",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getRevenueEstate());
						mapANo.put("PurposeId", purposeId);
						mapANo.put("NameofOwner",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
						mapANo.put("DateOfHearing", date);
						mapANo.put("DateForFilingOfReply", date);
						mapANo.put("UserId", "2");
						mapANo.put("UserLoginId", "39");
						applicationNmber = thirPartyAPiCall.generateApplicationNumber(mapANo, authtoken).getBody()
								.get("Value").toString();
						tradeLicense.setTcpApplicationNumber(applicationNumber);

						/****************
						 * End Here
						 ***********/
						/****************
						 * starttransaction data
						 ********/
						Map<String, Object> map3 = new HashMap<String, Object>();
						map3.put("UserName", "tcp");
						map3.put("EmailId", "mkthakur84@gmail.com");
						map3.put("MobNo", mobNo);
						map3.put("TxnNo", grn);
						map3.put("TxnAmount", newobj.getFeesAndCharges().getPayableNow());
						map3.put("NameofOwner",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getLandOwner());
						map3.put("LicenceFeeNla", newobj.getFeesAndCharges().getLicenseFee());
						map3.put("ScrutinyFeeNla", newobj.getFeesAndCharges().getScrutinyFee());
						map3.put("UserId", "39");
						map3.put("UserLoginId", "39");
						map3.put("TpUserId", "12356");
						// TODO Renu to Add these two vaues
						map3.put("PaymentMode", paymentType);
						map3.put("PayAgreegator", bankcode);

						map3.put("LcApplicantName", "tcp");
						map3.put("LcPurpose", newobj.getApplicantPurpose().getPurpose());
						// to do select development plan
						map3.put("LcDevelopmentPlan",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDevelopmentPlan());
						map3.put("LcDistrict",
								newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getDistrict());
						saveTransaction = thirPartyAPiCall.saveTransactionData(map3, authtoken).getBody().get("Value")
								.toString();
						tradeLicense.setTcpSaveTransactionNumber(saveTransaction);

						/****************
						 * End Here
						 ***********/

						tradeLicense.setAction("PAID");
						tradeLicense.setWorkflowCode("NewTL");
						tradeLicense.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));

						TradeLicenseRequest tradeLicenseRequests = new TradeLicenseRequest();

						tradeLicenseRequests.addLicensesItem(tradeLicense);
						tradeLicenseRequests.setRequestInfo(info);
						tradeLicenseService.update(tradeLicenseRequests, "TL");

						// -----------------payment----------------------//
						MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

						params.put("eg_pg_txnid", Collections.singletonList(txnId));

						String paymentUrl = paymentHost + paymentSuccess + tradeLicense.getBusinessService() + "/"
								+ applicationNumber + "/" + tradeLicense.getTenantId();
						String returnPaymentUrl = paymentUrl + "?" + params;
						log.info("returnPaymentUrl" + returnPaymentUrl);
						httpHeaders.setLocation(
								UriComponentsBuilder.fromHttpUrl(returnPaymentUrl.toString()).build().encode().toUri());
						return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);

						// ------------------finish--------------------//
						/*
						 * try { String payment =
						 * rest.postForObject(config.getPgHost().concat(config.getPgPath()),
						 * requestParam, String.class); log.info("responses" + payment); } catch
						 * (Exception e) {
						 * 
						 * }
						 */

					}
				}
			}

		} else if (!status.isEmpty() && status.equalsIgnoreCase("Error")) {

		}

		MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(returnURL).build().getQueryParams();

		HttpHeaders httpHeaders = new HttpHeaders();

		StringBuilder redirectURL = new StringBuilder();
		redirectURL.append(returnURL);

		httpHeaders.setLocation(UriComponentsBuilder.fromHttpUrl(redirectURL.toString()).queryParams(requestParam)
				.build().encode().toUri());
		return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);

	}

	public LicenseServiceDao findNewServicesInfoById(String applicationNumber) {

		LicenseServiceDao newServiceInfo = newServiceInfoRepo.getOne(id);
		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
			}
		}
		return newServiceInfo;
	}

	public LicenseServiceDao findByLoiNumber(String loiNumber) {
		return this.newServiceInfoRepo.findByLoiNumber(loiNumber);
	}

	public boolean existsByLoiNumber(String loiNumber) {
		return this.newServiceInfoRepo.existsByLoiNumber(loiNumber);
	}

	public boolean existsByApplicationNumber(String applicationNumber) {
		return this.newServiceInfoRepo.existsByApplicationNumber(applicationNumber);
	}

	public List<TradeLicense> generateLoiNumber(Map<String, Object> map, @Valid RequestInfoWrapper requestInfoWrapper,
			String applicationNo) {
		TradeLicense tradeLicense = new TradeLicense();
		String applicationNumber = applicationNo;
		TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();

		tradeLicenseRequest.setApplicationNumber(applicationNumber);
		List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest,
				requestInfoWrapper.getRequestInfo());

		tradeLicenses.get(0).getTenantId();

		TradeLicenseRequest tradeLicenseRequests = new TradeLicenseRequest();
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setId(tradeLicenses.get(0).getTradeLicenseDetail().getId());
		tradeLicenseDetail.setAdditionalDetail(tradeLicenses.get(0).getTradeLicenseDetail().getAdditionalDetail());

		// ---------------------loi number start-------------------//
		String dispatchNumber;
		Map<String, Object> depAuthtoken = new HashMap<String, Object>();
		depAuthtoken.put("UserId", "169");
		depAuthtoken.put("EmailId", "dtp.panchkula.tcp@gmail.com");

		Map<String, Object> mapDispatchNumber = new HashMap<String, Object>();
		mapDispatchNumber.put("DispatchID", 0);
		mapDispatchNumber.put("Subject", "Testing Information");
		mapDispatchNumber.put("DoctypeID", "220");
		mapDispatchNumber.put("TblApplicationId", "0");
		mapDispatchNumber.put("CreatedBy", "169");
		mapDispatchNumber.put("DispatchedTo", "District-Gurugram, Haryana");
		mapDispatchNumber.put("Type", "1");
		mapDispatchNumber.put("FileId", "eOffice_DiaryID-66731");
		mapDispatchNumber.put("ReceivedFrom", "");
		mapDispatchNumber.put("RelatedDiaryNo", "");
		mapDispatchNumber.put("Description", "");
		dispatchNumber = thirPartyAPiCall.getDispatchNumber(mapDispatchNumber, depAuthtoken).getBody().toString();
		String loiNumber = dispatchNumber.replaceAll("\\\\", "");
		log.info("dispatch" + loiNumber);
		loiNumber = loiNumber.substring(1, loiNumber.length() - 1);

		List<GenerateLoiNumberResponse> generateLoiNumberResponse = null;
		ObjectReader objectReader = mapper.readerFor(new TypeReference<List<GenerateLoiNumberResponse>>() {
		});
		try {

			generateLoiNumberResponse = objectReader.readValue(loiNumber);
		} catch (IOException e) {

			e.printStackTrace();
		}

		String dispatchNo = generateLoiNumberResponse.get(0).getDispatchNo();

		// -----------dispatch number finish-----------------------//

		// --------------------bank gurantee calculator start-------------------------//
		JsonNode estimate = tradeLicenses.get(0).getTradeLicenseDetail().getAdditionalDetail();

		BankGuaranteeCalculationCriteria calculatorRequest = new BankGuaranteeCalculationCriteria();

		calculatorRequest.setApplicationNumber(applicationNo);
		calculatorRequest.setPotentialZone(
				estimate.get(0).get("ApplicantPurpose").get("AppliedLandDetails").get(0).get("potential").textValue());
		// calculatorRequest.setPotentialZone("HYP");
		calculatorRequest.setPurposeCode(estimate.get(0).get("ApplicantPurpose").get("purpose").textValue());
		calculatorRequest.setTotalLandSize(new BigDecimal("1"));
		calculatorRequest.setRequestInfo(requestInfoWrapper.getRequestInfo());
		calculatorRequest.setTenantId(tradeLicenses.get(0).getTenantId());

		StringBuilder url = new StringBuilder(guranteeHost);
		url.append(guranteeEndPoint);
		Object responseGuranteeEstimate = serviceRequestRepository.fetchResult(url, calculatorRequest);
		String data = null;
		try {
			data = mapper.writeValueAsString(responseGuranteeEstimate);
		} catch (JsonProcessingException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		GuranteeCalculatorResponse guranteeCalculatorResponse = null;
		ObjectReader reader = mapper.readerFor(new TypeReference<GuranteeCalculatorResponse>() {
		});
		try {

			guranteeCalculatorResponse = reader.readValue(data);
		} catch (IOException e) {

			e.printStackTrace();
		}
		String bankGuaranteeForEDC = guranteeCalculatorResponse.getBankGuaranteeForEDC();
		String bankGuaranteeForIDW = guranteeCalculatorResponse.getBankGuaranteeForIDW();
		// --------------------bank gurantee calculator end-------------------------//

		// ---------------------create bank gurantee request------------------------//
		NewBankGuaranteeContract newBankGuaranteeContract = new NewBankGuaranteeContract();
		newBankGuaranteeContract.setRequestInfo(requestInfoWrapper.getRequestInfo());
		List<NewBankGuaranteeRequest> bankGuaranteeRequest = new ArrayList<>();
		NewBankGuaranteeRequest newBankGuaranteeRequest = new NewBankGuaranteeRequest();
		newBankGuaranteeRequest.setAction("PRE_SUBMIT");
		newBankGuaranteeRequest.setLoiNumber(dispatchNo);
		String edcType = "EDC";
		String idwType = "IDW";

		if (edcType == "EDC") {
			newBankGuaranteeRequest.setTypeOfBg(edcType);
			newBankGuaranteeRequest.setAmountInFig(new BigDecimal(bankGuaranteeForEDC));
		} else {
			newBankGuaranteeRequest.setTypeOfBg(idwType);
			newBankGuaranteeRequest.setAmountInFig(new BigDecimal(bankGuaranteeForIDW));
		}

		newBankGuaranteeRequest.setTenantId(tradeLicenses.get(0).getTenantId());
		bankGuaranteeRequest.add(newBankGuaranteeRequest);
		newBankGuaranteeContract.setNewBankGuaranteeRequest(bankGuaranteeRequest);
		List<NewBankGuarantee> newBankGuaranteeList = bankGuaranteeService
				.createNewBankGuarantee(newBankGuaranteeContract);
		log.info("newBankGuaranteeList" + newBankGuaranteeList);
		String bankGuranteeApplicationNo = newBankGuaranteeList.get(0).getApplicationNumber();

		// --------------------------crate bank gurantee end---------------------//

		// --------------------------calculation--------------------------------//
		StringBuilder calculatorUrl = new StringBuilder(guranteeHost);
		calculatorUrl.append(calculatorEndPoint);

		List<CalulationCriteria> calulationCriteria = new ArrayList<>();
		CalulationCriteria calulationCriteriaRequest = new CalulationCriteria();
		calulationCriteriaRequest.setTenantId(tradeLicenses.get(0).getTenantId());
		calulationCriteria.add(calulationCriteriaRequest);

		CalculatorRequest calculator = new CalculatorRequest();
		calculator.setApplicationNumber(applicationNo);
		calculator.setPotenialZone(
				estimate.get(0).get("ApplicantPurpose").get("AppliedLandDetails").get(0).get("potential").textValue());
		// calculator.setPotenialZone("HYP");
		calculator.setPurposeCode(estimate.get(0).get("ApplicantPurpose").get("purpose").textValue());
		calculator.setTotalLandSize("1");

		Map<String, Object> calculatorMap = new HashMap<>();
		calculatorMap.put("CalulationCriteria", calulationCriteria);
		calculatorMap.put("CalculatorRequest", calculator);
		calculatorMap.put("RequestInfo", requestInfoWrapper.getRequestInfo());
		Object responseCalculator = serviceRequestRepository.fetchResult(calculatorUrl, calculatorMap);
		log.info("responseCalculator" + responseCalculator);

		String data1 = null;
		try {
			data = mapper.writeValueAsString(responseCalculator);
		} catch (JsonProcessingException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		CalculationRes calculationRes = null;
		ObjectReader objectReaders = mapper.readerFor(new TypeReference<CalculationRes>() {
		});
		try {

			calculationRes = objectReaders.readValue(data);
		} catch (IOException e) {

			e.printStackTrace();
		}
		FeeAndBillingSlabIds charges = calculationRes.getCalculations().get(0).getTradeTypeBillingIds();

		tradeLicenseDetail.setScrutinyFeeCharges(charges.getScrutinyFeeCharges());
		tradeLicenseDetail.setLicenseFeeCharges(charges.getLicenseFeeCharges());
		tradeLicenseDetail.setExternalDevelopmentCharges(charges.getExternalDevelopmentCharges());
		tradeLicenseDetail.setConversionCharges(charges.getConversionCharges());
		tradeLicenseDetail.setStateInfrastructureDevelopmentCharges(charges.getStateInfrastructureDevelopmentCharges());

		// --------------------------calculation end--------------------------------//

		tradeLicense.setId(tradeLicenses.get(0).getId());
		tradeLicense.setLoiNumber(dispatchNo);
		tradeLicense.setAction("APPROVE");
		tradeLicense.setWorkflowCode("NewTL");
		tradeLicense.setTenantId(tradeLicenses.get(0).getTenantId());
		tradeLicense.setApplicationNumber(tradeLicenses.get(0).getApplicationNumber());
		tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseRequests.addLicensesItem(tradeLicense);
		tradeLicenseRequests.setRequestInfo(requestInfoWrapper.getRequestInfo());

		List<TradeLicense> response = tradeLicenseService.update(tradeLicenseRequests, "TL");

		return response;
	}

}