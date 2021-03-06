
package com.job.lr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.job.lr.entity.Task;

public interface TaskDao extends PagingAndSortingRepository<Task, Long>, JpaSpecificationExecutor<Task> {

	Page<Task> findByUserId(Long id, Pageable pageRequest);

	@Modifying
	@Query("delete from Task task where task.user.id=?1")
	void deleteByUserId(Long id);
	
	@Query("from Task")
	Page<Task> pageAll(Pageable pageRequest);
	
	@Query("from Task t where t.auditFlag='1' and t.jobClass=?1 and t.jobSts='开放'")
	Page<Task> pageByClass(String jobClass, Pageable pageRequest);
	
	@Query("from Task t where t.auditFlag='1' and t.jobSts='开放'")
	Page<Task> pageAuditedTask(Pageable pageRequest);
}
