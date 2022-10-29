package org.egov.user.abm.developer.repo;



import org.egov.user.abm.developer.contract.DeveloperRegistration;
import org.egov.user.abm.developer.contract.Developerdetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperRegistrationRepo extends JpaRepository<DeveloperRegistration, Long>{

	public boolean existsById(Long id);
	public DeveloperRegistration findById(Long id);
	
	
	// @Query(value = "SELECT a.* FROM author a "
    //+ "WHERE CAST(a.book ->> 'price' AS INTEGER) = ?1",
    // nativeQuery = true)
	
	@Query(value = "SELECT lt FROM DeveloperRegistration lt WHERE CAST(lt.developerDetail.devDetail.addRemoveAuthoizedUsers.mobileNumber->>'mobileNumber' as STRING) = ?1", nativeQuery = true)
	public Developerdetail findAuthorizedUser(String mobileNumber);
}