package org.example.processorservice.service;

import org.example.processorservice.dto.ActionDTO;
import org.example.processorservice.dto.MetricsDTO;
import org.example.processorservice.dto.NotificationMessageDTO;
import org.example.processorservice.dto.PlatformInformationDTO;
import org.example.processorservice.feignClient.LoaderServiceClient;
import org.example.processorservice.feignClient.MetricServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessorService {

    private final MetricServiceClient metricClient;
    private final LoaderServiceClient loaderClient;
    private final KafkaProducerService notificationProducer;

    @Autowired
    public ProcessorService(MetricServiceClient metricClient, LoaderServiceClient loaderServiceClient, KafkaProducerService notificationProducer) {
        this.metricClient = metricClient;
        this.loaderClient = loaderServiceClient;
        this.notificationProducer = notificationProducer;
    }

    public MetricsDTO sendMetricUpdate(Long id, MetricsDTO metrics) {
        return metricClient.updateMetric(id, metrics).getBody();
    }

    public MetricsDTO getMetricById(Long id) {
        return metricClient.getMatricById(id).getBody();
    }

    public List<MetricsDTO> getMetricsByUser(String userId) {
        return metricClient.getByUserId(userId).getBody();
    }

    public List<MetricsDTO> fetchAllMetrics() {
        return metricClient.getAllMetrics().getBody();

    }
    public MetricsDTO deleteMetric(Long id) {
        return metricClient.deleteMetric(id).getBody();
    }

    public List<PlatformInformationDTO> getInformation() {
        return loaderClient.getInfoData().getBody();
    }

    /**
     * Manually trigger an action process, bypassing all predefined conditions
     * @param  The ID of the action to trigger
     * @return Boolean indicating if the trigger was successful
     */
    public boolean manuallyTriggerAction(ActionDTO action) {
        try {
            // Create the notification topic name based on action type
            String topic = action.getActionType().toLowerCase() + "-topic";
            
            // Create notification message
            NotificationMessageDTO notificationMessageDTO = NotificationMessageDTO.builder()
                    .type("ACTION")
                    .topic(topic)
                    .user(NotificationMessageDTO.user.builder()
                            .email(action.getTo())
                            .userId(action.getUserId())
                            .build())
                    .message(action.getMessage())
                    .build();
            
            // Send notification to the appropriate topic
            notificationProducer.sendNotification(topic, notificationMessageDTO);
            
            return true;
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error manually triggering action: " + e.getMessage());
            return false;
        }
    }
}