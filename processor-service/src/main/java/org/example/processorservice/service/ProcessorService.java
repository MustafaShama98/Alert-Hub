package org.example.processorservice.service;

import org.example.processorservice.dto.MetricsDTO;
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

    @Autowired
    public ProcessorService(MetricServiceClient metricClient,LoaderServiceClient loaderServiceClient) {
        this.metricClient = metricClient;
        this.loaderClient = loaderServiceClient;
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
    public List<PlatformInformationDTO> getInformation(Long id) {
        return loaderClient.getInfoData().getBody();
    }

}