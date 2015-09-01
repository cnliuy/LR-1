
package com.job.lr.web.task;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springside.modules.web.Servlets;

import com.google.common.collect.Maps;
import com.job.lr.entity.Task;
import com.job.lr.entity.TaskComment;
import com.job.lr.entity.User;
import com.job.lr.rest.ControllerUtil;
import com.job.lr.service.account.ShiroDbRealm.ShiroUser;
import com.job.lr.service.task.TaskCommentService;
import com.job.lr.service.task.TaskService;

/**
 * Task管理的Controller, 使用Restful风格的Urls:
 * 
 * List page : GET /task/
 * Create page : GET /task/create
 * Create action : POST /task/create
 * Update page : GET /task/update/{id}
 * Update action : POST /task/update
 * Delete action : GET /task/delete/{id}
 * Upload File page : GET /task/uploadfile/{id}
 * @author calvin
 */
@Controller
@RequestMapping(value = "/task")
public class TaskController {

	private static final String PAGE_SIZE = "3";

	private static Map<String, String> sortTypes = Maps.newLinkedHashMap();
	static {
		sortTypes.put("auto", "自动");
		sortTypes.put("title", "标题");
	}

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private TaskCommentService commentService;

	@RequestMapping(method = RequestMethod.GET)
	public String list(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "page.size", defaultValue = PAGE_SIZE) int pageSize,
			@RequestParam(value = "sortType", defaultValue = "auto") String sortType, Model model,
			ServletRequest request) {
		
		
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		Long userId = getCurrentUserId();

		Page<Task> tasks = taskService.getUserTask(userId, searchParams, pageNumber, pageSize, sortType);

		model.addAttribute("tasks", tasks);
		model.addAttribute("sortType", sortType);
		model.addAttribute("sortTypes", sortTypes);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", Servlets.encodeParameterStringWithPrefix(searchParams, "search_"));

		return "task/taskList";
	}
	
	@RequestMapping(value = "viewComment/{taskId}", method = RequestMethod.GET)
	public String viewComment(@PathVariable("taskId") Long taskId, 
			@RequestParam(value = "page", defaultValue = "1") int pageNum, 
			Model model,
			ServletRequest request) {
		
		Page<TaskComment> comments = this.commentService.findPageByTaskId(taskId, pageNum);
		
		model.addAttribute("comments", comments);
		model.addAttribute("pageNum",pageNum);
		return "task/listComment";
	}
	

	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String createForm(Model model) {
		model.addAttribute("task", new Task());
		model.addAttribute("action", "create");
		return "task/taskForm";
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String create(@Valid Task newTask, RedirectAttributes redirectAttributes) {
		System.out.println(" i am in here Task()create()");
		User user = new User(getCurrentUserId());
		newTask.setUser(user);

		taskService.createTask(newTask);
		redirectAttributes.addFlashAttribute("message", "创建任务成功");
		return "redirect:/task/";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("task", taskService.getTask(id));
		model.addAttribute("action", "update");
		return "task/taskForm";
	}
	
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String update(@Valid @ModelAttribute("task") Task task, RedirectAttributes redirectAttributes) {
		taskService.saveTask(task);
		redirectAttributes.addFlashAttribute("message", "更新任务成功");
		return "redirect:/task/";
	}

	//转向到接收上传的文件页面 get   liuy
	@RequestMapping(value = "uploadfile/{id}", method = RequestMethod.GET)
	public String touploadfileForm(@PathVariable("id") Long id, Model model) {
		System.out.println("TaskController------------touploadfileForm()");
		model.addAttribute("task", taskService.getTask(id));
		model.addAttribute("action", "uploadfilehere");
		return "task/taskuploadfileForm";
	}

	
	//------------------------------
	//转向到接收上传的文件页面 post
	//独立的action ，不区分post和get 
	//------------------------------
	//@RequestMapping(value = "uploadfile/{id}", method = RequestMethod.POST)
	@RequestMapping(value = "/uploadfilehere")  //独立的action ，不区分post和get 
	//public String uploadfileForm(@PathVariable("id") Long id, Model model) {
	public	String uploadfileForm(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, ModelMap model) {  
	
		System.out.println("TaskController ----------here is uploadfileForm get  id=");
        String path = request.getSession().getServletContext().getRealPath("upload");
        System.out.println("path="+path);
        String fileName = file.getOriginalFilename();  
        //String fileName = new Date().getTime()+".jpg";  
        System.out.println(path);  
        File targetFile = new File(path, fileName);  
        if(!targetFile.exists()){  
            targetFile.mkdirs();  
        }  
        //保存  
        try {  
            file.transferTo(targetFile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  		
		
		//model.addAttribute("task", taskService.getTask(id));
		model.addAttribute("action", "uploadfile");
		return "redirect:/task/";
	}
	
	
	
	@RequestMapping(value = "delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		taskService.deleteTask(id);
		redirectAttributes.addFlashAttribute("message", "删除任务成功");
		return "redirect:/task/";
	}

	/**
	 * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
	 */
	@ModelAttribute
	public void getTask(@RequestParam(value = "id", defaultValue = "-1") Long id, Model model) {
		if (id != -1) {
			model.addAttribute("task", taskService.getTask(id));
		}
	}

	/**
	 * 取出Shiro中的当前用户Id.
	 */
	private Long getCurrentUserId() {		
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if (user== null){
			return 0L;
		}else{
			//System.out.println("user.id +user.id： "+user.id);
			return user.id;
		}	
	}
	

	
	
}
