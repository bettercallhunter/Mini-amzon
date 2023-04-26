package org.mini_amazon.controllers;

import java.util.ArrayList;
import java.util.List;

import org.mini_amazon.errors.ServiceError;
import org.mini_amazon.models.Order;
import org.mini_amazon.models.Shipment;
import org.mini_amazon.proto.WorldAmazonProtocol;
import org.mini_amazon.services.ShipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ShipmentController {
  // UNCOMMENT ME!!!!!!!!!!!!!!
  // @Resource
  // private AmazonDaemon amazonDaemon;
  @Resource
  private ShipmentService shipmentService;

  record ShipmentRequest(int destinationX, int destinationY,
      List<OrderController.OrderRequest> orderRequests) {
  }

  @PostMapping("/placeShipment")
  public ResponseEntity<Shipment> placeShipment(@RequestBody ShipmentRequest request) throws ServiceError {
    Shipment shipment = shipmentService.createShipment(request.orderRequests(), request.destinationX(),
        request.destinationY());
    // UNCOMMENT ME!!!!!!!!!!!!!!
    // long seqNum = amazonDaemon.getSeqNum();
    List<WorldAmazonProtocol.AProduct> products = new ArrayList<>();
    for (Order order : shipment.getOrders()) {
      WorldAmazonProtocol.AProduct.Builder productBuilder = WorldAmazonProtocol.AProduct.newBuilder();
      productBuilder.setId(order.getItem().getId());
      productBuilder.setCount(order.getQuantity());
      productBuilder.setDescription(order.getItem().getDescription());
      products.add(productBuilder.build());
    }
    // amazonDaemon.sendBuyRequest(List.of(AMessageBuilder.createAPurchaseMore(shipment.getWarehouse().getId(),
    // products, seqNum)), seqNum);
    return ResponseEntity.ok().body(shipment);
  }

  record findShipmentRequest(long shipmentNumber) {
  }

  // need fixing
  @PostMapping("/findShipment")
  public ResponseEntity<?> findOrder(@RequestBody findShipmentRequest request) {
    long shipmentNumber = request.shipmentNumber();
    try {

      List<Order> orders = shipmentService.getOrdersByShipment(shipmentNumber);
      return ResponseEntity.ok().body(orders);
    } catch (Exception e) {
      String errorMessage = "Failed to retrieve order with shipmentNumber: " + shipmentNumber;
      return ResponseEntity.badRequest().body(errorMessage);
    }

  }

}
