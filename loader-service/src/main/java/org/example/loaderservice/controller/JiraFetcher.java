package org.example.loaderservice.controller;

import org.example.loaderservice.dto.LoaderResponseDTO;
import org.example.loaderservice.repository.bean.PlatformInformation;
import org.example.loaderservice.service.PlatformInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/loader/developer")
public class JiraFetcher {
    @Autowired
    PlatformInformationService platformInformationService;

    @GetMapping("/jira")
    public List<PlatformInformation> getInfoData() {
        String rawTextUrl = "https://raw.githubusercontent.com/yones753/project_data_files/main/jira/jira_2024_08_22T13_30_00.csv";
        StringBuilder tmp = new StringBuilder();
        List<PlatformInformation> savedTasks = new ArrayList<>();
        try {
            URL url = new URL(rawTextUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            reader.readLine(); // skip first line
            String line;
            while ((line = reader.readLine()) != null) {
                // Split each line by comma, treating it as CSV
                System.out.println(line);
                String[] values = line.split(",", -1); // -1 to include empty strings
                for (String i : values)
                    System.out.println(i);

                // Map values to a Task object
                PlatformInformation task = new PlatformInformation();
                task.setTask_id(Integer.parseInt(values[0]));
                task.setManager_id(Integer.parseInt(values[1]));
                task.setProject(values[2]);
                task.setTag(values[3]);
                task.setLabel(values[4]);
                task.setDeveloper_id(Integer.parseInt(values[5]));
                task.setDeveloper_name(values[3]);
                task.setTask_number(values[6]);
                task.setEnvironment(values[7]);
                task.setUser_story(values[8]);
                task.setTask_point(Integer.parseInt(values[9]));
                task.setSprint(values[10]);
                // Save the task
                platformInformationService.addNewInfo(task);
                savedTasks.add(task);
            }

            reader.close();
            return savedTasks;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/most-label")
    public  ResponseEntity<LoaderResponseDTO> mostLabelDevelper(@RequestParam("label") String label,
                                                                @RequestParam("since") int sinceDays) {
        var topDeveloper = platformInformationService.mostLabelDevelper(label, sinceDays);
        return ResponseEntity.ok(topDeveloper);
    }

    @GetMapping("/{developer_id}/label-aggregate")
    public ResponseEntity<List<LoaderResponseDTO>> labelAggregate(@PathVariable String developer_id,
                                                           @RequestParam int since) {
        var topDeveloper = platformInformationService.labelAggregate(developer_id, since);
        return ResponseEntity.ok(topDeveloper);
    }

    @GetMapping("/{developer_id}/task-amount")
    public ResponseEntity<LoaderResponseDTO> taskAmount(@PathVariable String developer_id,
                                                    @RequestParam int since) {
        var topDeveloper = platformInformationService.taskAmount(developer_id, since);
        return ResponseEntity.ok(topDeveloper);
    }
}

