package com.zarta.inventory.service.impl;

import com.zarta.domain.dto.DlqMessage;
import com.zarta.domain.dto.OrderAvro;
import com.zarta.inventory.model.Payment;
import com.zarta.inventory.repository.MasterItemRepository;
import com.zarta.inventory.repository.PaymentRepository;
import com.zarta.inventory.service.InventoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Value("${topic.dlq.name}")
    private String dlqTopic;

    private final MasterItemRepository masterItemRepository;
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, DlqMessage> template;

    public InventoryServiceImpl(
            MasterItemRepository masterItemRepository, PaymentRepository paymentRepository,
            KafkaTemplate<String, DlqMessage> template) {
        this.masterItemRepository = masterItemRepository;
        this.paymentRepository = paymentRepository;
        this.template = template;
    }

    @KafkaListener(topics = "${topic.name}")
    public void saveInventory(ConsumerRecord<String, OrderAvro> consumerRecord) {
        try {
            OrderAvro orderAvro = consumerRecord.value();
            orderAvro.getItems().forEach(e -> {
                masterItemRepository.findByKode(e.getKode().toString()).ifPresent(item -> {
                    int stock = item.getQty() - e.getQty();
                    item.setQty(stock);
                    masterItemRepository.save(item);
                });
            });

            Payment payment = new Payment();
            int totalPay = orderAvro.getItems().stream().mapToInt(e -> e.getPrice() * e.getQty()).sum();
            payment.setTotal(totalPay);
            payment.setOrderId(orderAvro.getId().longValue());
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);
        } catch (Exception e) {
            DlqMessage dlqMessage = DlqMessage.newBuilder()
                    .setOriginalTopic(consumerRecord.topic())
                    .setOriginalKey(consumerRecord.key())
                    .setError(e.getMessage())
                    .setTimestamp(System.currentTimeMillis())
                    .build();

            template.send(dlqTopic, consumerRecord.key(), dlqMessage);
        }
    }
}
