/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advintic.integrator.module.yanbu;

/**
 *
 * @author Mahmoud
 */
import com.advintic.integrator.module.yanbu.Worklist;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface WorklistDao extends CrudRepository<Worklist, Integer> {

    boolean exists(int worklistId);

    Worklist findById(int worklistId);
    Worklist findByAccessionNumber(String accessionNumber);
    
     List<Worklist> findByHandled(int handled);
    
     
    @Modifying
    @Query("UPDATE Worklist w SET w.handled = :handled "
            + " WHERE w.id = :worklistId")
    public Integer setHandled(@Param("worklistId") Integer worklistId,@Param("handled") int handled);

    @Modifying
    @Query("UPDATE Worklist w SET w.worklistStatus = :worklistStatus "
            + " WHERE w.id = :worklistId")
    public Integer setWorklistStatus(@Param("worklistId") Integer worklistId, @Param("worklistStatus") String worklistStatus);

    
     @Modifying
    @Query("UPDATE Worklist w SET w.examCompleted = 1 "
            + " WHERE w.id = :worklistId")
    public Integer setExamCompleted(@Param("worklistId") Integer worklistId);
}
