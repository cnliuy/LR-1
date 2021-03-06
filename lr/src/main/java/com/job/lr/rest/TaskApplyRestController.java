package com.job.lr.rest;

import java.net.URI;

import javax.validation.Validator;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springside.modules.beanvalidator.BeanValidators;
import org.springside.modules.web.MediaTypes;

import com.job.lr.entity.GeneralResponse;
import com.job.lr.entity.TaskApplyRecord;
import com.job.lr.entity.User;
import com.job.lr.service.account.ShiroDbRealm.ShiroUser;
import com.job.lr.service.task.TaskApplyService;


@RestController
@RequestMapping(value = "/api/v1/taskApply")
public class TaskApplyRestController {
	

	private static Logger logger = LoggerFactory.getLogger(TaskRestController.class);

	@Autowired
	TaskApplyService applyService;

	@Autowired
	private Validator validator;
	
	/**
	 * 根据Id查询申请
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public TaskApplyRecord get(@PathVariable("id") Long id) {
		TaskApplyRecord apply = applyService.getApply(id);
		if (apply == null) {
			String message = "申请不存在(id:" + id + ")";
			logger.warn(message);
			throw new RestException(HttpStatus.NOT_FOUND, message);
		}
		return apply;
	}

	
	/**
	 * 根据任务Id分页查询申请列表
	 * @param taskId
	 * @param pageNum
	 * @return
	 */
	@RequestMapping(value = "/pageByTaskId/{taskId}_{pageNum}",method = RequestMethod.GET) 
	public Page<TaskApplyRecord> pageByTaskId (@PathVariable("taskId") Long taskId,
			@PathVariable("pageNum") int pageNum){
		return applyService.findPageByTaskId(taskId,pageNum);
	}
	
	/**
	 * 根据兼职任务ID和申请状态分页查询申请列表
	 * @param taskId
	 * @param sts
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/pageByTaskAndSts/{taskId}_{sts}_{pageNum}_{pageSize}",
			method = RequestMethod.GET,
			produces = MediaTypes.JSON_UTF_8) 
	public Page<TaskApplyRecord> pageByTaskAndSts (@PathVariable("taskId") Long taskId,
			@PathVariable("sts") String sts,
			@PathVariable("pageNum") int pageNum,
			@PathVariable("pageSize") int pageSize){
		return applyService.pageByTaskAndSts(taskId, sts, pageNum, pageSize);
	}
	
	/**
	 * 根据发起申请的用户的ID和申请状态分页查询申请列表
	 * @param userId
	 * @param sts
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/pageByUserAndSts/{userId}_{sts}_{pageNum}_{pageSize}",
			method = RequestMethod.GET,
			produces = MediaTypes.JSON_UTF_8) 
	public Page<TaskApplyRecord> pageByUserAndSts (@PathVariable("userId") Long userId,
			@PathVariable("sts") String sts,
			@PathVariable("pageNum") int pageNum,
			@PathVariable("pageSize") int pageSize){
		return applyService.pageByUserAndSts(userId, sts, pageNum, pageSize);
	}
	
	
	/**
	 * 通过向URL发起GET请求建立任务申请记录
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/create/{taskId}",method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public GeneralResponse createApply(@PathVariable("taskId") Long taskId) {
		
		GeneralResponse response;
		
		TaskApplyRecord entity = new TaskApplyRecord();
		entity.setTaskId(taskId);
		
		User user = new User();
		user.setId(((ShiroUser) SecurityUtils.getSubject().getPrincipal()).id);
		entity.setUser(user);
		
		response = applyService.createApply(entity);
	
		return response;
	}
	
	/**
	 * 兼职申请者取消报名申请
	 * @param applyId
	 * @return
	 */
	@RequestMapping(value = "/cancel/{applyId}",method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public GeneralResponse cancelApply(@PathVariable("applyId") Long applyId) {
		
		GeneralResponse response = applyService.cancelApply(applyId);
		
		return response;
	}
	
	/**
	 * 兼职任务发布者确认报名申请
	 * @param applyId
	 * @return
	 */
	@RequestMapping(value = "/confirm/{applyId}",method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public GeneralResponse confirmApply(@PathVariable("applyId") Long applyId) {
		
		GeneralResponse response = applyService.confirmApply(applyId);
		
		return response;
	}
	
	/**
	 * 兼职任务发布者确认申请者已经到岗
	 * @param applyId
	 * @return
	 */
	@RequestMapping(value = "/comeToPositon/{applyId}",method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public GeneralResponse comeToPositon(@PathVariable("applyId") Long applyId) {
		
		GeneralResponse response = applyService.comeToPosition(applyId);
		
		return response;
	}
	
	/**
	 * 兼职任务发布者确认兼职任务已经付款支付
	 * @param applyId
	 * @return
	 */
	@RequestMapping(value = "/confirmPayment/{applyId}",method = RequestMethod.GET, produces = MediaTypes.JSON_UTF_8)
	public GeneralResponse confirmPayment(@PathVariable("applyId") Long applyId) {
		
		GeneralResponse response = applyService.confirmPayment(applyId);
		
		return response;
	}
	
	/**
	 * 通过向URL提交REST对象建立任务申请记录
	 * @param apply
	 * @param uriBuilder
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaTypes.JSON)
	public ResponseEntity<?> create(@RequestBody TaskApplyRecord apply, UriComponentsBuilder uriBuilder) {
		// 调用JSR303 Bean Validator进行校验, 异常将由RestExceptionHandler统一处理.
		BeanValidators.validateWithException(validator, apply);

		// 保存任务
		applyService.createApply(apply);

		// 按照Restful风格约定，创建指向新任务的url, 也可以直接返回id或对象.
		Long id = apply.getId();
		URI uri = uriBuilder.path("/api/v1/apply/" + id).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);

		return new ResponseEntity(headers, HttpStatus.CREATED);
	}

}
