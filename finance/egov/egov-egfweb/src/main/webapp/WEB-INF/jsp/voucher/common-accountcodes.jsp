<%@ page contentType="text/json"%><%@ taglib prefix="s"
	uri="/WEB-INF/tags/struts-tags.tld"%><s:iterator var="s"
	value="accountCodesForDetailTypeList" status="status">
	<s:property value="%{glcode}" />`-`<s:property value="%{name}" />`~`<s:property
		value="%{id}" />
	<s:if test="%{chartOfAccountDetails.size()>0}">`-`true</s:if>+</s:iterator>
^
