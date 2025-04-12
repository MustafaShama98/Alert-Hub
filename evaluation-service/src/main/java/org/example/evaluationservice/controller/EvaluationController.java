package org.example.evaluationservice.controller;


import org.example.evaluationservice.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * EvaluationController is responsible for handling requests related to evaluations.
 * It provides endpoints for creating, updating, and retrieving evaluations.
 */
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private EvaluationService evaluationService;

    @Autowired
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }
    /**
     * Find the Developer with the Most Occurrences of a Specific Label Within a Specific Time Frame.
     *
     * @param label The label for which you want to retrieve the task count.
     * @param since The number of days before the current date for which you want to check the task count.
     * @return Retrieves the developer with the highest number of tasks associated with a specific label.
     */
    @GetMapping("developer/most-label")
    public String getHighestLabel(@RequestParam String label, @RequestParam String since) {
        return evaluationService.getHighestLabel(label, Integer.parseInt(since)).toString();
    }

   /**
     * Returns the count of tasks associated with each label for the specified developer
     *
     * @param developerId The ID of the developer whose label counts you want to retrieve.
     * @param since     The number of days before the current date for which you want to check the task count.
     * @return the highest label for the specified developer
     */

    @GetMapping("developer/most-label/{developerId}/label-aggregate")
    public String getHighestLabelForDeveloper(@PathVariable Integer developerId, @RequestParam String since) {
        return "Most Label for Developer";
    }

    //Get the Total Number of Tasks Assigned to a Specified Developer Within a Specific Time Frame.
    @GetMapping("developer/most-label/{developerId}/task-amount")
    public String getTaskCountForDeveloper(@PathVariable Integer developerId, @RequestParam String since) {
        return "Task Count for Developer";
    }
}
