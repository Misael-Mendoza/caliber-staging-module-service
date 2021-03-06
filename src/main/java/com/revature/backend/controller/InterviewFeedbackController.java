package com.revature.backend.controller;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.backend.model.Associate;
import com.revature.backend.model.InterviewFeedback;
import com.revature.backend.model.Manager;
import com.revature.backend.service.AssociateServiceImpl;
import com.revature.backend.service.InterviewFeedbackService;
import com.revature.backend.service.ManagerServiceImpl;

@RestController
@RequestMapping(value = "/feedback")
@CrossOrigin(origins = "*")
public class InterviewFeedbackController {
	
	@Autowired
	private InterviewFeedbackService ifServ;
	
	@Autowired
	private AssociateServiceImpl assocServ;
	
	@Autowired
	private ManagerServiceImpl managerServ;

	/**
	 * Return all feedback in the database
	 * 
	 * @return
	 */
	@GetMapping("/all")
	public ResponseEntity<List<InterviewFeedback>> getAllFeedback() {
		List<InterviewFeedback> appList = ifServ.retrieveAllFeedback();
		if(appList.size() == 0) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(appList, HttpStatus.OK);
		}
	}
	
	/**
	 * Return feedback with a certain id
	 * 
	 * @param id The id of the feedback that should be returned
	 * @return Returns the feedback if found, null if not found
	 */
	@GetMapping("/{id}")
	public ResponseEntity<InterviewFeedback> getFeedbackById(@PathVariable("id") int id) {
		InterviewFeedback feedback = ifServ.retrieveFeedbackById(id);
		if(feedback == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(feedback, HttpStatus.OK);
		}
	}
	
	/**
	 * Returns feedback of an associate, found using their id
	 * 
	 * @param id The id of the associate to return feedback for
	 * @return Returns all feedback for an associate if found, null if not found
	 */
	@GetMapping("/associate/{id}")
	public ResponseEntity<List<InterviewFeedback>> getFeedbackByAssociate(@PathVariable("id") int id) {
		Associate associate = assocServ.getAssociateById(id);
		List<InterviewFeedback> feedbackList = ifServ.retrieveFeedbackByAssociate(associate);
		if(feedbackList.size() == 0) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(feedbackList, HttpStatus.OK);
		}
	}
	
	/**
	 * Returns feedback that a manager has provided, found using their id
	 * 
	 * @param id The id of the manager to return feedback for
	 * @return Returns all feedback a manager has written, null if not found
	 */
	@GetMapping("/manager/{id}")
	public ResponseEntity<List<InterviewFeedback>> getFeedbackByManager(@PathVariable("id") int id) {
		Manager manager = managerServ.getManagerById(id);
		List<InterviewFeedback> feedbackList = ifServ.retrieveFeedbackByManager(manager);
		if(feedbackList.size() == 0) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(feedbackList, HttpStatus.OK);
		}
	}
	
	/**
	 * Creates a new feedback
	 * 
	 * @param feedbackMap Map of the information to input into the feedback
	 * @return Success or failure statement
	 */
	@PostMapping("/{id}")
	public ResponseEntity<String> postFeedback(@RequestBody LinkedHashMap feedbackMap) {
		Associate associate = assocServ.getAssociateById((Integer)feedbackMap.get("associateId"));
		if(associate == null) {
			return new ResponseEntity<>("Associate id is invalid", HttpStatus.NOT_FOUND);
		} 
		
		Manager manager = managerServ.getManagerById((Integer)feedbackMap.get("managerId"));
		if(manager == null) {
			return new ResponseEntity<>("Manager id is invalid", HttpStatus.NOT_FOUND);
		}
		
		String content = (String)feedbackMap.get("content");
		Timestamp date = new Timestamp(System.currentTimeMillis());
		
		InterviewFeedback feedback = new InterviewFeedback(associate, manager, content, date);
		ifServ.insertFeedback(feedback);
		return new ResponseEntity<>("Feedback successfully added", HttpStatus.OK);
	}
	
	/**
	 * Updates a feedback
	 * 
	 * @param id Id of the feedback to be updated
	 * @return Success or failure statement
	 */
	@PutMapping("/{id}")
	public ResponseEntity<String> updateFeedback(@RequestBody LinkedHashMap feedbackMap, @PathVariable("id") int id) {
		System.out.println("Feedback Map: " + feedbackMap);
		System.out.println("Manager Id: " + feedbackMap.get("managerId"));
		Associate associate = assocServ.getAssociateById((Integer)feedbackMap.get("associateId"));
		if(associate == null) {
			return new ResponseEntity<>("Associate id is invalid", HttpStatus.NOT_FOUND);
		} 
		
		Manager manager = managerServ.getManagerById((Integer)feedbackMap.get("managerId"));
		if(manager == null) {
			return new ResponseEntity<>("Manager id is invalid", HttpStatus.NOT_FOUND);
		} 
		
		InterviewFeedback feedback = ifServ.retrieveFeedbackById(id);
		if(feedback == null) {
			return new ResponseEntity<>("Feedback failed to be updated", HttpStatus.NOT_FOUND);
		} 
		
		else {
			String content = (String)feedbackMap.get("content");
			feedback.setAssociate(associate);
			feedback.setManager(manager);
			feedback.setContent(content);
			ifServ.updateFeedback(feedback);
			return new ResponseEntity<>("Feedback sucessfully updated", HttpStatus.OK);
		}
	}
	
	/**
	 * Deletes a feedback
	 * 
	 * @param id Id of the feedback to be deleted
	 * @return Success or failure statement
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteFeedback(@PathVariable("id") int id) {
		InterviewFeedback feedback = ifServ.retrieveFeedbackById(id);
		if(feedback == null) {
			return new ResponseEntity<>("Feedback failed to be deleted", HttpStatus.NOT_FOUND);
		} else {
			ifServ.deleteFeedback(feedback);
			return new ResponseEntity<>("Feedback sucessfully deleted", HttpStatus.OK);
		}
	}
}